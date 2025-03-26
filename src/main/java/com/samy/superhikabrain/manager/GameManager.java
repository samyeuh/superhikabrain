package com.samy.superhikabrain.manager;

import com.samy.superhikabrain.TeamManager;
import com.samy.superhikabrain.utils.GameMessageUtils;
import com.samy.superhikabrain.utils.GameState;
import com.samy.superhikabrain.SuperHikabrain;
import com.samy.superhikabrain.tasks.StartingTask;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public void joinPlayer(Player player) {
        if (state == GameState.WAITING) {
            player.teleport(waitingServer.getSpawnLocation());
            Map<ItemStack, Integer> items = hotbarManager.getWaitingHotbar(player);
            items.forEach((item, slot) -> player.getInventory().setItem(slot, item));
            this.addPlayer(player);
        } else if (state == GameState.PLAYING) {
            teamManager.teleportPlayers();
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
        String nbPlayers = players.size() + "/2";
        this.sendMessageAll(GameMessageUtils.playerLeaveMessage(player.getName(), nbPlayers));
    }

    public void startGame() {
        state = GameState.STARTING;
        StartingTask task = new StartingTask(this);
        task.runTaskTimer(plugin, 0, 20);
    }

    public void playGame() {
        state = GameState.PLAYING;
        teamManager.addPlayersToTeam();
        players.forEach(player -> player.getInventory().clear());
        teamManager.teleportPlayers();
        players.forEach(player -> player.sendMessage("Vous êtes dans l'équipe " + teamManager.getPlayerTeam(player).getName()));
    }

    public void playerDeath(Player p){
        if (state == GameState.WAITING) {
            p.teleport(waitingServer.getSpawnLocation());
        }
    }

    public void sendMessageAll(String message) {
        players.forEach(player -> player.sendMessage(message));
    }

    public void sendTitleAll(String sec){
        players.forEach(player -> player.sendTitle(ChatColor.GREEN + sec, ""));
    }
}
