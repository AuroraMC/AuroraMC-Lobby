/*
 * Copyright (c) 2021 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.events.ServerCloseRequestEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShutdownEventListener implements Listener {


    @EventHandler
    public void onShutdown(ServerCloseRequestEvent e) {
        AuroraMCAPI.setShuttingDown(true);
        CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.CONFIRM_SHUTDOWN, "Mission Control", e.getType(), AuroraMCAPI.getServerInfo().getName(), AuroraMCAPI.getServerInfo().getNetwork().name()));
    }


}
