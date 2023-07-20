/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.api.backend.info.ServerInfo;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.gui.GameServerListing;
import net.auroramc.lobby.gui.LobbySwitcher;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateServersRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (ServerInfo info : LobbyAPI.getGameServers().values()) {
            info.fetchData();
        }
        LobbyAPI.updateTotals();
        for (AuroraMCServerPlayer player : ServerAPI.getPlayers()) {
            GUI gui = ServerAPI.getGUI(player);
            if (gui != null) {
                if (gui instanceof GameServerListing) {
                    ((GameServerListing) gui).update();
                } else if (gui instanceof LobbySwitcher) {
                    ((LobbySwitcher) gui).update();
                }
            }
        }
    }
}
