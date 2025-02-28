package com.samy.superhikabrain;

import com.samy.api.TeamGame;
import com.samy.api.rank.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class HikaTeam extends TeamGame {

    private final Location spawn;
    private final int maxSize;

    public HikaTeam(String name, ChatColor color, List<Player> players, Location spawn, int maxSize) {
        super(name, color, players);
        this.spawn = spawn;
        this.maxSize = maxSize;
    }

    public Location getSpawn() {
        return spawn;
    }

    public int getMaxSize() {
        return maxSize;
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
        getPlayers().forEach(player -> player.teleport(spawn));
    }


}
