package com.samy.superhikabrain;

import org.bukkit.ChatColor;

public class GameMessageUtils {

    public static String header = ChatColor.LIGHT_PURPLE + "[Hikabrain] ";

    public static String getHeader() {
        return header;
    }

    public static String getGameStartMessage(String sec) {
        return header +
                ChatColor.WHITE + "La partie commence dans " +
                ChatColor.GREEN +  sec + " secondes" +
                ChatColor.WHITE + " !";
    }

    public static String playerJoinMessage(String player, String nbPlayers) {
        return header +
                ChatColor.AQUA + player +
                ChatColor.WHITE + " a " +
                ChatColor.GREEN + "rejoint " +
                ChatColor.WHITE + "la partie ! " +
                ChatColor.GREEN + nbPlayers;
    }

    public static String playerLeaveMessage(String player, String nbPlayers) {
        return header +
                ChatColor.AQUA + player +
                ChatColor.WHITE + " a " +
                ChatColor.RED + "quitté " +
                ChatColor.WHITE + "la partie ! " +
                ChatColor.GREEN + nbPlayers;
    }
}
