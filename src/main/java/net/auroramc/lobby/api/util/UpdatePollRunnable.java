/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.GameServerInfo;
import net.auroramc.lobby.gui.GameServerListing;
import net.auroramc.lobby.gui.LobbySwitcher;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdatePollRunnable extends BukkitRunnable {

    @Override
    public void run() {
        LobbyAPI.checkForPoll();
    }
}
