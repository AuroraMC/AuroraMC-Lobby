/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.permissions.Rank;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.changelog.Changelogs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class TheMonke extends GUI {

    private final static ItemStack head;

    static {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/f60e33fb112437571888d217227c3b3b6dbc7d58558f4b1f0e5af70db3afc309\"}}}".getBytes())));

        head = new GUIItem(Material.SKULL_ITEM, "&3&lDiscord", 1, ";&rThe AuroraMC Discord is the main;&rcommunication platform that is used by;&rAuroraMC. If you want to interact with;&rmembers of the community, don't hesitate;&rto join the discord!;;&aClick to get the link!", (short)3).getItem();
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        Field field;
        try {
            field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            try {
                field.set(meta, profile);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
            head.setItemMeta(meta);
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }

    private final AuroraMCLobbyPlayer player;

    public TheMonke(AuroraMCLobbyPlayer player) {
        super("&3&lThe Monke", 5, true);
        this.border("&3&lThe Monke", null);
        this.player = player;

        this.setItem(0, 4, new GUIItem(Material.EMPTY_MAP, "&3&lChangelogs", 1, ";&rLatest Update:;&b" + ((LobbyAPI.getLatestChangelog() != null)?LobbyAPI.getLatestChangelog().getUpdateTitle():"None") + ";;&aClick to view more changelogs!"));

        boolean claimDaily = player.canClaimDaily();
        boolean claimMonthly = player.canClaimMonthly();
        boolean claimPlus = player.canClaimPlus();

        if (claimDaily) {
            this.setItem(2, 2, new GUIItem(Material.IRON_BLOCK, "&3&lLoyalty Bonus", 1, ";&rClaim your daily bonus for:;&6+100 Crowns;&d+100 Tickets;&a+100 XP;;&rDaily bonuses claimed: **" + player.getDailyBonusClaimed() + "**;&aClick to claim!"));
        } else {
            this.setItem(2, 2, new GUIItem(Material.REDSTONE_BLOCK, "&3&lLoyalty Bonus", 1, ";&cYou have already claimed;&ctoday's bonus!;;&rCome back tomorrow to claim again!"));
        }
        if (claimMonthly) {
            this.setItem(2, 4, new GUIItem(Material.DIAMOND_BLOCK, "&3&lMonthly Bonus", 1, "&rRank: &" + ((player.getRank() == Rank.PLAYER)?'7':player.getRank().getPrefixColor()) + player.getRank().getName() + ";;&rBonus:;&6+10000 Crowns;&d+10000 Tickets;;&aClick to claim!"));
        } else {
            this.setItem(2, 4, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months bonus!;;&rCome back next month to claim again!"));
        }
        if (claimPlus) {
            this.setItem(2, 6, new GUIItem(Material.EMERALD_BLOCK, "&3&lPlus Bonus", 1, ";&rClaim your Plus bonus for:;&6+10000 Crowns;&d+10000 Tickets;;&aClick to claim!"));
        } else {
            this.setItem(2, 6, new GUIItem(Material.REDSTONE_BLOCK, "&3&lPlus Bonus", 1, ";&cYou have already claimed;&cthis months Plus bonus!;;&rCome back in 30 days to claim again!"));
        }

        if (LobbyAPI.getPoll() == null || LobbyDatabaseManager.hasVoted(LobbyAPI.getPoll().getId(), player.getId())) {
            this.setItem(3, 3, new GUIItem(Material.BOOK, "&3&lCommunity Poll", 1, ";&rCommunity Polls are a way for;&rour Community Management team to;&rget feedback on the network!;;" + ((LobbyAPI.getPoll() == null)?"&cThere is currently no poll in progress!":"&cYou've already voted in the active poll!")));
        } else {
            this.setItem(3, 3, new GUIItem(Material.BOOK, "&3&lCommunity Poll", 1, ";&rCurrent Poll:;&b" + LobbyAPI.getPoll().getQuestion() + ";;&aClick to answer!"));
        }

        this.setItem(3, 5, new GUIItem(head));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        switch (item.getType()) {
            case IRON_BLOCK: {
                if (player.canClaimDaily()) {
                    player.claimDaily();
                    this.updateItem(2, 2, new GUIItem(Material.REDSTONE_BLOCK, "&3&lLoyalty Bonus", 1, ";&cYou have already claimed;&ctoday's bonus!;;&rCome back tomorrow to claim again!"));
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 1);
                }
                break;
            }
            case DIAMOND_BLOCK: {
                if (player.canClaimMonthly()) {
                    player.claimMonthly();
                    this.updateItem(2, 4, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months bonus!;;&rCome back next month to claim again!"));
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 1);
                }
                break;
            }
            case EMERALD_BLOCK: {
                if (player.canClaimPlus()) {
                    player.claimPlus();
                    this.updateItem(2, 6, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months Plus bonus!;;&rCome back in 30 days to claim again!"));
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 1);
                }
                break;
            }
            case BOOK: {
                if (LobbyAPI.getPoll() != null) {
                    if (!LobbyDatabaseManager.hasVoted(LobbyAPI.getPoll().getId(), player.getId())) {
                        Poll poll = new Poll(player);
                        AuroraMCAPI.closeGUI(player);
                        poll.open(player);
                        AuroraMCAPI.openGUI(player, poll);
                    } else {
                        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
                        return;
                    }
                } else {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
                    return;
                }
                break;
            }
            case SKULL_ITEM: {
                player.getPlayer().closeInventory();
                TextComponent component = new TextComponent("Click here to join the AuroraMC Discord!");
                component.setColor(ChatColor.GREEN);
                component.setBold(true);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.auroramc.net/"));
                ComponentBuilder componentHover = new ComponentBuilder(AuroraMCAPI.getFormatter().convert("&3&lAuroraMC Discord\n"
                        + "\n"
                        + WordUtils.wrap("The AuroraMC Discord is the main communication platform that is used by," +
                        " AuroraMC. If you want to interact with members of the community, don't hesitate to join the discord!", 40, "\n&r", false)
                        + "\n\n&aClick here to join our discord."));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentHover.create()));
                player.getPlayer().spigot().sendMessage(component);
                break;
            }
            case EMPTY_MAP: {
                Changelogs logs = new Changelogs(player);
                AuroraMCAPI.closeGUI(player);
                logs.open(player);
                AuroraMCAPI.openGUI(player, logs);
                break;
            }
            default: {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
            }
        }
    }
}
