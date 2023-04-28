/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateDataRunnable extends BukkitRunnable {

    @Override
    public void run() {
        LobbyDatabaseManager.updateServerData();
    }
}
