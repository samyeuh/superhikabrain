package com.samy.superhikabrain.manager;

import com.samy.superhikabrain.HikaTeam;
import com.samy.superhikabrain.TeamManager;
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

    private final GameManager manager;
    private final TeamManager teamManager;

    public HotbarManager(GameManager manager) {
        this.manager = manager;
        this.teamManager = manager.getTeamManager();
    }

    public Map<ItemStack, Integer> getWaitingHotbar(Player p) {
        p.getInventory().clear();
        p.getInventory().setHeldItemSlot(0);
        Map<ItemStack, Integer> hotbar = new HashMap<>();

        if (!teamManager.isSolo()){
            ItemStack teamSelect = new ItemStack(Material.WOOL, 1);
            hotbar.put(teamSelect, 0);
        }

        ItemStack leaveGame = new ItemStack(Material.WOOD_DOOR, 1);
        hotbar.put(leaveGame, 8);
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

    public void onWoolClick(Player p, ItemStack itemClick) {
        if (itemClick.getType() == Material.WOOL) {
            Inventory inv = p.getServer().createInventory(null, 9, "Choix de l'équipe");
            int i = 0;
            for (HikaTeam team : manager.getTeamManager().getTeams()) {
                ItemStack item = new ItemStack(Material.WOOL, 1, getWoolColor(team.getColor()));
                ItemMeta meta = getItemMeta(team, item);
                item.setItemMeta(meta);
                inv.setItem(i, item);
                i++;
            }
            inv.setItem(8, new ItemStack(Material.DARK_OAK_DOOR, 1));
            p.openInventory(inv);
        } else if (itemClick.getType() == Material.WOOD_DOOR) {
            p.kickPlayer("Vous avez quitté la partie");
        }
    }

    public void onColorWoolClick(Player p, ItemStack item) {
        if (item.getType() == Material.WOOL) {
            ChatColor[] colors = {ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW};
            byte data = (byte) item.getDurability();
            for (ChatColor c : colors){
                if (data == getWoolColor(c)){
                    HikaTeam team = manager.getTeamManager().getTeamByColor(c);
                    if (team != null)  {
                        if (team.isFull()) {
                            p.closeInventory();
                            return;
                        }
                        teamManager.addPlayerToTeam(p, team);
                        p.closeInventory();
                        p.getInventory().setItem(0, item);
                    }
                }
            }
        }
        p.closeInventory();
    }

    private static ItemMeta getItemMeta(HikaTeam team, ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String full = team.isFull() ? ChatColor.GRAY + " (pleine)" : "";
        meta.setDisplayName(team.getColor() + team.getName() + full);
        List<String> lore = new ArrayList<>();
        if (!team.getPlayers().isEmpty()) {
            for (Player player : team.getPlayers()) {
                lore.add(ChatColor.GRAY + " - " + player.getName());
            }
            meta.setLore(lore);
        }
        return meta;
    }
}
