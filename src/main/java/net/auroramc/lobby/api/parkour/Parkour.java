/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.parkour;

import net.auroramc.core.api.utils.holograms.Hologram;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.parkour.plates.*;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Parkour {

    private final int id;
    private final String name;
    private final StartPoint start;
    private final EndPoint endPoint;
    private final List<Checkpoint> checkpoints;
    private final List<AuroraMCLobbyPlayer> players;
    private final Reward checkpointCommand;
    private final Reward endCommand;
    private final RestartPoint restartPoint;
    private final LeaderboardHologram leaderboard;
    private final List<Hologram> holograms;

    public Parkour(int id, String name, Reward checkpointCommand, Reward endCommand) {
        this.id = id;
        JSONObject locations = LobbyAPI.getLobbyMap().getMapData().getJSONObject("game").getJSONObject("PARKOUR");
        JSONArray startLocation = locations.getJSONArray(id + " START");
        JSONArray endLocation = locations.getJSONArray(id + " END");
        JSONArray restartLocation = locations.getJSONArray(id + " RESTART");
        JSONArray holoLocation = locations.getJSONArray(id + " HOLO");

        this.start = new StartPoint(new Location(LobbyAPI.getLobby().getServer().getWorld("world"), startLocation.getJSONObject(0).getInt("x") + 0.5, startLocation.getJSONObject(0).getInt("y") + 0.5, startLocation.getJSONObject(0).getInt("z") + 0.5, startLocation.getJSONObject(0).getFloat("yaw"),  0));
        this.start.setParkour(this);
        this.endPoint = new EndPoint(new Location(LobbyAPI.getLobby().getServer().getWorld("world"), endLocation.getJSONObject(0).getInt("x") + 0.5, endLocation.getJSONObject(0).getInt("y") + 0.5, endLocation.getJSONObject(0).getInt("z") + 0.5, endLocation.getJSONObject(0).getFloat("yaw"),  0));
        this.endPoint.setParkour(this);
        this.checkpoints = new ArrayList<>();
        this.holograms = new ArrayList<>();
        boolean last = false;
        int i = 1;
        while (locations.has(id + " CHECKPOINT" + i)) {
            JSONArray location = locations.getJSONArray(id + " CHECKPOINT" + i);
            this.checkpoints.add(new Checkpoint(new Location(LobbyAPI.getLobby().getServer().getWorld("world"), location.getJSONObject(0).getInt("x") + 0.5, location.getJSONObject(0).getInt("y") + 0.5, location.getJSONObject(0).getInt("z") + 0.5, location.getJSONObject(0).getFloat("yaw"),  0), i));
            i++;
        }
        this.players = new ArrayList<>();
        this.checkpointCommand = checkpointCommand;
        this.endCommand = endCommand;
        this.name = name;
        this.restartPoint = new RestartPoint(new Location(LobbyAPI.getLobby().getServer().getWorld("world"), restartLocation.getJSONObject(0).getInt("x") + 0.5, restartLocation.getJSONObject(0).getInt("y") + 0.5, restartLocation.getJSONObject(0).getInt("z") + 0.5, restartLocation.getJSONObject(0).getFloat("yaw"),  0));
        this.leaderboard = new LeaderboardHologram(new Location(LobbyAPI.getLobby().getServer().getWorld("world"), restartLocation.getJSONObject(0).getInt("x"), holoLocation.getJSONObject(0).getInt("y") + 0.5, holoLocation.getJSONObject(0).getInt("z"), holoLocation.getJSONObject(0).getFloat("yaw"),  0), this);
    }

    public int getNoCheckpoints() {
        return checkpoints.size();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EndPoint getEndPoint() {
        return endPoint;
    }

    public List<Checkpoint> getCheckpoints() {
        return new ArrayList<>(checkpoints);
    }

    public List<AuroraMCLobbyPlayer> getPlayers() {
        return new ArrayList<>(players);
    }

    public RestartPoint getRestartPoint() {
        return restartPoint;
    }

    public StartPoint getStart() {
        return start;
    }

    public Reward getCheckpointCommand() {
        return checkpointCommand;
    }

    public Reward getEndCommand() {
        return endCommand;
    }

    public List<PressurePlate> getAllPoints() {
        List<PressurePlate> pressurePlates = new ArrayList<>(checkpoints);
        pressurePlates.add(endPoint);
        pressurePlates.add(start);
        pressurePlates.add(restartPoint);
        return pressurePlates;
    }

    public void playerStart(AuroraMCLobbyPlayer p) {
        players.add(p);
    }

    public void playerEnd(AuroraMCLobbyPlayer p) {
        players.remove(p);
    }

    public Checkpoint getCheckpoint(int checkpointNo) {
        for (Checkpoint cp : checkpoints) {
            if (cp.getCheckpointNo() == checkpointNo) {
                return cp;
            }
        }
        return null;
    }

    public LeaderboardHologram getLeaderboard() {
        return leaderboard;
    }

    public void generateHolograms() {
        leaderboard.generate();
        Hologram hologram = new Hologram(null, start.getLocation().clone().add(0, 2, 0), null);
        hologram.addLine(1, name);
        hologram.addLine(2, "&bParkour Start");
        hologram.spawn();
        holograms.add(hologram);
        hologram = new Hologram(null, endPoint.getLocation().clone().add(0, 2, 0), null);
        hologram.addLine(1, "" + name);
        hologram.addLine(2, "&bParkour End");
        hologram.spawn();
        holograms.add(hologram);
        for (Checkpoint checkpoint : checkpoints) {
            hologram = new Hologram(null, checkpoint.getLocation().clone().add(0, 2, 0), null);
            hologram.addLine(1, name);
            hologram.addLine(2, "&bCheckpoint #" + checkpoint.getCheckpointNo());
            hologram.spawn();
            holograms.add(hologram);
        }
    }

    public static String formatTime(long ms) {
        long minutes,seconds;

        minutes = ms / 60000;
        ms -= (minutes * 60000);

        seconds = ms / 1000;
        ms -= (seconds * 1000);

        StringBuilder sb = new StringBuilder();

        if (minutes > 0) {
            sb.append(minutes);
            sb.append("m ");
        }

        sb.append(seconds);
        sb.append(".");
        sb.append(((ms < 100)?((ms < 10)?"00":"0"):""));
        sb.append(ms);
        sb.append("s");
        return sb.toString();
    }
}
