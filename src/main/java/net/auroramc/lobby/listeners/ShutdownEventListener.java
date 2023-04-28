/*
 * Copyright (c) 2021 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.events.server.ServerCloseRequestEvent;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShutdownEventListener implements Listener {

    @EventHandler
    public void onShutdown(ServerCloseRequestEvent e) {
        for (AuroraMCServerPlayer player : ServerAPI.getPlayers()) {
            player.sendMessage(TextFormatter.pluginMessage("Server Manager", "This lobby is being restarted for an update. You are being sent to another lobby."));
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Lobby");
            out.writeUTF(player.getUniqueId().toString());
            player.sendPluginMessage(out.toByteArray());
        }
        ServerAPI.setShuttingDown(true);
        CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.CONFIRM_SHUTDOWN, "Mission Control", e.getType(), AuroraMCAPI.getInfo().getName(), AuroraMCAPI.getInfo().getNetwork().name()));
    }


}
