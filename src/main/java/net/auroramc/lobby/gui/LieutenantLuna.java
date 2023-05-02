/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.api.permissions.Rank;
import net.auroramc.api.punishments.PunishmentLength;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
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

public class LieutenantLuna extends GUI {

    private final static ItemStack head;

    static {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/f60e33fb112437571888d217227c3b3b6dbc7d58558f4b1f0e5af70db3afc309\"}}}".getBytes())));

        head = new GUIItem(Material.SKULL_ITEM, "&3&lDiscord", 1, ";&r&fThe AuroraMC Discord is the main;&r&fcommunication platform that is used by;&r&fAuroraMC. If you want to interact with;&r&fmembers of the community, don't hesitate;&r&fto join the discord!;;&aClick to get the link!", (short)3).getItemStack();
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

    public LieutenantLuna(AuroraMCLobbyPlayer player) {
        super("&6&lLieutenant &e&lLuna", 5, true);
        this.border("&6&lLieutenant &e&lLuna", null);
        this.player = player;

        this.setItem(0, 4, new GUIItem(Material.EMPTY_MAP, "&3&lChangelogs", 1, ";&r&fLatest Update:;&b" + ((LobbyAPI.getLatestChangelog() != null)?LobbyAPI.getLatestChangelog().getUpdateTitle():"None") + ";;&aClick to view more changelogs!"));

        boolean claimDaily = player.canClaimDaily();
        boolean claimMonthly = player.canClaimMonthly();
        boolean claimPlus = player.canClaimPlus();

        if (claimDaily) {
            this.setItem(2, 2, new GUIItem(Material.IRON_BLOCK, "&3&lLoyalty Bonus", 1, ";&r&fClaim your daily bonus for:;&6+100 Crowns;&d+100 Tickets;&a+100 XP;;&r&fDaily bonuses claimed: **" + player.getDailyBonusClaimed() + "**;&r&fCurrent Streak: **" + player.getDailyStreak() + "**" + ((player.getDailyStreak() > 0)?";&r&fStreak Expires: **" + (new PunishmentLength((129600000 - (System.currentTimeMillis() - player.getLastDailyBonus()))/3600000d)) + "**":"") + ";&r&fHighest Streak: **" + player.getStats().getStatistic(0, "streak") + "**;;&aClick to claim!"));
        } else {
            this.setItem(2, 2, new GUIItem(Material.REDSTONE_BLOCK, "&3&lLoyalty Bonus", 1, ";&cYou have already claimed;&ctoday's bonus!;;&r&fDaily bonuses claimed: **" + player.getDailyBonusClaimed() + "**;&r&fCurrent Streak: **" + player.getDailyStreak() + "**" + ((player.getDailyStreak() > 0)?";&r&fStreak Expires: **" + (new PunishmentLength((129600000 - (System.currentTimeMillis() - player.getLastDailyBonus()))/3600000d)) + "**":"") + ";&r&fHighest Streak: **" + player.getStats().getStatistic(0, "streak") + "**;;&r&fCome back tomorrow to claim again!"));
        }
        if (claimMonthly) {
            String reward;
            switch (player.getRank()) {
                case PLAYER: {
                    reward = "&7+1 Iron Crate;&6+1,000 Crowns;&d+1,000 Tickets";
                    break;
                }
                case ELITE:{
                    reward = "&7+2 Iron Crates;&6+1 Gold Crate;&6+2,500 Crowns;&d+2,500 Tickets";
                    break;
                }
                default: {
                    reward = "&6+1 Gold Crate;&b+2 Diamond Crates;&6+5,000 Crowns;&d+5,000 Tickets";
                    break;
                }
            }
            this.setItem(2, 4, new GUIItem(Material.DIAMOND_BLOCK, "&3&lMonthly Bonus", 1, "&r&fRank: " + ((player.getRank() == Rank.PLAYER)?"§7":player.getRank().getPrefixColor()) + player.getRank().getName() + ";;&r&fBonus:;" + reward + ";;&aClick to claim!"));
        } else {
            this.setItem(2, 4, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months bonus!;;&r&fCome back next month to claim again!"));
        }
        if (claimPlus) {
            this.setItem(2, 6, new GUIItem(Material.EMERALD_BLOCK, "&3&lPlus Bonus", 1, ";&r&fClaim your Plus bonus for:;&b+1 Diamond Crate;&a+1 Emerald Crate;&6+5,000 Crowns;&d+5,000 Tickets;;&aClick to claim!"));
        } else {
            this.setItem(2, 6, new GUIItem(Material.REDSTONE_BLOCK, "&3&lPlus Bonus", 1, ";&cYou have already claimed;&cthis months Plus bonus!;;&r&fCome back in 30 days to claim again!"));
        }

        if (LobbyAPI.getPoll() == null || LobbyDatabaseManager.hasVoted(LobbyAPI.getPoll().getId(), player.getId())) {
            this.setItem(3, 3, new GUIItem(Material.BOOK, "&3&lCommunity Poll", 1, ";&r&fCommunity Polls are a way for;&r&four Community Management team to;&r&fget feedback on the network!;;" + ((LobbyAPI.getPoll() == null)?"&cThere is currently no poll in progress!":"&cYou've already voted in the active poll!")));
        } else {
            this.setItem(3, 3, new GUIItem(Material.BOOK, "&3&lCommunity Poll", 1, ";&r&fCurrent Poll:;&b" + LobbyAPI.getPoll().getQuestion() + ";;&aClick to answer!"));
        }

        this.setItem(3, 5, new GUIItem(head));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        switch (item.getType()) {
            case IRON_BLOCK: {
                if (player.canClaimDaily()) {
                    player.claimDaily();
                    this.updateItem(2, 2, new GUIItem(Material.REDSTONE_BLOCK, "&3&lLoyalty Bonus", 1, ";&cYou have already claimed;&ctoday's bonus!;;&r&fDaily bonuses claimed: **" + player.getDailyBonusClaimed() + "**;&r&fCurrent Streak: **" + player.getDailyStreak() + "**" + ((player.getDailyStreak() > 0)?";&r&fStreak Expires: **" + (new PunishmentLength((129600000 - (System.currentTimeMillis() - player.getLastDailyBonus()))/3600000d)) + "**":"") + ";&r&fHighest Streak: **" + player.getStats().getStatistic(0, "streak") + "**;;&r&fCome back tomorrow to claim again!"));
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 1);
                }
                break;
            }
            case DIAMOND_BLOCK: {
                if (player.canClaimMonthly()) {
                    player.claimMonthly();
                    this.updateItem(2, 4, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months bonus!;;&r&fCome back next month to claim again!"));
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 1);
                }
                break;
            }
            case EMERALD_BLOCK: {
                if (player.canClaimPlus()) {
                    player.claimPlus();
                    this.updateItem(2, 6, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months Plus bonus!;;&r&fCome back in 30 days to claim again!"));
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 1);
                }
                break;
            }
            case BOOK: {
                if (LobbyAPI.getPoll() != null) {
                    if (!LobbyDatabaseManager.hasVoted(LobbyAPI.getPoll().getId(), player.getId())) {
                        Poll poll = new Poll(player);
                        poll.open(player);
                    } else {
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                        return;
                    }
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                    return;
                }
                break;
            }
            case SKULL_ITEM: {
                player.closeInventory();
                TextComponent component = new TextComponent("Click here to join the AuroraMC Discord!");
                component.setColor(ChatColor.GREEN);
                component.setBold(true);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.auroramc.net/"));
                ComponentBuilder componentHover = new ComponentBuilder(TextFormatter.convert("&3&lAuroraMC Discord\n"
                        + "\n"
                        + WordUtils.wrap("The AuroraMC Discord is the main communication platform that is used by," +
                        " AuroraMC. If you want to interact with members of the community, don't hesitate to join the discord!", 40, "\n&r&f", false)
                        + "\n\n&aClick here to join our discord."));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentHover.create()));
                player.sendMessage(component);
                break;
            }
            case EMPTY_MAP: {
                Changelogs logs = new Changelogs(player);
                logs.open(player);
                break;
            }
            default: {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            }
        }
    }
}
