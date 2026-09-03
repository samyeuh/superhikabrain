package com.samy.superhikabrain.listeners;

import com.samy.api.scoreboard.IScoreboardManager;
import com.samy.superhikabrain.manager.GameManager;
import com.samy.superhikabrain.utils.GameState;
import com.samy.superhikabrain.utils.HikaTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.List;

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

    public void refreshAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setScoreboard(player);
        }
    }

    public void setScoreboard(Player player) {
        Objective objective = newObjective(player);
        objective.getScore("§6samy.fr").setScore(-2);
        objective.getScore(" ").setScore(-1);

        if (gameManager.getState() == GameState.WAITING) {
            objective.getScore("§lJoueurs: §a" + gameManager.getPlayers().size() + "/" + gameManager.getMaxPlayers()).setScore(0);
        } else {
            List<HikaTeam> teams = gameManager.getTeamManager().getTeams();
            int score = teams.size();
            for (HikaTeam team : teams) {
                String line = team.getColor() + team.getName() + " §c" + hearts(team.getLives());
                objective.getScore(line).setScore(score--);
            }
        }

        scoreboardManager.setScoreboard(player, objective, null);
    }

    private Objective newObjective(Player player) {
        String objName = ("hika_" + System.currentTimeMillis() + player.getName());
        objName = objName.substring(0, Math.min(16, objName.length()));

        Objective objective = Bukkit.getScoreboardManager().getNewScoreboard().registerNewObjective("hikabrain", objName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§d§lHikabrain");
        return objective;
    }

    private String hearts(int lives) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lives; i++) {
            sb.append("❤");
        }
        return sb.toString();
    }
}
