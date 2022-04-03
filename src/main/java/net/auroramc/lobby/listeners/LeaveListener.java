/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.events.player.PlayerLeaveEvent;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerLeaveEvent e) {
        AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) e.getPlayer();
        player.deactivateForcefield();
        player.getStats().addLobbyTime(System.currentTimeMillis() - player.getJoinTimestamp(), true);
    }

}
