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

    public static String getLifeLostMessage(HikaTeam team) {
        return header +
                ChatColor.WHITE + "L'équipe " +
                team.getColor() + team.getName() +
                ChatColor.WHITE + " a perdu un point ! (" +
                ChatColor.RED + team.getLives() +
                ChatColor.WHITE + " restant" + (team.getLives() > 1 ? "s" : "") + ")";
    }

    public static String getTeamEliminatedMessage(HikaTeam team) {
        return header +
                ChatColor.WHITE + "L'équipe " +
                team.getColor() + team.getName() +
                ChatColor.WHITE + " n'a plus de points, elle est " +
                ChatColor.RED + "éliminée" +
                ChatColor.WHITE + " !";
    }

    public static String getVictoryMessage(HikaTeam team) {
        return header +
                ChatColor.WHITE + "L'équipe " +
                team.getColor() + team.getName() +
                ChatColor.WHITE + " a gagné la partie !";
    }

    public static String getNoWinnerMessage() {
        return header + ChatColor.WHITE + "Partie terminée, aucune équipe gagnante.";
    }

    public static String playerDisconnectedMessage(String player) {
        return header +
                ChatColor.AQUA + player +
                ChatColor.WHITE + " s'est déconnecté, il a " +
                ChatColor.GREEN + "5 minutes" +
                ChatColor.WHITE + " pour revenir dans la partie.";
    }

    public static String playerReconnectedMessage(String player) {
        return header +
                ChatColor.AQUA + player +
                ChatColor.WHITE + " est " +
                ChatColor.GREEN + "revenu" +
                ChatColor.WHITE + " dans la partie !";
    }
}
