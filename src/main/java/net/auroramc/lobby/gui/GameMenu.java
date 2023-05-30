/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.gui;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GameMenu extends GUI {

    private final AuroraMCServerPlayer player;

    public GameMenu(AuroraMCServerPlayer player) {
        super("&3&lServer Navigation", 5, true);
        this.player = player;
        border("&3&lServer Navigation", null);

        this.setItem(1, 3, new GUIItem(Material.NETHER_STAR, "&bCrystal Quest &3&lFEATURED GAME!", 1, "&8v" + LobbyAPI.getVersionNumber("CRYSTAL_QUEST") + ";;&7Collect Resources, Upgrade Gear and;&7protect your crystals at all costs!;;&fJoin **" + LobbyAPI.getGameTotals().get("CRYSTAL_QUEST") + "**&f other players!;;&aClick to view servers!"));
        this.setItem(1, 5, new GUIItem(Material.GRASS, "&dNuttersSMP", 1, ";&7We've partnered with JellyPeanut to host his SMP!;&7Play a classic SMP with the Nutters community!;;&r&fMinecraft Version &b1.19.4&r.;;&aClick to join!"));
        this.setItem(3, 2, new GUIItem(Material.IRON_SWORD, "&cDuels", 1, "&8v" + LobbyAPI.getVersionNumber("DUELS") + ";;&7Battle your opponent and be the last player standing!;;&fJoin **" + LobbyAPI.getGameTotals().get("DUELS") + "**&f other players!;;&aClick to view servers!"));
        this.setItem(3, 4, new GUIItem(Material.FIREWORK, "&eArcade Mode", 1, "&8v" + LobbyAPI.getVersionNumber("ARCADE_MODE") + ";;&7Play a selection of different arcade;&7games with or without your friends.;;&fJoin **" + LobbyAPI.getGameTotals().get("ARCADE_MODE") + "**&f other players!;;&aClick to view servers!"));
        this.setItem(3, 6, new GUIItem(Material.SNOW_BALL, "&aPaintball", 1, "&8v" + LobbyAPI.getVersionNumber("PAINTBALL") + ";;&7Throw snowballs at each other to get as;&7many kills as possible before time runs out!;;&fJoin **" + LobbyAPI.getGameTotals().get("PAINTBALL") + "**&f other players!;;&aClick to view servers!"));

        this.setItem(5, 3, new GUIItem(Material.WOOL, "&a&lQuick Teleport to Easy Parkour", 1,";&fTeleport to our &aEasy&f parkour!;;&aClick here to teleport!", (short)5));
        this.setItem(5, 4, new GUIItem(Material.WOOL, "&6&lQuick Teleport to Medium Parkour", 1,";&fTeleport to our &6Medium&f parkour!;;&aClick here to teleport!", (short)1));
        this.setItem(5, 5, new GUIItem(Material.WOOL, "&c&lQuick Teleport to Hard Parkour", 1,";&fTeleport to our &cHard&f parkour!;;&aClick here to teleport!", (short)14));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() == Material.STAINED_GLASS_PANE) {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }
        switch (item.getType()) {
            case NETHER_STAR: {
                GameServerListing listing = new GameServerListing(player, "CRYSTAL_QUEST", "Crystal Quest", "CQ");
                listing.open(player);
                break;
            }
            case FIREWORK: {
                GameServerListing listing = new GameServerListing(player, "ARCADE_MODE", "Arcade Mode", "Arcade");
                listing.open(player);
                break;
            }
            case SNOW_BALL: {
                GameServerListing listing = new GameServerListing(player, "PAINTBALL", "Paintball", "PB");
                listing.open(player);
                break;
            }
            case IRON_SWORD: {
                GameServerListing listing = new GameServerListing(player, "DUELS", "Duels", "Duels");
                listing.open(player);
                break;
            }
            case WOOL: {
                Location l = null;
                switch (column) {
                    case 3: {
                        l = LobbyAPI.getEasy().getRestartPoint().getLocation().clone();
                        player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have been teleported to the §aEasy§r parkour."));
                        break;
                    }
                    case 4: {
                        l = LobbyAPI.getMedium().getRestartPoint().getLocation().clone();
                        player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have been teleported to the §6Medium§r parkour."));
                        break;
                    }
                    case 5: {
                        l = LobbyAPI.getHard().getRestartPoint().getLocation().clone();
                        player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have been teleported to the §cHard§r parkour."));
                        break;
                    }
                }
                player.setVelocity(new Vector(0, 0, 0));
                player.teleport(l);
                player.closeInventory();
                break;
            }
            case GRASS: {
                player.closeInventory();
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("SMP");
                out.writeUTF(player.getUniqueId().toString());
                player.sendPluginMessage(out.toByteArray());
                break;
            }
        }
    }
}
