package com.samy.superhikabrain.manager;

import com.samy.superhikabrain.utils.HikaTeam;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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

    private static ItemStack makeUnbreakable(ItemStack item){
        net.minecraft.server.v1_8_R3.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = nms.hasTag() ? nms.getTag() : new NBTTagCompound();
        tag.setBoolean("Unbreakable", true);
        nms.setTag(tag);
        return CraftItemStack.asBukkitCopy(nms);
    }

    public Map<Integer, ItemStack> getWaitingHotbar(Player p) {
        p.getInventory().clear();
        p.getInventory().setHeldItemSlot(0);
        Map<Integer, ItemStack> hotbar = new HashMap<>();

        if (!teamManager.isSolo()){
            ItemStack teamSelect = new ItemStack(Material.WOOL, 1);
            ItemMeta teamSelect_meta = teamSelect.getItemMeta();
            teamSelect_meta.setDisplayName(ChatColor.YELLOW + "Choisir une équipe");
            teamSelect.setItemMeta(teamSelect_meta);
            hotbar.put(0, teamSelect);
        }

        ItemStack leaveGame = new ItemStack(Material.WOOD_DOOR, 1);
        ItemMeta leaveGame_meta = leaveGame.getItemMeta();
        leaveGame_meta.setDisplayName(ChatColor.RED + "Quitter la partie");
        leaveGame.setItemMeta(leaveGame_meta);
        hotbar.put(8, leaveGame);
        return hotbar;
    }

    public Map<Integer, ItemStack> getPlayingHotbar(Player p) {
        p.getInventory().clear();
        p.getInventory().setHeldItemSlot(0);
        Map<Integer, ItemStack> hotbar = new HashMap<>();

        ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta sword_meta = sword.getItemMeta();
        sword_meta.setDisplayName(ChatColor.AQUA + "Épée");
        sword_meta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
        sword_meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        sword_meta.addEnchant(Enchantment.DURABILITY, 3, true);
        sword_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        sword.setItemMeta(sword_meta);
        sword = makeUnbreakable(sword);
        hotbar.put(0, sword);

        ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE, 1);
        ItemMeta pickaxe_meta = pickaxe.getItemMeta();
        pickaxe_meta.setDisplayName(ChatColor.AQUA + "Pioche");
        pickaxe_meta.addEnchant(Enchantment.DIG_SPEED, 3, true);
        pickaxe_meta.addEnchant(Enchantment.DURABILITY, 3, true);
        pickaxe_meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pickaxe.setItemMeta(pickaxe_meta);
        pickaxe = makeUnbreakable(pickaxe);
        hotbar.put(1, pickaxe);

        ItemStack gold_apple = new ItemStack(Material.GOLDEN_APPLE, 64);
        hotbar.put(2, gold_apple);

        for (int i = 3; i < 9; i++) {
            ItemStack blocks = new ItemStack(Material.SANDSTONE, 64, (short) 2);
            hotbar.put(i, blocks);
        }

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
