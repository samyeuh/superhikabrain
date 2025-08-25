package com.samy.superhikabrain.manager;

import com.samy.superhikabrain.tasks.PrePlayingTask;
import com.samy.superhikabrain.utils.GameMessageUtils;
import com.samy.superhikabrain.utils.GameState;
import com.samy.superhikabrain.SuperHikabrain;
import com.samy.superhikabrain.tasks.StartingTask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameManager {

    private GameState state;
    private final List<Player> players = new ArrayList<>();
    private final SuperHikabrain plugin;
    private final int maxPlayers;
    private final World waitingServer;
    private final World gameServer;
    private final HotbarManager hotbarManager;
    private final TeamManager teamManager;

    public GameManager(SuperHikabrain plugin) {
        this.state = GameState.WAITING;
        this.plugin = plugin;
        this.teamManager = new TeamManager(this);
        this.hotbarManager = new HotbarManager(this);
        this.maxPlayers = plugin.getConfig().getInt("max_players");
        this.gameServer = plugin.getServer().getWorld("game");
        this.waitingServer = plugin.getServer().getWorld("waiting");
        waitingServer.setSpawnLocation(54, 64, 0);

        setGameRules();
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
        if (state == GameState.WAITING) {
            this.removePlayer(player);
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
        String nbPlayers = players.size() + "/" + maxPlayers;
        this.sendMessageAll(GameMessageUtils.playerLeaveMessage(player.getName(), nbPlayers));
    }

    public void startGame() {
        state = GameState.STARTING;
        StartingTask task = new StartingTask(this);
        task.runTaskTimer(plugin, 0, 20);
    }

    public void preplayGame() {
        state = GameState.PREPLAYING;
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
        if (state == GameState.PLAYING) {
            teamManager.teleportPlayerToSpawn(p);
            Map<Integer, ItemStack> items = hotbarManager.getPlayingHotbar(p);
            items.forEach((slot, item) -> p.getInventory().setItem(slot, item));
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
