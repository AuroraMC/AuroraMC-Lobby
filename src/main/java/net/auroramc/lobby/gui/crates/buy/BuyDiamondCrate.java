/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates.buy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.crates.buy.confirmation.ConfirmDiamond;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class BuyDiamondCrate extends GUI {

    private AuroraMCLobbyPlayer player;

    public BuyDiamondCrate(AuroraMCLobbyPlayer player) {
        super("&b&lBuy Diamond Crates", 4, true);
        border("&b&lBuy Diamond Crates", null);

        this.player = player;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/25807cc4c3b6958aea6156e84518d91a49c5f32971e6eb269a23a25a27145\"}}}".getBytes())));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        ItemMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&b&lDiamond Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight(";&7Diamond Crates are legendary crates that;&7can not just give you cosmetics, but can;&7also give you a rank upgrade!")).split(";")));
        Field field;
        try {
            field = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return;
        }
        field.setAccessible(true);
        try {
            field.set(meta, profile);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        this.setItem(0, 4, new GUIItem(head));

        ItemStack one = head.clone();
        meta = one.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&b&l1 Diamond Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight(";&rCost: &d" + String.format("%,d",CrateUtil.DIAMOND_CRATE_PRICE) + " Tickets;;&aClick here to purchase 1 Diamond Crate!")).split(";")));
        one.setItemMeta(meta);
        this.setItem(2, 2, new GUIItem(one));

        ItemStack five = head.clone();
        five.setAmount(5);
        meta = five.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&b&l5 Diamond Crates")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight(";&rCost: &d" + String.format("%,d",CrateUtil.DIAMOND_CRATE_PRICE * 5) + " Tickets;;&aClick here to purchase 5 Diamond Crates!")).split(";")));
        five.setItemMeta(meta);
        this.setItem(2, 4, new GUIItem(five));

        ItemStack ten = head.clone();
        ten.setAmount(10);
        meta = ten.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&b&l10 Diamond Crates")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight(";&rCost: &d" + String.format("%,d",CrateUtil.DIAMOND_CRATE_PRICE * 10) + " Tickets;;&aClick here to purchase 10 Diamond Crates!")).split(";")));
        ten.setItemMeta(meta);
        this.setItem(2, 6, new GUIItem(ten));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        switch (column) {
            case 2: {
                if (player.getBank().getTickets() >= CrateUtil.DIAMOND_CRATE_PRICE) {
                    ConfirmDiamond diamond = new ConfirmDiamond(player, 1);
                    diamond.open(player);
                    AuroraMCAPI.openGUI(player, diamond);
                } else {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
                break;
            }
            case 4: {
                if (player.getBank().getTickets() >= CrateUtil.DIAMOND_CRATE_PRICE * 5) {
                    ConfirmDiamond diamond = new ConfirmDiamond(player, 5);
                    diamond.open(player);
                    AuroraMCAPI.openGUI(player, diamond);
                } else {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
                break;
            }
            case 6: {
                if (player.getBank().getTickets() >= CrateUtil.DIAMOND_CRATE_PRICE * 10) {
                    ConfirmDiamond diamond = new ConfirmDiamond(player, 10);
                    diamond.open(player);
                    AuroraMCAPI.openGUI(player, diamond);
                } else {
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
                break;
            }
            default: {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
            }
        }
    }
}
