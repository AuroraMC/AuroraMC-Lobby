/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.changelog;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;

public class Changelogs extends GUI {

    private AuroraMCPlayer player;

    public Changelogs(AuroraMCPlayer player) {
        super("&3&lChangelogs", 5, true);

        SimpleDateFormat format = new SimpleDateFormat("dd MMMMMMMMM yyyy");


        this.setItem(2, 2, new GUIItem(Material.EXP_BOTTLE, "&b&lMiscellaneous Changelogs", 1, ";&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("MISC").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));

        this.setItem(2, 2, new GUIItem(Material.NETHER_STAR, "&b&lCrystal Quest", 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("CRYSTAL_QUEST") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("CRYSTAL_QUEST").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 3, new GUIItem(Material.IRON_SWORD, "&c&lBackstab", 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("BACKSTAB") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("BACKSTAB").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 4, new GUIItem(Material.SNOW_BALL, "&a&lPaintball", 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("PAINTBALL") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("PAINTBALL").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 5, new GUIItem(Material.IRON_SPADE, "&b&lSpleef", 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("SPLEEF") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("SPLEEF").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
        this.setItem(2, 6, new GUIItem(Material.BAKED_POTATO, "&c&lHotPotato", 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("HOTPOTATO") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("HOTPOTATO").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 3, new GUIItem(Material.IRON_AXE, "&c&lFFA", 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("FFA") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("FFA").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 4, new GUIItem(Material.COBBLE_WALL, "&c&lHole In The Wall", 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("HOLE_IN_THE_WALL") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("HOLE_IN_THE_WALL").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
        this.setItem(3, 5, new GUIItem(Material.NOTE_BLOCK, AuroraMCAPI.getFormatter().rainbowBold("Block Party"), 1, ";&rLatest Version;&b" + LobbyAPI.getVersionNumber("BLOCK_PARTY") + ";;&rLast Update: **" + format.format(new Date(LobbyAPI.getChangelogs().get("BLOCK_PARTY").get(0).getTimestamp())) + "**;;&aClick to view more changelogs!"));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {

    }
}
