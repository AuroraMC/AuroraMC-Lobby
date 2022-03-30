/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.players;

import net.auroramc.core.api.players.AuroraMCPlayer;

public class AuroraMCLobbyPlayer extends AuroraMCPlayer {

    private final long joinTimestamp;

    public AuroraMCLobbyPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.joinTimestamp = System.currentTimeMillis();
    }

    public long getJoinTimestamp() {
        return joinTimestamp;
    }
}
