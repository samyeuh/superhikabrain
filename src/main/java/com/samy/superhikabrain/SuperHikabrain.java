package com.samy.superhikabrain;

import com.samy.api.SuperAPI;
import com.samy.superhikabrain.listeners.ScoreboardListener;
import com.samy.superhikabrain.manager.GameManager;
import com.samy.superhikabrain.manager.HotbarManager;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import com.samy.superhikabrain.listeners.GameListener;

public class SuperHikabrain extends JavaPlugin {

    private SuperAPI api;
    private HotbarManager hotbarManager;

    @Override
    public void onEnable() {
        api = SuperAPI.getInstance();

        getServer().createWorld(new WorldCreator("waiting"));

        GameManager manager = new GameManager(this);
        getServer().getPluginManager().registerEvents(new GameListener(manager), this);

        enableScoreboard();
    }

    public void enableScoreboard(){
        getServer().getPluginManager().registerEvents(new ScoreboardListener(api.getScoreboardManager()), this);
    }

    public HotbarManager getHotbarManager(){
        return hotbarManager;
    }

}
