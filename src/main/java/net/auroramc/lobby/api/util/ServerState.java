/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

public enum ServerState {

    STARTING_UP("Starting up"),
    IDLE("Idle"),
    RELOADING_MAPS("Reloading Maps"),
    PREPARING_GAME("Preparing Game"),
    WAITING_FOR_PLAYERS("Waiting For Players"),
    STARTING("Starting Soon"),
    IN_GAME ("In-Game"),
    ENDING("Ending");

    private String name;

    ServerState(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}