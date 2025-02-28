package com.samy.superhikabrain.tasks;

import com.samy.superhikabrain.manager.GameManager;
import com.samy.superhikabrain.utils.GameMessageUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask extends BukkitRunnable {

    private final GameManager manager;
    private int seconds;

    public StartingTask(GameManager manager) {
        this.manager = manager;
        this.seconds = 15;
    }

    @Override
    public void run() {

        if (!manager.isFull()) {
            cancel();
            return;
        }

        if (seconds == 15)
            manager.sendMessageAll(GameMessageUtils.getGameStartMessage("15"));

        if (seconds == 10)
            manager.sendMessageAll(GameMessageUtils.getGameStartMessage("10"));

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
