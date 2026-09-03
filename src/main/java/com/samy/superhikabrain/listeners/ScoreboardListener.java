package com.samy.superhikabrain.listeners;

import com.samy.api.scoreboard.IScoreboardManager;
import com.samy.superhikabrain.manager.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardListener implements Listener {

    private final IScoreboardManager scoreboardManager;
    private final GameManager gameManager;

    public ScoreboardListener(IScoreboardManager scoreboardManager, GameManager gameManager) {
        this.scoreboardManager = scoreboardManager;
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        refreshAll();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        refreshAll();
    }

    private void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setScoreboard(player);
        }
    }

    public void setScoreboard(Player player) {
        String objName = ("hika_" + System.currentTimeMillis() + player.getName());
        objName = objName.substring(0, Math.min(16, objName.length()));

        Objective objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("hikabrain", objName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§d§lHikabrain");

        objective.getScore("§6samy.fr").setScore(-2);
        objective.getScore(" ").setScore(-1);
        objective.getScore("§lJoueurs: §a" + gameManager.getPlayers().size() + "/" + gameManager.getMaxPlayers()).setScore(0);

        scoreboardManager.setScoreboard(player, objective, null);
    }
}
