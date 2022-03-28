/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.commands.admin;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.command.Command;
import net.auroramc.core.api.permissions.Permission;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandVersion extends Command {


    public CommandVersion() {
        super("version", Collections.emptyList(), Collections.singletonList(Permission.ADMIN), false, null);
    }

    @Override
    public void execute(AuroraMCPlayer player, String aliasUsed, List<String> args) {
        if (args.size() == 2) {
            new BukkitRunnable(){
                @Override
                public void run() {
                    LobbyDatabaseManager.setVersionNumber(args.get(0), args.get(1));
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Version", "Version number for game **" + args.get(0) + "** set to **v" + args.get(1) + "**"));
                    CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.VERSION_UPDATE, "Mission Control", "update", AuroraMCAPI.getServerInfo().getName(), "update"));
                }
            }.runTaskAsynchronously(AuroraMCAPI.getCore());
        } else {
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Version", "Invalid syntax. Correct syntax: **/version [game key] [version]**"));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCPlayer auroraMCPlayer, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
