package com.samy.superhikabrain;

import com.samy.api.SuperAPI;
import com.samy.superhikabrain.listeners.HotbarListener;
import com.samy.superhikabrain.listeners.ScoreboardListener;
import com.samy.superhikabrain.manager.GameManager;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import com.samy.superhikabrain.listeners.GameListener;

public class SuperHikabrain extends JavaPlugin {

    private SuperAPI api;
    private GameManager manager;

    @Override
    public void onEnable() {
        api = SuperAPI.getInstance();

        getServer().createWorld(new WorldCreator("waiting"));
        getServer().createWorld(new WorldCreator("game"));

        if (getServer().getWorld("waiting") == null) {
            getLogger().severe("Le monde 'waiting' est introuvable !");
        }
        if (getServer().getWorld("game") == null) {
            getLogger().severe("Le monde 'game' est introuvable !");
        }

        this.manager = new GameManager(this);
        getServer().getPluginManager().registerEvents(new GameListener(manager), this);
        getServer().getPluginManager().registerEvents(new HotbarListener(this), this);
        enableScoreboard();
    }

    public void enableScoreboard(){
        getServer().getPluginManager().registerEvents(new ScoreboardListener(api.getScoreboardManager()), this);
    }

    public GameManager getGameManager(){
        return manager;
    }

}
