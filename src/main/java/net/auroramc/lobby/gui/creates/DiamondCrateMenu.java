/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.creates;

import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.core.cosmetics.crates.IronCrate;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class DiamondCrateMenu extends GUI {


    private final AuroraMCLobbyPlayer player;
    private final List<Crate> availableCrates;

    public DiamondCrateMenu(AuroraMCLobbyPlayer player, ItemStack item) {
        super("&b&lDiamond Crates", 4, true);
        border("&b&lDiamond Crates", null);
        this.setItem(0, 4, new GUIItem(item));

        this.player = player;

         availableCrates = player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof DiamondCrate).collect(Collectors.toList());

        if (availableCrates.size() == 0) {
            this.setItem(2, 4, new GUIItem(Material.BARRIER, "&c&lYou do not have any available Diamond Crates.", 1, ";&7Purchase Diamond Crates using the main menu;&7or at store.auroramc.net!"));
        } else {
            int row = 1;
            int column = 1;
            for (Crate crate : availableCrates) {
                this.setItem(row, column, new GUIItem(Material.CHEST, "&b&lDiamond Crate", 1, ";&7UUID: " + crate.getUuid() + ";;&aClick to open!"));
                column++;
                if (column == 8) {
                    row++;
                    column = 1;
                    if (row == 4) {
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() != Material.CHEST) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
        } else {
            Crate crate = availableCrates.get(((row - 1) * 7) + (column - 1));
            //Do something
        }
    }
}
