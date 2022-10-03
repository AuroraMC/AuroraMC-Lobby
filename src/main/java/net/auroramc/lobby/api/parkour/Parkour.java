/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.parkour;

import net.auroramc.lobby.api.parkour.plates.*;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Parkour {

    private final int id;
    private UUID server;
    private String name;
    private StartPoint start;
    private EndPoint endPoint;
    private final List<Checkpoint> checkpoints;
    private final List<AuroraMCLobbyPlayer> players;
    private Reward checkpointCommand;
    private Reward endCommand;
    private RestartPoint restartPoint;
    private final List<BorderPoint> borderPoints;
    private final LeaderboardHologram leaderboard;

    @SuppressWarnings("unused")
    public Parkour(int id, UUID server, String name, StartPoint start, EndPoint end, List<Checkpoint> checkpoints, RestartPoint restartPoint, List<BorderPoint> borderPoints, Reward checkpointCommand, Reward endCommand, LeaderboardHologram leaderboard) {
        this.id = id;
        this.server = server;
        this.start = start;
        this.start.setParkour(this);
        this.endPoint = end;
        this.endPoint.setParkour(this);
        this.checkpoints = checkpoints;
        for (Checkpoint checkpoint : checkpoints) {
            checkpoint.setParkour(this);
        }
        this.players = new ArrayList<>();
        this.checkpointCommand = checkpointCommand;
        this.endCommand = endCommand;
        this.name = name;
        this.restartPoint = restartPoint;
        this.borderPoints = borderPoints;
        for (BorderPoint borderPoint : borderPoints) {
            borderPoint.setParkour(this);
        }
        this.leaderboard = leaderboard;
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
        pressurePlates.addAll(borderPoints);
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

    public List<BorderPoint> getBorders() {
        return borderPoints;
    }

    public LeaderboardHologram getLeaderboard() {
        return leaderboard;
    }
}
