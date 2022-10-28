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
import org.bukkit.scoreboard.Objective;

import java.util.concurrent.atomic.AtomicInteger;

public class UpdateServersRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for (GameServerInfo info : LobbyAPI.getGameServers().values()) {
            info.fetchData();
        }
        LobbyAPI.updateTotals();
        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
            GUI gui = AuroraMCAPI.getGUI(player);
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
