package com.samy.superhikabrain;

import com.samy.superhikabrain.manager.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamManager {

    private final List<HikaTeam> teams = new ArrayList<>();
    private final GameManager manager;

    public TeamManager(GameManager manager){
        this.manager = manager;

        int maxPlayers = manager.getPlugin().getConfig().getInt("max_players");
        int teamCount = manager.getPlugin().getConfig().getInt("team_count");

        createTeams(maxPlayers, teamCount);

    }

    public void createTeams(int maxPlayers, int teamCount){
        List<HikaTeam> possibleTeams = new ArrayList<>();
        possibleTeams.add(new HikaTeam("Red", ChatColor.RED, new ArrayList<>(),  new Location(manager.getPlugin().getServer().getWorld("game"), 0, 64, 0), maxPlayers / teamCount));
        possibleTeams.add(new HikaTeam("Blue", ChatColor.BLUE, new ArrayList<>(), new Location(manager.getPlugin().getServer().getWorld("game"), 0, 64, 0), maxPlayers / teamCount));
        possibleTeams.add(new HikaTeam("Green", ChatColor.GREEN, new ArrayList<>(), new Location(manager.getPlugin().getServer().getWorld("game"), 0, 64, 0), maxPlayers / teamCount));
        possibleTeams.add(new HikaTeam("Yellow", ChatColor.YELLOW, new ArrayList<>(), new Location(manager.getPlugin().getServer().getWorld("game"), 0, 64, 0), maxPlayers / teamCount));

        for (int i = 0; i < teamCount; i++){
            teams.add(possibleTeams.get(i));
        }
    }

    public List<HikaTeam> getTeams() {
        return teams;
    }

    public void addPlayerToTeam(Player player){
        for (HikaTeam team : teams){
            if (!team.isFull()){
                team.addPlayer(player);
                return;
            }
        }
    }

    public void removePlayerFromTeam(Player player){
        for (HikaTeam team : teams){
            if (team.getPlayers().contains(player)){
                team.removePlayer(player);
                return;
            }
        }
    }

    public void teleportPlayers(){
        for (HikaTeam team : teams){
            team.teleportPlayers();
        }
    }
}
