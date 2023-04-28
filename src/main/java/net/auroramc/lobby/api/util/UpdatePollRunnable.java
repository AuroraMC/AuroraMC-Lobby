/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.lobby.api.LobbyAPI;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdatePollRunnable extends BukkitRunnable {

    @Override
    public void run() {
        LobbyAPI.checkForPoll();
    }
}
