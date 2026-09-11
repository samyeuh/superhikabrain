package com.samy.superhikabrain.manager;

import com.samy.superhikabrain.tasks.PrePlayingTask;
import com.samy.superhikabrain.utils.GameMessageUtils;
import com.samy.superhikabrain.utils.GameState;
import com.samy.superhikabrain.SuperHikabrain;
import com.samy.superhikabrain.tasks.StartingTask;
import com.samy.superhikabrain.utils.HikaTeam;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class GameManager {

    private GameState state;
    private final List<Player> players = new ArrayList<>();
    private final SuperHikabrain plugin;
    private final int maxPlayers;
    private final World waitingServer;
    private final World gameServer;
    private final HotbarManager hotbarManager;
    private final TeamManager teamManager;
    private final Set<Location> placedBlocks = new HashSet<>();
    private final Set<Location> minedBlocks = new HashSet<>();
    private final Map<UUID, BukkitTask> pendingRemovals = new HashMap<>();
    private static final long RECONNECT_GRACE_TICKS = 20L * 60 * 5; // 5 min
    private final File arenaStateFile;

    public GameManager(SuperHikabrain plugin) {
        this.state = GameState.WAITING;
        this.plugin = plugin;
        this.teamManager = new TeamManager(this);
        this.hotbarManager = new HotbarManager(this);
        this.maxPlayers = plugin.getConfig().getInt("max_players");
        this.gameServer = plugin.getServer().getWorld("game");
        this.waitingServer = plugin.getServer().getWorld("waiting");
        this.arenaStateFile = new File(plugin.getDataFolder(), "arena-state.yml");
        waitingServer.setSpawnLocation(54, 64, 0);

        setGameRules();

        // Le tracking des blocs posés/minés ne vit qu'en mémoire : un restart/reload
        // avant la fin d'une partie le perdait sans remettre l'arène à zéro. On
        // recharge ce qui a pu être sauvegardé et on nettoie tout de suite.
        loadArenaState();
        resetArena();
    }

    public void setGameRules() {
        // Waiting world
        if (waitingServer != null) {
            waitingServer.setPVP(false);
            waitingServer.setGameRuleValue("doDaylightCycle", "false");
            waitingServer.setGameRuleValue("doWeatherCycle", "false");
            waitingServer.setGameRuleValue("doMobSpawning", "false");
            waitingServer.setGameRuleValue("mobGriefing", "false");
            waitingServer.setGameRuleValue("keepInventory", "true");
            waitingServer.setGameRuleValue("doFireTick", "false");
        }

        // Game world
        if (gameServer != null) {
            gameServer.setPVP(true);
            gameServer.setGameRuleValue("doDaylightCycle", "false");
            gameServer.setGameRuleValue("doWeatherCycle", "false");
            gameServer.setGameRuleValue("doMobSpawning", "false");
            gameServer.setGameRuleValue("mobGriefing", "false");
            gameServer.setGameRuleValue("doTileDrops", "false");
            gameServer.setGameRuleValue("keepInventory", "false");
            gameServer.setGameRuleValue("doFireTick", "false");
        }
    }

    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public SuperHikabrain getPlugin() {
        return plugin;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public HotbarManager getHotbarManager() {
        return hotbarManager;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GameState getState() { return state; }

    public void joinPlayer(Player player) {
        if (pendingRemovals.containsKey(player.getUniqueId())) {
            reconnectPlayer(player);
            return;
        }

        if (state == GameState.WAITING) {
            player.teleport(waitingServer.getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.JUMP);

            this.addPlayer(player);
            Map<Integer, ItemStack> items = hotbarManager.getWaitingHotbar(player);
            items.forEach((slot, item) -> player.getInventory().setItem(slot, item));
        } else if (state == GameState.PLAYING || state == GameState.PREPLAYING) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(new Location(gameServer, -50, 16, 406.5, 180f, 0.0f));
        }
    }

    public void quitPlayer(Player player){
        if (!players.contains(player)) return;

        if (state == GameState.PREPLAYING || state == GameState.PLAYING) {
            disconnectPlayer(player);
            return;
        }

        this.removePlayer(player);
        if (state == GameState.STARTING) {
            state = GameState.WAITING;
        }
    }

    private void disconnectPlayer(Player player) {
        BukkitTask existing = pendingRemovals.remove(player.getUniqueId());
        if (existing != null) existing.cancel();

        sendMessageAll(GameMessageUtils.playerDisconnectedMessage(player.getName()));
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> finalizeDisconnect(player), RECONNECT_GRACE_TICKS);
        pendingRemovals.put(player.getUniqueId(), task);
    }

    private void finalizeDisconnect(Player player) {
        pendingRemovals.remove(player.getUniqueId());
        if (!players.contains(player)) return;

        this.removePlayer(player);
        if (state == GameState.PLAYING) {
            checkWinCondition();
        }
    }

    private void reconnectPlayer(Player player) {
        BukkitTask task = pendingRemovals.remove(player.getUniqueId());
        if (task != null) task.cancel();

        sendMessageAll(GameMessageUtils.playerReconnectedMessage(player.getName()));

        HikaTeam team = teamManager.getPlayerTeam(player);
        if (state == GameState.PLAYING && team != null && !team.isEliminated()) {
            teamManager.teleportPlayerToSpawn(player);
            Map<Integer, ItemStack> items = hotbarManager.getPlayingHotbar(player);
            items.forEach((slot, item) -> player.getInventory().setItem(slot, item));
            player.setGameMode(GameMode.SURVIVAL);
        } else {
            moveToSpectator(player);
        }
    }

    private void addPlayer(Player player) {
        players.add(player);
        String nbPlayers = players.size() + "/" + maxPlayers;
        this.sendMessageAll(GameMessageUtils.playerJoinMessage(player.getName(), nbPlayers));
        if (players.size() == maxPlayers) {
            this.startGame();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        teamManager.removePlayerFromTeam(player);
        String nbPlayers = players.size() + "/" + maxPlayers;
        this.sendMessageAll(GameMessageUtils.playerLeaveMessage(player.getName(), nbPlayers));
    }

    public void startGame() {
        state = GameState.STARTING;
        StartingTask task = new StartingTask(this);
        task.runTaskTimer(plugin, 0, 20);
    }

    public void trackPlacedBlock(Location location) {
        placedBlocks.add(location);
        saveArenaState();
    }

    public boolean isPlacedBlock(Location location) {
        return placedBlocks.contains(location);
    }

    public void untrackPlacedBlock(Location location) {
        placedBlocks.remove(location);
        saveArenaState();
    }

    public void trackMinedBlock(Location location) {
        minedBlocks.add(location);
        saveArenaState();
    }

    private void resetArena() {
        for (Location location : placedBlocks) {
            location.getBlock().setType(Material.AIR);
        }
        placedBlocks.clear();

        for (Location location : minedBlocks) {
            Block block = location.getBlock();
            block.setType(Material.SANDSTONE);
            block.setData((byte) 2);
        }
        minedBlocks.clear();

        saveArenaState();
        teamManager.resetLives();
    }

    private void saveArenaState() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("placed", serializeLocations(placedBlocks));
        config.set("mined", serializeLocations(minedBlocks));
        try {
            config.save(arenaStateFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Impossible de sauvegarder l'état de l'arène", e);
        }
    }

    private void loadArenaState() {
        if (!arenaStateFile.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(arenaStateFile);
        placedBlocks.addAll(deserializeLocations(config.getStringList("placed")));
        minedBlocks.addAll(deserializeLocations(config.getStringList("mined")));
    }

    private List<String> serializeLocations(Set<Location> locations) {
        List<String> serialized = new ArrayList<>();
        for (Location location : locations) {
            serialized.add(location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ());
        }
        return serialized;
    }

    private List<Location> deserializeLocations(List<String> serialized) {
        List<Location> locations = new ArrayList<>();
        for (String entry : serialized) {
            String[] parts = entry.split(";");
            World world = plugin.getServer().getWorld(parts[0]);
            if (world == null) continue;
            locations.add(new Location(world, Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])));
        }
        return locations;
    }

    public void preplayGame() {
        state = GameState.PREPLAYING;
        resetArena();
        teamManager.addPlayersToTeam();
        teamManager.teleportPlayers();
        PrePlayingTask task = new PrePlayingTask(this);
        players.forEach(player -> {
            Map<Integer, ItemStack> items = hotbarManager.getPlayingHotbar(player);
            items.forEach((slot, item) -> player.getInventory().setItem(slot, item));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 128, false, false));
            player.setGameMode(GameMode.SURVIVAL);
        });
        task.runTaskTimer(plugin, 0, 20);
    }

    public void playGame() {
        state = GameState.PLAYING;
        players.forEach(player -> {
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.JUMP);
        });
    }

    public void playerFall(Player p){
        if (state != GameState.PLAYING) return;

        HikaTeam team = teamManager.getPlayerTeam(p);
        if (team != null && team.isEliminated()) {
            moveToSpectator(p);
            return;
        }

        teamManager.teleportPlayerToSpawn(p);
        Map<Integer, ItemStack> items = hotbarManager.getPlayingHotbar(p);
        items.forEach((slot, item) -> p.getInventory().setItem(slot, item));
    }

    public void onBedStepped(HikaTeam team) {
        if (state != GameState.PLAYING || team.isEliminated()) return;

        team.loseLife();
        sendMessageAll(GameMessageUtils.getLifeLostMessage(team));

        if (team.isEliminated()) {
            eliminateTeam(team);
        }
    }

    private void eliminateTeam(HikaTeam team) {
        sendMessageAll(GameMessageUtils.getTeamEliminatedMessage(team));
        new ArrayList<>(team.getPlayers()).forEach(this::moveToSpectator);
        checkWinCondition();
    }

    private void moveToSpectator(Player p) {
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(new Location(gameServer, -50, 16, 406.5, 180f, 0.0f));
    }

    private void checkWinCondition() {
        if (state != GameState.PLAYING) return;

        List<HikaTeam> aliveTeams = new ArrayList<>();
        for (HikaTeam team : teamManager.getTeams()) {
            if (!team.isEliminated()) {
                aliveTeams.add(team);
            }
        }

        if (aliveTeams.size() <= 1) {
            endGame(aliveTeams.isEmpty() ? null : aliveTeams.get(0));
        }
    }

    private void endGame(HikaTeam winningTeam) {
        state = GameState.FINISHED;

        if (winningTeam != null) {
            sendMessageAll(GameMessageUtils.getVictoryMessage(winningTeam));
            sendTitleAll(winningTeam.getColor() + winningTeam.getName());
        } else {
            sendMessageAll(GameMessageUtils.getNoWinnerMessage());
        }

        Bukkit.getScheduler().runTaskLater(plugin, this::resetToLobby, 100L);
    }

    private void resetToLobby() {
        for (BukkitTask task : pendingRemovals.values()) {
            task.cancel();
        }
        pendingRemovals.clear();

        List<Player> toRequeue = new ArrayList<>(players);
        players.clear();
        teamManager.clearTeams();
        state = GameState.WAITING;

        for (Player player : toRequeue) {
            if (player.isOnline()) {
                joinPlayer(player);
            }
        }
    }

    public void sendMessageAll(String message) {
        players.forEach(player -> {
            player.sendMessage(message);
        });
    }

    public void sendTitleAll(String sec) {
        players.forEach(player -> {
            player.sendTitle(ChatColor.GREEN + sec, "");
        });
    }
}
