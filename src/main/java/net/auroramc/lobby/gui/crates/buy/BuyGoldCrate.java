/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates.buy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.crates.buy.confirmation.ConfirmGold;
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
import java.util.logging.Level;

public class BuyGoldCrate extends GUI {

    private AuroraMCLobbyPlayer player;

    public BuyGoldCrate(AuroraMCLobbyPlayer player) {
        super("&6&lBuy Gold Crates", 4, true);
        border("&6&lBuy Gold Crates", null);

        this.player = player;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/487ff5845bd61e98b16d21915f700edf454497acd3d5a7ae2dbefccacbd5abe3\"}}}".getBytes())));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        ItemMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&6&lGold Crate"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&7Gold Crates are rare crates that;&7contain more legendary loot!")).split(";")));
        Field field;
        try {
            field = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
            return;
        }
        field.setAccessible(true);
        try {
            field.set(meta, profile);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
        }
        head.setItemMeta(meta);
        this.setItem(0, 4, new GUIItem(head));

        ItemStack one = head.clone();
        meta = one.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&6&l1 Gold Crate"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&r&fCost: &d" + String.format("%,d",CrateUtil.GOLD_CRATE_PRICE) + " Tickets;;&aClick here to purchase 1 Gold Crate!")).split(";")));
        one.setItemMeta(meta);
        this.setItem(2, 2, new GUIItem(one));

        ItemStack five = head.clone();
        five.setAmount(5);
        meta = five.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&6&l5 Gold Crates"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&r&fCost: &d" + String.format("%,d",CrateUtil.GOLD_CRATE_PRICE * 5) + " Tickets;;&aClick here to purchase 5 Gold Crates!")).split(";")));
        five.setItemMeta(meta);
        this.setItem(2, 4, new GUIItem(five));

        ItemStack ten = head.clone();
        ten.setAmount(10);
        meta = ten.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&6&l10 Gold Crates"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&r&fCost: &d" + String.format("%,d",CrateUtil.GOLD_CRATE_PRICE * 10) + " Tickets;;&aClick here to purchase 10 Gold Crates!")).split(";")));
        ten.setItemMeta(meta);
        this.setItem(2, 6, new GUIItem(ten));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        switch (column) {
            case 2: {
                if (player.getBank().getTickets() >= CrateUtil.GOLD_CRATE_PRICE) {
                    ConfirmGold diamond = new ConfirmGold(player, 1);
                    diamond.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
                break;
            }
            case 4: {
                if (player.getBank().getTickets() >= CrateUtil.GOLD_CRATE_PRICE * 5) {
                    ConfirmGold diamond = new ConfirmGold(player, 1);
                    diamond.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
            }
            case 6: {
                if (player.getBank().getTickets() >= CrateUtil.GOLD_CRATE_PRICE * 10) {
                    ConfirmGold diamond = new ConfirmGold(player, 1);
                    diamond.open(player);
                } else {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
                }
            }
            default: {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            }
        }
    }
}
