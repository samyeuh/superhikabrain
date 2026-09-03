package com.samy.superhikabrain.manager;

import com.samy.superhikabrain.utils.HikaTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

    public boolean isSolo() {
        int maxPlayers = manager.getPlugin().getConfig().getInt("max_players");
        int teamCount = manager.getPlugin().getConfig().getInt("team_count");
        return (maxPlayers / teamCount) == 1;
    }

    public void createTeams(int maxPlayers, int teamCount){
        List<Map<?, ?>> configuredTeams = manager.getPlugin().getConfig().getMapList("teams");
        if (configuredTeams.size() < teamCount) {
            throw new IllegalStateException("config.yml définit " + configuredTeams.size()
                    + " équipe(s) dans 'teams' mais team_count=" + teamCount
                    + " : ajoute des entrées 'teams' dans config.yml.");
        }

        World world = manager.getPlugin().getServer().getWorld("game");
        int teamSize = maxPlayers / teamCount;

        for (int i = 0; i < teamCount; i++) {
            Map<?, ?> teamConfig = configuredTeams.get(i);
            String name = (String) teamConfig.get("name");
            ChatColor color = ChatColor.valueOf((String) teamConfig.get("color"));
            Location spawn = parseLocation(world, (String) teamConfig.get("spawn"));
            Location bedSpawn = parseLocation(world, (String) teamConfig.get("bed"));
            teams.add(new HikaTeam(name, color, new ArrayList<>(), spawn, teamSize, bedSpawn));
        }
    }

    private Location parseLocation(World world, String raw) {
        String[] parts = raw.split(",");
        double x = Double.parseDouble(parts[0].trim());
        double y = Double.parseDouble(parts[1].trim());
        double z = Double.parseDouble(parts[2].trim());
        float yaw = parts.length > 3 ? Float.parseFloat(parts[3].trim()) : 0f;
        float pitch = parts.length > 4 ? Float.parseFloat(parts[4].trim()) : 0f;
        return new Location(world, x, y, z, yaw, pitch);
    }

    public List<HikaTeam> getTeams() {
        return teams;
    }

    public HikaTeam getPlayerTeam(Player p){
        for (HikaTeam team : teams){
            if (team.getPlayers().contains(p)){
                return team;
            }
        }
        return null;
    }

    public void addPlayerToTeam(Player player, HikaTeam team){
        if (getPlayerTeam(player) != null) {
            removePlayerFromTeam(player);
        }
        team.addPlayer(player);
    }

    public void addPlayersToTeam() {
        for (Player player : manager.getPlayers()) {
            if (getPlayerTeam(player) == null) {
                addPlayerToRandomTeam(player);
            }
        }
    }

    public void addPlayerToRandomTeam(Player player){
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

    public void resetBeds() {
        for (HikaTeam team : teams) {
            team.restoreBed();
        }
    }

    public void clearTeams() {
        for (HikaTeam team : teams) {
            team.getPlayers().clear();
        }
    }

    public void teleportPlayers(){
        for (HikaTeam team : teams){
            team.teleportPlayers();
        }
    }

    public HikaTeam getTeamByColor(ChatColor color){
        for (HikaTeam team : teams){
            if (team.getColor().equals(color)){
                return team;
            }
        }
        return null;
    }

    public void teleportPlayerToSpawn(Player p) {
        HikaTeam team = getPlayerTeam(p);
        if (team != null) {
            team.teleportPlayer(p);
        }
    }
}
