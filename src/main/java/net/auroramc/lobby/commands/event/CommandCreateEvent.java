/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.commands.event;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.backend.info.ServerInfo;
import net.auroramc.api.permissions.Permission;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.lobby.api.LobbyAPI;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandCreateEvent extends ServerCommand {

    public CommandCreateEvent() {
        super("createevent", Arrays.asList("ce", "event", "hostevent", "he", "partyanimal"), Arrays.asList(Permission.ADMIN, Permission.EVENT_MANAGEMENT), false, null);
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String s, List<String> args) {
        for (ServerInfo info : LobbyAPI.getGameServers().values()) {
           if (info.getServerType().getString("game").equalsIgnoreCase("event")) {
               player.sendMessage(TextFormatter.pluginMessage("Server Manager", "There is already an event server open. Please close that event server before opening another."));
               return;
           }
        }
        if (args.size() > 0) {
            if (args.size() > 1) {
                player.sendMessage(TextFormatter.pluginMessage("Server Manager", "Invalid syntax. Correct syntax: **/createevent [server name]**"));
                return;
            }

            String name = args.get(0);
            if (!name.equalsIgnoreCase(AuroraMCAPI.getFilter().filter(name))) {
                player.sendMessage(TextFormatter.pluginMessage("Server Manager", "That name would have been filtered, so server creation was blocked."));
                return;
            }

            if (name.length() > 16) {
                player.sendMessage(TextFormatter.pluginMessage("Server Manager", "Server names can have a max length of 16 characters."));
                return;
            }

            ProtocolMessage message = new ProtocolMessage(Protocol.CREATE_SERVER, "Mission Control", "eventopen", AuroraMCAPI.getInfo().getName(), AuroraMCAPI.getInfo().getNetwork().name() + ";" + name);
            CommunicationUtils.sendMessage(message);
            player.sendMessage(TextFormatter.pluginMessage("Server Manager", "Event server **" + name + "** created. The server should be online is roughly 1 minute. When it's online, try **/server " + name + "** to connect."));
        } else {
            ProtocolMessage message = new ProtocolMessage(Protocol.CREATE_SERVER, "Mission Control", "eventopen", AuroraMCAPI.getInfo().getName(), AuroraMCAPI.getInfo().getNetwork().name() + ";Event-1");
            CommunicationUtils.sendMessage(message);
            player.sendMessage(TextFormatter.pluginMessage("Server Manager", "Event server **Event-1** created. The server should be online is roughly 1 minute. When it's online, try **/server Event-1** to connect."));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer player, String s, List<String> list, String s1, int i) {
        return null;
    }
}
