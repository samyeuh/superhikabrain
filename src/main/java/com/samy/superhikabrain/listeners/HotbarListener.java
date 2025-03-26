package com.samy.superhikabrain.listeners;

import com.samy.superhikabrain.SuperHikabrain;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class HotbarListener implements Listener {

    public SuperHikabrain plugin;

    public HotbarListener(SuperHikabrain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        if (item.getType() == Material.WOOL) {
            plugin.getGameManager().getHotbarManager().onColorWoolClick(player, item);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        Action action = event.getAction();
        if (item == null) return;
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
            plugin.getGameManager().getHotbarManager().onWoolClick(player, item);
        }

    }
}
