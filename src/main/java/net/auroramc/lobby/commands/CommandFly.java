/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.commands;

import net.auroramc.api.permissions.Permission;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandFly extends ServerCommand {


    public CommandFly() {
        super("fly", Collections.singletonList("flight"), Arrays.asList(Permission.ELITE, Permission.PLUS), true, "You must have Elite rank or better or Plus to use this command. Purchase a rank at store.auroramc.net.");
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        if (((AuroraMCLobbyPlayer)player).isInParkour()) {
            player.sendMessage(TextFormatter.pluginMessage("Lobby", "You cannot toggle flight while in a parkour!"));
            return;
        }
        player.setFlying(false);
        player.getPreferences().setHubFlight(!player.getPreferences().isHubFlightEnabled(), true);
        player.sendMessage(TextFormatter.pluginMessage("Lobby", "Hub Flight: " + ((player.getPreferences().isHubFlightEnabled())?"§aEnabled":"§cDisabled")));
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer auroraMCPlayer, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
