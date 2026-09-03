package com.samy.superhikabrain.utils;

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

    public static String getBedDestroyedMessage(HikaTeam team) {
        return header +
                ChatColor.WHITE + "Le lit de l'équipe " +
                team.getColor() + team.getName() +
                ChatColor.WHITE + " a été détruit !";
    }

    public static String getPlayerEliminatedMessage(String player) {
        return header +
                ChatColor.AQUA + player +
                ChatColor.WHITE + " a été " +
                ChatColor.RED + "éliminé" +
                ChatColor.WHITE + " !";
    }
}
