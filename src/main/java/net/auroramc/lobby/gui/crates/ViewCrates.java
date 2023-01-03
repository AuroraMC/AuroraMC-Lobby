/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.core.cosmetics.crates.EmeraldCrate;
import net.auroramc.core.cosmetics.crates.GoldCrate;
import net.auroramc.core.cosmetics.crates.IronCrate;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.crates.buy.BuyDiamondCrate;
import net.auroramc.lobby.gui.crates.buy.BuyGoldCrate;
import net.auroramc.lobby.gui.crates.buy.BuyIronCrate;
import net.auroramc.lobby.gui.crates.open.DiamondCrateMenu;
import net.auroramc.lobby.gui.crates.open.EmeraldCrateMenu;
import net.auroramc.lobby.gui.crates.open.GoldCrateMenu;
import net.auroramc.lobby.gui.crates.open.IronCrateMenu;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

public class ViewCrates extends GUI {

    private final AuroraMCLobbyPlayer player;

    public ViewCrates(AuroraMCLobbyPlayer player) {
        super("&3&lCrate Menu", 2, true);
        border("&3&lCrate Menu", null);
        this.player = player;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/f7aadff9ddc546fdcec6ed5919cc39dfa8d0c07ff4bc613a19f2e6d7f2593\"}}}".getBytes())));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&7&lIron Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("&fYou have **" + player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof IronCrate).count() + "**&f crates to open.;;&7Iron Crates are the most common;&7and have the lowest chances to;&7win awesome stuff.;;&r&fCost: &d" + String.format("%,d", CrateUtil.IRON_CRATE_PRICE) +" Tickets;;&aLeft-click to view available crates!;&aRight-click to purchase!")).split(";")));
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
        this.setItem(1, 1, new GUIItem(head));

        profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/487ff5845bd61e98b16d21915f700edf454497acd3d5a7ae2dbefccacbd5abe3\"}}}".getBytes())));
        head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&6&lGold Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("&fYou have **" + player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof GoldCrate).count() + "**&f crates to open!;;&7Gold Crates are rare crates that;&7contain more legendary loot!;;&r&fCost: &d" + String.format("%,d",CrateUtil.GOLD_CRATE_PRICE) + " Tickets;;&aLeft-click to view available crates!;&aRight-click to purchase!")).split(";")));
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
        this.setItem(1, 3, new GUIItem(head));

        profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/25807cc4c3b6958aea6156e84518d91a49c5f32971e6eb269a23a25a27145\"}}}".getBytes())));
        head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&b&lDiamond Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("&fYou have **" + player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof DiamondCrate).count() + "**&f crates to open!;;&7Diamond Crates are legendary crates that;&7can not just give you cosmetics, but can;&7also give you a rank upgrade!;;&r&fCost: &d" + String.format("%,d",CrateUtil.DIAMOND_CRATE_PRICE) + " Tickets;;&aLeft-click to view available crates!;&aRight-click to purchase!")).split(";")));
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
        this.setItem(1, 5, new GUIItem(head));

        profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/f3d5e43de5d4177c4baf2f44161554473a3b0be5430998b5fcd826af943afe3\"}}}".getBytes())));
        head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&a&lEmerald Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("&fYou have **" + player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof EmeraldCrate).count() + "**&f crates to open!;;&7Emerald Crates are legendary crates that not;&7only give you a very high chance to find;&7legendary loot, but also contains no duplicates!;;&cNote: &r&fThese crates can only be obtained;&r&fby claiming a Plus Bonus from Cosmonaut Luna.;;&aLeft-click to view available crates!")).split(";")));
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
        this.setItem(1, 7, new GUIItem(head));
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() != Material.SKULL_ITEM) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
        } else {
            switch (column) {
                case 1: {
                    if (clickType.isLeftClick()) {
                        IronCrateMenu menu = new IronCrateMenu(player);
                        menu.open(player);
                        AuroraMCAPI.openGUI(player, menu);
                    } else if (clickType.isRightClick()) {
                        BuyIronCrate menu = new BuyIronCrate(player);
                        menu.open(player);
                        AuroraMCAPI.openGUI(player, menu);
                    }
                    break;
                }
                case 3:{
                    if (clickType.isLeftClick()) {
                        GoldCrateMenu menu = new GoldCrateMenu(player);
                        menu.open(player);
                        AuroraMCAPI.openGUI(player, menu);
                    } else if (clickType.isRightClick()) {
                        BuyGoldCrate menu = new BuyGoldCrate(player);
                        menu.open(player);
                        AuroraMCAPI.openGUI(player, menu);
                    }
                    break;
                }
                case 5:{
                    if (clickType.isLeftClick()) {
                        DiamondCrateMenu menu = new DiamondCrateMenu(player);
                        menu.open(player);
                        AuroraMCAPI.openGUI(player, menu);
                    } else if (clickType.isRightClick()) {
                        BuyDiamondCrate menu = new BuyDiamondCrate(player);
                        menu.open(player);
                        AuroraMCAPI.openGUI(player, menu);
                    }
                    break;
                }
                case 7: {
                    EmeraldCrateMenu menu = new EmeraldCrateMenu(player);
                    menu.open(player);
                    AuroraMCAPI.openGUI(player, menu);
                    break;
                }
            }
        }
    }
}
