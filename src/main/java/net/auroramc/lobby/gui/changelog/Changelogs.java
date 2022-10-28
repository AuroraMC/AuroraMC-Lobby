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

        this.setItem(1, 3, new GUIItem(Material.BEACON, "&b&lLobby Changelogs", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("LOBBY") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("LOBBY") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("LOBBY").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(1, 5, new GUIItem(Material.EXP_BOTTLE, "&b&lGeneral Changelogs", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("GENERAL") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("GENERAL") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("GENERAL").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));

        this.setItem(2, 2, new GUIItem(Material.NETHER_STAR, "&b&lCrystal Quest", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("CRYSTAL_QUEST") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("CRYSTAL_QUEST") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("CRYSTAL_QUEST").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 3, new GUIItem(Material.IRON_SWORD, "&c&lDuels", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("DUELS") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("DUELS") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("DUELS").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 4, new GUIItem(Material.SNOW_BALL, "&a&lPaintball", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("PAINTBALL") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("PAINTBALL") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("PAINTBALL").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 5, new GUIItem(Material.IRON_SPADE, "&b&lSpleef", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("SPLEEF") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("SPLEEF") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("SPLEEF").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 6, new GUIItem(Material.BAKED_POTATO, "&c&lHot Potato", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("HOT_POTATO") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("HOT_POTATO") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("HOT_POTATO").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 3, new GUIItem(Material.IRON_AXE, "&c&lFFA", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("FFA") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("FFA") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("FFA").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 4, new GUIItem(Material.LEASH, "&c&lTag", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("TAG") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("TAG") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("TAG").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 5, new GUIItem(Material.STAINED_CLAY, "&e&lRun", 1, ";&r&fLatest Version;&bv" + LobbyAPI.getVersionNumber("RUN") + ";;&r&fLast Update: **" + ((LobbyAPI.getLatestChangelog("RUN") != null)?format.format(new Date(LobbyAPI.getLatestChangelog("RUN").getTimestamp())):"None") + "**;;&aClick to view more changelogs!"));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        switch (item.getType()) {
            case EXP_BOTTLE: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "GENERAL", "General", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case BEACON: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "LOBBY", "Lobby", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case NETHER_STAR: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "CRYSTAL_QUEST", "Crystal Quest", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case IRON_SWORD: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "DUELS", "Duels", item);
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
                ChangelogListing stats = new ChangelogListing(player, "HOT_POTATO", "Hot Potato", item);
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
            case STAINED_CLAY: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "RUN", "Run", item);
                stats.open(player);
                AuroraMCAPI.openGUI(player, stats);
                break;
            }
            case LEASH: {
                AuroraMCAPI.closeGUI(player);
                ChangelogListing stats = new ChangelogListing(player, "TAG", "Tag", item);
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
