/*
 * Copyright (c) 2021 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.events.ServerCloseRequestEvent;
import net.auroramc.core.api.players.AuroraMCPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShutdownEventListener implements Listener {


    @EventHandler
    public void onShutdown(ServerCloseRequestEvent e) {
        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Lobby");
            out.writeUTF(player.getPlayer().getUniqueId().toString());
            player.getPlayer().sendPluginMessage(AuroraMCAPI.getCore(), "BungeeCord", out.toByteArray());
        }
        AuroraMCAPI.setShuttingDown(true);
        CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.CONFIRM_SHUTDOWN, "Mission Control", e.getType(), AuroraMCAPI.getServerInfo().getName(), AuroraMCAPI.getServerInfo().getNetwork().name()));
    }


}
