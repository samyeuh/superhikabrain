package com.samy.superhikabrain.listeners;

import com.samy.superhikabrain.manager.GameManager;
import com.samy.superhikabrain.manager.TeamManager;
import com.samy.superhikabrain.utils.GameMessageUtils;
import com.samy.superhikabrain.utils.GameState;
import com.samy.superhikabrain.utils.HikaTeam;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.List;

public class GameListener implements Listener {


    public GameManager manager;

    public GameListener(GameManager manager){
        this.manager = manager;
    }


    @EventHandler
    public void onJoinPlayer(PlayerJoinEvent event){
        Player p = event.getPlayer();
        manager.joinPlayer(p);
    }

    @EventHandler
    public void onQuitPlayer(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        manager.quitPlayer(p);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (manager.getState() == GameState.PLAYING) {
            World gameWorld = manager.getPlugin().getServer().getWorld("game");
            Location getFrom = event.getFrom();
            Location getTo = event.getTo();
            TeamManager teamManager = manager.getTeamManager();
            if (getFrom.getBlockY() < 0 && getTo.getBlockY() < 0 && p.getWorld().equals(gameWorld)){
                manager.playerFall(p);
            }
            List<HikaTeam> teams = teamManager.getTeams();
            for (HikaTeam team : teams){
                if (!team.getPlayers().contains(p)){
                    Block block = getTo.getBlock();
                    if (block.getLocation().equals((team.getBedSpawn()))){
                        teamManager.teleportPlayerToSpawn(p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (manager.getState() != GameState.PLAYING) return;
        Location loc = event.getBlock().getLocation();

        for (HikaTeam team : manager.getTeamManager().getTeams()) {
            if (!team.isBedDestroyed() && loc.equals(team.getBedSpawn())) {
                team.setBedDestroyed(true);
                manager.sendMessageAll(GameMessageUtils.getBedDestroyedMessage(team));
                return;
            }
        }

        if (manager.isPlacedBlock(loc)) {
            manager.untrackPlacedBlock(loc);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (manager.getState() != GameState.PLAYING) return;
        manager.trackPlacedBlock(event.getBlock().getLocation());
    }
}
