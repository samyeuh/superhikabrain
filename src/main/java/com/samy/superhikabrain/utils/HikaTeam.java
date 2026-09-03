package com.samy.superhikabrain.utils;

import com.samy.api.TeamGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class HikaTeam extends TeamGame {

    private final Location spawn;
    private final int maxSize;
    private final Location bedSpawn;
    private final int startingLives;
    private int lives;

    public HikaTeam(String name, ChatColor color, List<Player> players, Location spawn, int maxSize, Location bedSpawn, int startingLives) {
        super(name, color, players);
        this.spawn = spawn;
        this.maxSize = maxSize;
        this.bedSpawn = bedSpawn;
        this.startingLives = startingLives;
        this.lives = startingLives;
    }

    public Location getSpawn() {
        return spawn;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public Location getBedSpawn() {
        return bedSpawn;
    }

    public int getLives() {
        return lives;
    }

    public boolean isEliminated() {
        return lives <= 0;
    }

    public void loseLife() {
        if (lives > 0) lives--;
    }

    public void resetLives() {
        this.lives = startingLives;
    }

    public boolean isFull() {
        return getPlayers().size() >= maxSize;
    }

    public void addPlayer(Player player) {
        if (!isFull()) {
            getPlayers().add(player);
        }
    }

    public void removePlayer(Player player) {
        getPlayers().remove(player);
    }

    public void teleportPlayers() {
        getPlayers().forEach(player -> {
            player.teleport(spawn);
            player.setFallDistance(0f);
            player.setNoDamageTicks(20);
        });
    }

    public void teleportPlayer(Player player) {
        if (!getPlayers().contains(player)) return;
        player.teleport(spawn);
        player.setFallDistance(0f);
        player.setNoDamageTicks(20);
    }


}
