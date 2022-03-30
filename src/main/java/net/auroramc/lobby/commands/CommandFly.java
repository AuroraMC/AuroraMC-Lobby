/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.commands;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.command.Command;
import net.auroramc.core.api.permissions.Permission;
import net.auroramc.core.api.players.AuroraMCPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandFly extends Command {


    public CommandFly() {
        super("fly", Collections.singletonList("flight"), Arrays.asList(Permission.ELITE, Permission.PLUS), true, "You must have Elite rank or better or Plus to use this command. Purchase a rank at store.auroramc.net.");
    }

    @Override
    public void execute(AuroraMCPlayer player, String aliasUsed, List<String> args) {
        player.getPlayer().setAllowFlight(!player.getPlayer().getAllowFlight());
        player.getPreferences().setHubFlight(player.getPlayer().getAllowFlight());
        player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Lobby", "Hub Flight: " + ((player.getPlayer().getAllowFlight())?"&aEnabled":"&cDisabled")));
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCPlayer auroraMCPlayer, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
