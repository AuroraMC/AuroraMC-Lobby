/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.events.ProtocolMessageEvent;
import net.auroramc.lobby.api.LobbyAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ProtocolListener implements Listener {

    @EventHandler
    public void onProtocolMessage(ProtocolMessageEvent e) {
        if (e.getMessage().getProtocol() == Protocol.VERSION_UPDATE) {
            LobbyAPI.loadVersionNumbers();
        } else if (e.getMessage().getProtocol() == Protocol.SERVER_ONLINE) {
            LobbyAPI.addGameServer(e.getMessage().getExtraInfo());
        } else if (e.getMessage().getProtocol() == Protocol.REMOVE_SERVER) {
            LobbyAPI.removeGameServer(e.getMessage().getExtraInfo());
        }
    }

}
