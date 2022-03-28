/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.backend;

import net.auroramc.core.api.backend.ServerInfo;
import net.auroramc.lobby.api.util.ServerState;

public class GameServerInfo {

    private final ServerInfo info;
    private int currentPlayers;
    private int maxPlayers;
    private ServerState serverState;
    private String activeGame;
    private String activeMap;

    public GameServerInfo(ServerInfo info) {
        this.info = info;
    }

    public ServerInfo getInfo() {
        return info;
    }

    public void fetchData() {
        String[] data = LobbyDatabaseManager.fetchData(this.info).split(";");
        String[] players = data[1].split("/");
        currentPlayers = Integer.parseInt(players[0]);
        maxPlayers = Integer.parseInt(players[1]);
        serverState = ServerState.valueOf(data[0]);
        activeGame = data[2];
        activeMap = data[3];
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getActiveGame() {
        return activeGame;
    }

    public String getActiveMap() {
        return activeMap;
    }

    public ServerState getServerState() {
        return serverState;
    }
}

