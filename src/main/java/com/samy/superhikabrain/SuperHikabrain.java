package com.samy.superhikabrain;

import com.samy.api.SuperAPI;
import com.samy.superhikabrain.listeners.HotbarListener;
import com.samy.superhikabrain.listeners.ScoreboardListener;
import com.samy.superhikabrain.manager.GameManager;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import com.samy.superhikabrain.listeners.GameListener;

import java.io.File;

public class SuperHikabrain extends JavaPlugin {

    private SuperAPI api;
    private GameManager manager;

    @Override
    public void onEnable() {
        api = SuperAPI.getInstance();

        if (!loadArenaWorld("waiting") || !loadArenaWorld("game")) {
            getLogger().severe("Arène(s) manquante(s) : dépose les dossiers de map 'waiting' et 'game' à la racine du serveur avant de relancer. Le plugin ne démarre pas.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.manager = new GameManager(this);
        getServer().getPluginManager().registerEvents(new GameListener(manager), this);
        getServer().getPluginManager().registerEvents(new HotbarListener(this), this);
        enableScoreboard();
    }

    private boolean loadArenaWorld(String name) {
        File worldFolder = new File(getServer().getWorldContainer(), name);
        if (!worldFolder.isDirectory()) {
            getLogger().severe("Map '" + name + "' introuvable (" + worldFolder.getPath() + ").");
            return false;
        }
        return getServer().createWorld(new WorldCreator(name)) != null;
    }

    public void enableScoreboard(){
        getServer().getPluginManager().registerEvents(new ScoreboardListener(api.getScoreboardManager(), manager), this);
    }

    public GameManager getGameManager(){
        return manager;
    }

}
