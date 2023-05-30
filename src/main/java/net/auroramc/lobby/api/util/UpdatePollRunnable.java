/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
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
