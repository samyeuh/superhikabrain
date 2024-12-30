package com.samy.superhikabrain.waiting;

import com.samy.superhikabrain.GameManager;
import com.samy.superhikabrain.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaitingListener implements Listener {


    public GameManager manager;

    public WaitingListener(GameManager manager){
        this.manager = manager;
    }


    @EventHandler
    public void onJoinPlayer(PlayerJoinEvent event){
        Player p = event.getPlayer();
        GameState state = manager.getState();
        if (state == GameState.WAITING) {
            p.getInventory().clear();
            p.teleport(p.getWorld().getSpawnLocation());
            manager.addPlayer(p);
            if (manager.getPlayers().size() == 2) {
                manager.startGame();
            }
        }
    }

    @EventHandler
    public void onQuitPlayer(PlayerQuitEvent event){
        Player p = event.getPlayer();
        GameState state = manager.getState();
        if (state == GameState.WAITING) {
            manager.removePlayer(p);
        }
    }
}
