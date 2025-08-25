package com.samy.superhikabrain.tasks;

import com.samy.superhikabrain.manager.GameManager;
import com.samy.superhikabrain.utils.GameMessageUtils;
import com.samy.superhikabrain.utils.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class PrePlayingTask extends BukkitRunnable {

    private final GameManager manager;
    private int seconds;

    public PrePlayingTask(GameManager manager) {
        this.manager = manager;
        this.seconds = 5;
    }

    @Override
    public void run() {

        if (manager.getState() != GameState.PREPLAYING) {
            cancel();
            return;
        }

        if (seconds <= 5) {
            manager.sendTitleAll(String.valueOf(seconds));
            if (seconds == 5)
                manager.sendMessageAll(GameMessageUtils.getGameStartMessage("5"));

        }

        if (seconds == 0) {
            manager.playGame();
            cancel();
            return;
        }

        seconds--;
    }
}
