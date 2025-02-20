package com.samy.superhikabrain;

import com.samy.api.SuperAPI;
import com.samy.superhikabrain.scoreboard.ScoreboardListener;
import org.bukkit.plugin.java.JavaPlugin;
import com.samy.superhikabrain.waiting.WaitingListener;

public class SuperHikabrain extends JavaPlugin {

    private SuperAPI api;

    @Override
    public void onEnable() {
        api = SuperAPI.getInstance();
        GameManager manager = new GameManager(this);
        getServer().getPluginManager().registerEvents(new WaitingListener(manager), this);
        enableScoreboard();
    }

    public void enableScoreboard(){
        getServer().getPluginManager().registerEvents(new ScoreboardListener(api.getScoreboardManager()), this);
    }

}
