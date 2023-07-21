/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.commands.event;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.permissions.Permission;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
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

            ProtocolMessage message = new ProtocolMessage(Protocol.MESSAGE, "Mission Control", "eventopen", AuroraMCAPI.getInfo().getName(), AuroraMCAPI.getInfo().getNetwork().name() + ";" + name);
            CommunicationUtils.sendMessage(message);
        } else {
            ProtocolMessage message = new ProtocolMessage(Protocol.MESSAGE, "Mission Control", "eventopen", AuroraMCAPI.getInfo().getName(), AuroraMCAPI.getInfo().getNetwork().name() + ";Event-1");
            CommunicationUtils.sendMessage(message);
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer player, String s, List<String> list, String s1, int i) {
        return null;
    }
}
