/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ViewCrates extends GUI {

    private final AuroraMCLobbyPlayer player;

    public ViewCrates(AuroraMCLobbyPlayer player) {
        super("&3&lCrate Menu", 2, true);
        border("&3&lCrate Menu", null);
        this.player = player;
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {

    }
}
