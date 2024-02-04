/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.gui.changelog;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.util.Changelog;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChangelogListing extends GUI {

    private final AuroraMCServerPlayer player;
    private final String game;


    public ChangelogListing(AuroraMCServerPlayer player, String game, String gameName, ItemStack item) {
        super("&3&lChangelogs for " + gameName, 5, true);

        this.player = player;
        this.game = game;

        SimpleDateFormat format = new SimpleDateFormat("dd MMMMMMMMM yyyy");
        this.border("&3&lChangelogs for " + gameName, null);
        this.setItem(0, 4, new GUIItem(item));
        this.setItem(0, 0, new GUIItem(Material.ARROW, "&3&lBACK"));


        int row = 1;
        int column = 1;
        if (LobbyAPI.getChangelogs().get(game) != null) {
            for (Changelog changelog : LobbyAPI.getChangelogs().get(game)) {
                this.setItem(row, column, new GUIItem(Material.PAPER, "&3&l" + changelog.getUpdateTitle(), 1, ";&r&fVersion:;&b" + changelog.getVersion() + ";;&r&fReleased:;&b" + format.format(new Date(changelog.getTimestamp())) + ";;&aClick to open the changelog!"));
                column++;
                if (column == 8) {
                    row++;
                    column = 1;
                    if (row == 5) {
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() == Material.PAPER) {
            String version = ChatColor.stripColor(item.getItemMeta().getLore().get(2));
            for (Changelog changelog : LobbyAPI.getChangelogs().get(game)) {
                if (changelog.getVersion().equals(version)) {
                    player.closeInventory();
                    TextComponent component = new TextComponent("Click here to view the changelog!");
                    component.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                    component.setBold(true);
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, changelog.getUrl()));
                    ComponentBuilder componentHover = new ComponentBuilder(TextFormatter.convert("&3Click here to open the changelog!"));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentHover.create()));
                    player.sendMessage(component);
                    return;
                }
            }
        } else if (item.getType() == Material.ARROW) {
            Changelogs logs = new Changelogs(player);
            logs.open(player);
        } else {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
        }
    }
}
