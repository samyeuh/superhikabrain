package com.samy.superhikabrain;

import com.samy.superhikabrain.starting.StartingTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    private GameState state;
    private final List<Player> players = new ArrayList<>();
    private final SuperHikabrain plugin;

    public GameManager(SuperHikabrain plugin) {
        this.state = GameState.WAITING;
        this.plugin = plugin;
    }

    public GameState getState() {
        return state;
    }

    public void addPlayer(Player player) {
        players.add(player);
        String nbPlayers = players.size() + "/2";
        this.sendMessageAll(GameMessageUtils.playerJoinMessage(player.getName(), nbPlayers));
    }

    public void removePlayer(Player player) {
        players.remove(player);
        String nbPlayers = players.size() + "/2";
        this.sendMessageAll(GameMessageUtils.playerLeaveMessage(player.getName(), nbPlayers));
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void startGame() {
        state = GameState.STARTING;
        StartingTask task = new StartingTask(this);
        task.runTaskTimer(plugin, 0, 20);
    }

    public void playGame() {
        state = GameState.PLAYING;
        players.forEach(player -> player.sendMessage("youhou"));
    }

    public void sendMessageAll(String message) {
        players.forEach(player -> player.sendMessage(message));
    }

    public void sendTitleAll(String sec){
        players.forEach(player -> player.sendTitle(ChatColor.GREEN + sec, ""));
    }
}
