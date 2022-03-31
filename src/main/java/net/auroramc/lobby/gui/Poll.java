/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.util.CommunityPoll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Poll extends GUI {

    private AuroraMCPlayer player;

    public Poll(AuroraMCPlayer player) {
        super("&3&lCommunity Poll", 2, true);
        this.border("&3&lCommunity Poll", "");
        this.player = player;

        this.setItem(0, 4, new GUIItem(Material.BOOK, "&3&lCommunity Poll", 1, ";&rQuestion:;&b" + LobbyAPI.getPoll().getQuestion()));

        switch (LobbyAPI.getPoll().getAnswers().size()) {
            case 2: {
                int column = 3;
                for (Map.Entry<Integer, CommunityPoll.PollAnswer> entry : LobbyAPI.getPoll().getAnswers().entrySet()) {
                    this.setItem(1, column, new GUIItem(Material.PAPER,"&3&l" + entry.getValue().getId() + ") " + entry.getValue().getAnswer()));
                    column+=2;
                }
                break;
            }
            case 4: {
                int column = 1;
                for (Map.Entry<Integer, CommunityPoll.PollAnswer> entry : LobbyAPI.getPoll().getAnswers().entrySet()) {
                    this.setItem(1, column, new GUIItem(Material.PAPER,"&3&l" + entry.getValue().getId() + ") " + entry.getValue().getAnswer()));
                    column+=2;
                }
                break;
            }
            case 3: {
                int column = 2;
                for (Map.Entry<Integer, CommunityPoll.PollAnswer> entry : LobbyAPI.getPoll().getAnswers().entrySet()) {
                    this.setItem(1, column, new GUIItem(Material.PAPER,"&3&l" + entry.getValue().getId() + ") " + entry.getValue().getAnswer()));
                    column+=2;
                }
                break;
            }
        }
    }

    @Override
    public void onClick(int i, int i1, ItemStack item, ClickType clickType) {
        if (item.getType() == Material.PAPER) {
            int response = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split("[)]")[0]);
            LobbyDatabaseManager.setVote(LobbyAPI.getPoll().getId(), player.getId(), response);
            player.getPlayer().closeInventory();
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Polls", "Thank you for voting in this community poll! Your response was recorded!"));
        } else {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
        }
    }
}
