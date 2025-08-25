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

    public HikaTeam(String name, ChatColor color, List<Player> players, Location spawn, int maxSize, Location bedSpawn) {
        super(name, color, players);
        this.spawn = spawn;
        this.maxSize = maxSize;
        this.bedSpawn = bedSpawn;
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
