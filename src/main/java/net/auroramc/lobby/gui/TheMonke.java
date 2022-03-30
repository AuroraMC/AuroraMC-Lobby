/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.Material;
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

    public TheMonke(AuroraMCLobbyPlayer player) {
        super("&3&lThe Monke", 5, true);
        this.border("&3&lThe Monke", null);

        this.setItem(0, 4, new GUIItem(Material.MAP, "&3&lChangelogs", 1, ";&rLatest Update:;&b" + ((LobbyAPI.getLatestChangelog() != null)?LobbyAPI.getLatestChangelog().getUpdateTitle():"None") + ";;&aClick to view more changelogs!"));

        boolean claimDaily = player.canClaimDaily();
        boolean claimMonthly = player.canClaimMonthly();
        boolean claimPlus = player.canClaimPlus();

        if (claimDaily) {
            this.setItem(2, 2, new GUIItem(Material.IRON_BLOCK, "&3&lLoyalty Bonus", 1, ";&rClaim your daily bonus for:;&6+100 Crowns;&d+100 Tickets;&a+100 XP;;&rDaily bonuses claimed: **" + player.getDailyBonusClaimed() + "**;&aClick to claim!"));
        } else {
            this.setItem(2, 2, new GUIItem(Material.REDSTONE_BLOCK, "&3&lLoyalty Bonus", 1, ";&cYou have already claimed;&ctoday's bonus!;;&rCome back tomorrow to claim again!"));
        }
        if (claimMonthly) {
            this.setItem(2, 4, new GUIItem(Material.DIAMOND_BLOCK, "&3&lMonthly Bonus", 1, "&rRank: &" + player.getRank().getPrefixColor() + player.getRank().getName() + ";&rBonus:;&6+10000 Crowns;&d+10000 Tickets;;&aClick to claim!"));
        } else {
            this.setItem(2, 4, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months bonus!;;&rCome back next month to claim again!"));
        }
        if (claimPlus) {
            this.setItem(2, 4, new GUIItem(Material.EMERALD_BLOCK, "&3&lPlus Bonus", 1, ";&rClaim your Plus bonus for:;&6+10000 Crowns;&d+10000 Tickets;;&aClick to claim!"));
        } else {
            this.setItem(2, 4, new GUIItem(Material.REDSTONE_BLOCK, "&3&lMonthly Bonus", 1, ";&cYou have already claimed;&cthis months Plus bonus!;;&rCome back in 30 days to claim again!"));
        }

        if (LobbyAPI.getPoll() == null) {
            this.setItem(3, 3, new GUIItem(Material.BOOK, "&3&lCommunity Poll", 1, ";&rCommunity Polls are a way for;&rour Community Management team to;&rget feedback on the network!;;&cThere is currently no poll in progress!"));
        } else {
            this.setItem(3, 3, new GUIItem(Material.BOOK, "&3&lCommunity Poll", 1, ";&rCurrent Poll:;&b" + LobbyAPI.getPoll().getQuestion() + ";;&aClick to answer!"));
        }

        this.setItem(3, 5, new GUIItem(head));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {

    }
}
