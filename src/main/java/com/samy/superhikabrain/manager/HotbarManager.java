package com.samy.superhikabrain.manager;

import com.samy.superhikabrain.HikaTeam;
import com.samy.superhikabrain.SuperHikabrain;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotbarManager {

    public Map<Player, Map<ItemStack, Integer>> waitingHotbar;
    private final GameManager manager;

    public HotbarManager(GameManager manager) {
        waitingHotbar = new HashMap<>();
        this.manager = manager;
    }

    public Map<ItemStack, Integer> getWaitingHotbar(Player p) {
        p.getInventory().clear();
        Map<ItemStack, Integer> hotbar = new HashMap<>();
        ItemStack teamSelect = new ItemStack(Material.WOOL, 1);
        ItemStack leaveGame = new ItemStack(Material.DARK_OAK_DOOR, 1);
        hotbar.put(teamSelect, 0);
        hotbar.put(leaveGame, 8);
        waitingHotbar.put(p, hotbar);
        return hotbar;
    }

    public byte getWoolColor(ChatColor color) {
        switch (color) {
            case BLUE: return DyeColor.LIGHT_BLUE.getWoolData();
            case GREEN: return DyeColor.LIME.getWoolData();
            case RED: return DyeColor.RED.getWoolData();
            case YELLOW: return DyeColor.YELLOW.getWoolData();
            case WHITE: return DyeColor.WHITE.getWoolData();
            default: return 0;
        }
    }


    public void onWoolClick(Player p, Material block) {
        if (block == Material.WOOL) {
            Inventory inv = p.getServer().createInventory(null, 9, "Choix de l'équipe");
            int i = 0;
            for (HikaTeam team : manager.getTeamManager().getTeams()) {
                ItemStack item = new ItemStack(Material.WOOL, 1, getWoolColor(team.getColor()));
                ItemMeta meta = getItemMeta(team, item);
                item.setItemMeta(meta);
                inv.setItem(i, item);
                i++;
            }
            p.openInventory(inv);
        } else if (block == Material.DARK_OAK_DOOR) {
            p.kickPlayer("Vous avez quitté la partie");
        } else {

        }
    }

    private static ItemMeta getItemMeta(HikaTeam team, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String full = team.isFull() ? ChatColor.GRAY + " (pleine)" : "";
        meta.setDisplayName(team.getColor() + team.getName() + full);
        List<String> lore = new ArrayList<>();
        if (!team.getPlayers().isEmpty()) {
            for (Player player : team.getPlayers()) {
                lore.add(" - " + player.getName());
            }
            meta.setLore(lore);
        }
        return meta;
    }
}
