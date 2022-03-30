/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.players;

import net.auroramc.core.api.players.AuroraMCPlayer;

public class AuroraMCLobbyPlayer extends AuroraMCPlayer {

    private final long joinTimestamp;
    private long lastPunchTimestamp;
    private long lastMonkeClick;

    public AuroraMCLobbyPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.joinTimestamp = System.currentTimeMillis();
    }

    public long getJoinTimestamp() {
        return joinTimestamp;
    }

    public void punched() {
        lastPunchTimestamp = System.currentTimeMillis();
    }

    public boolean canBePunched() {
        return System.currentTimeMillis() - lastPunchTimestamp >= 30000;
    }

    public void monkeClick() {
        lastMonkeClick = System.currentTimeMillis();
    }

    public boolean canMonkeClick() {
        return System.currentTimeMillis() - lastMonkeClick >= 2000;
    }
}
