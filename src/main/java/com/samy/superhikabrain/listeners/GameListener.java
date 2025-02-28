package com.samy.superhikabrain.listeners;

import com.samy.superhikabrain.manager.GameManager;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
}
