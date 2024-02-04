/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
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
