/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.changelog;

import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class Changelogs extends GUI {

    private AuroraMCPlayer player;

    public Changelogs(AuroraMCPlayer player) {
        super("&3&lChangelogs", 5, true);


    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {

    }
}
