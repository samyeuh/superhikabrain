package com.samy.superhikabrain.starting;

import com.samy.superhikabrain.GameManager;
import com.samy.superhikabrain.GameMessageUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask extends BukkitRunnable {

    private final GameManager manager;
    private int seconds;

    public StartingTask(GameManager manager) {
        this.manager = manager;
        this.seconds = 10;
    }

    @Override
    public void run() {
        if (seconds == 0) {
            manager.playGame();
            cancel();
            return;
        }

        if (seconds == 10)
            manager.sendMessageAll(GameMessageUtils.getGameStartMessage("10"));

        if (seconds <= 5) {
            manager.sendTitleAll(String.valueOf(seconds));
            if (seconds == 5)
                manager.sendMessageAll(GameMessageUtils.getGameStartMessage("5"));

        }
        seconds--;
    }
}
