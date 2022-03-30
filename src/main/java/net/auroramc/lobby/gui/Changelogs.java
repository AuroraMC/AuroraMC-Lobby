/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import net.auroramc.core.api.utils.gui.GUI;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class Changelogs extends GUI {

    public Changelogs() {
        super("&3&lChangelogs", 5, true);


    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {

    }
}
