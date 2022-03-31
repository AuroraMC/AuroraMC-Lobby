/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.changelog;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.gui.stats.achievements.game.GameAchievementListing;
import net.auroramc.lobby.api.LobbyAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Changelogs extends GUI {

    private final AuroraMCPlayer player;

    public Changelogs(AuroraMCPlayer player) {
        super("&3&lChangelogs", 5, true);

        SimpleDateFormat format = new SimpleDateFormat("dd MMMMMMMMM yyyy");
        this.border("&3&lChangelogs", null);
        this.player = player;

        this.setItem(1, 4, new GUIItem(Material.EXP_BOTTLE, "&b&lMiscellaneous Changelogs", 1, ";&rLast Update: **" + ((LobbyAPI.getLatestChangelog("MISC") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("MISC").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));

        this.setItem(2, 2, new GUIItem(Material.NETHER_STAR, "&b&lCrystal Quest", 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("CRYSTAL_QUEST") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("CRYSTAL_QUEST") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("CRYSTAL_QUEST").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 3, new GUIItem(Material.IRON_SWORD, "&c&lBackstab", 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("BACKSTAB") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("BACKSTAB") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("BACKSTAB").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 4, new GUIItem(Material.SNOW_BALL, "&a&lPaintball", 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("PAINTBALL") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("PAINTBALL") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("PAINTBALL").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 5, new GUIItem(Material.IRON_SPADE, "&b&lSpleef", 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("SPLEEF") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("SPLEEF") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("SPLEEF").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 6, new GUIItem(Material.BAKED_POTATO, "&c&lHotPotato", 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("HOTPOTATO") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("HOTPOTATO") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("HOTPOTATO").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 3, new GUIItem(Material.IRON_AXE, "&c&lFFA", 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("FFA") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("FFA") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("FFA").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 4, new GUIItem(Material.COBBLE_WALL, "&c&lHole In The Wall", 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("HOLE_IN_THE_WALL") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("HOLE_IN_THE_WALL") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("HOLE_IN_THE_WALL").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 5, new GUIItem(Material.NOTE_BLOCK, AuroraMCAPI.getFormatter().rainbowBold("Block Party"), 1, ";&rLatest Version;&bv" + LobbyAPI.getVersionNumber("BLOCK_PARTY") + ";;&rLast Update: **" + ((LobbyAPI.getLatestChangelog("BLOCK_PARTY") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("BLOCK_PARTY").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        switch (item.getType()) {
            case NETHER_STAR: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "CRYSTAL_QUEST", "Crystal Quest", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case IRON_SWORD: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "BACKSTAB", "Backstab", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case SNOW_BALL: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "PAINTBALL", "Paintball", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case IRON_SPADE: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "SPLEEF", "Spleef", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case BAKED_POTATO: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "HOTPOTATO", "HotPotato", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case IRON_AXE: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "FFA", "FFA", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case COBBLE_WALL: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "HOLE_IN_THE_WALL", "Hole In The Wall", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case NOTE_BLOCK: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "BLOCK_PARTY", "Block Party", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            default: {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
            }
        }
    }
}
