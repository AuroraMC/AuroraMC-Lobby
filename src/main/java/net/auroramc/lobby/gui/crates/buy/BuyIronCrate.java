/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.gui.crates.buy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.crates.buy.confirmation.ConfirmIron;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class BuyIronCrate extends GUI {

    private AuroraMCLobbyPlayer player;

    public BuyIronCrate(AuroraMCLobbyPlayer player) {
        super("&7&lBuy Iron Crates", 4, true);
        border("&7&lBuy Iron Crates", null);

        this.player = player;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/f7aadff9ddc546fdcec6ed5919cc39dfa8d0c07ff4bc613a19f2e6d7f2593\"}}}".getBytes())));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&7&lIron Crate"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&7Iron Crates are the most common;&7and have the lowest chances to;&7win awesome stuff.")).split(";")));
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
        meta.setDisplayName(TextFormatter.convert("&7&l1 Iron Crate"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&r&fCost: &d" + String.format("%,d",CrateUtil.IRON_CRATE_PRICE) + " Tickets;;&aClick here to purchase 1 Iron Crate!")).split(";")));
        one.setItemMeta(meta);
        this.setItem(2, 2, new GUIItem(one));

        ItemStack five = head.clone();
        five.setAmount(5);
        meta = five.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&7&l5 Iron Crates"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&r&fCost: &d" + String.format("%,d",CrateUtil.IRON_CRATE_PRICE * 5) + " Tickets;;&aClick here to purchase 5 Iron Crates!")).split(";")));
        five.setItemMeta(meta);
        this.setItem(2, 4, new GUIItem(five));

        ItemStack ten = head.clone();
        ten.setAmount(10);
        meta = ten.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&7&l10 Iron Crates"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&r&fCost: &d" + String.format("%,d",CrateUtil.IRON_CRATE_PRICE * 10) + " Tickets;;&aClick here to purchase 10 Iron Crates!")).split(";")));
        ten.setItemMeta(meta);
        this.setItem(2, 6, new GUIItem(ten));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        switch (column) {
            case 2: {
                if (player.getBank().getTickets() >= CrateUtil.IRON_CRATE_PRICE) {
                    ConfirmIron diamond = new ConfirmIron(player, 1);
                    diamond.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
                break;
            }
            case 4: {
                if (player.getBank().getTickets() >= CrateUtil.IRON_CRATE_PRICE * 5) {
                    ConfirmIron diamond = new ConfirmIron(player, 5);
                    diamond.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
                break;
            }
            case 6: {
                if (player.getBank().getTickets() >= CrateUtil.IRON_CRATE_PRICE * 10) {
                    ConfirmIron diamond = new ConfirmIron(player, 10);
                    diamond.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
                break;
            }
            default: {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            }
        }
    }
}
