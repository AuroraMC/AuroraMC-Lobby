/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class GameMenu extends GUI {

    private final AuroraMCPlayer player;

    public GameMenu(AuroraMCPlayer player) {
        super("&3&lBrowse Games", 5, true);
        this.player = player;
        border("&3&lBrowse Games", null);

        this.setItem(1, 4, new GUIItem(Material.NETHER_STAR, "&bCrystal Quest &3&lFEATURED GAME!", 1, "&8v" + LobbyAPI.getVersionNumber("CRYSTAL_QUEST") + ";;&7Collect Resources, Upgrade Gear and;&7protect your crystals at all costs!;;&aClick to view servers!"));
        this.setItem(3, 2, new GUIItem(Material.IRON_SWORD, "&cDuels", 1, "&8v" + LobbyAPI.getVersionNumber("DUELS") + ";;&7Coming Soon™;;&aClick to view servers!"));
        this.setItem(3, 4, new GUIItem(Material.FIREWORK, "&eArcade Mode", 1, "&8v" + LobbyAPI.getVersionNumber("ARCADE_MODE") + ";;&7Play a selection of different arcade;&7games with or without your friends.;;&aClick to view servers!"));
        this.setItem(3, 6, new GUIItem(Material.SNOW_BALL, "&aPaintball", 1, "&8v" + LobbyAPI.getVersionNumber("PAINTBALL") + ";;&7Throw snowballs at each other to get as;&7many kills as possible before time runs out!;;&aClick to view servers!"));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() == Material.STAINED_GLASS_PANE) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }
        switch (item.getType()) {
            case NETHER_STAR: {
                GameServerListing listing = new GameServerListing(player, "CRYSTAL_QUEST", "Crystal Quest", "CQ");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
                break;
            }
            case FIREWORK: {
                GameServerListing listing = new GameServerListing(player, "ARCADE_MODE", "Arcade Mode", "Arcade");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
                break;
            }
            case SNOW_BALL: {
                GameServerListing listing = new GameServerListing(player, "PAINTBALL", "Paintball", "Paintball");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
                break;
            }
            case IRON_SWORD: {
                GameServerListing listing = new GameServerListing(player, "DUELS", "Duels", "Duels");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
                break;
            }
        }
    }
}
