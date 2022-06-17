/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.creates;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.core.cosmetics.crates.IronCrate;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiamondCrateMenu extends GUI {


    private final AuroraMCLobbyPlayer player;
    private final List<Crate> availableCrates;

    public DiamondCrateMenu(AuroraMCLobbyPlayer player) {
        super("&b&lDiamond Crates", 4, true);
        border("&b&lDiamond Crates", null);

        this.player = player;

         availableCrates = player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof DiamondCrate).collect(Collectors.toList());

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

        if (availableCrates.size() == 0) {
            this.setItem(2, 4, new GUIItem(Material.BARRIER, "&c&lYou do not have any available Diamond Crates.", 1, ";&7Purchase Diamond Crates using the main menu;&7or at store.auroramc.net!"));
        } else {
            int row = 1;
            int column = 1;
            for (Crate crate : availableCrates) {
                this.setItem(row, column, new GUIItem(Material.CHEST, "&b&lDiamond Crate", 1, ";&7UUID: " + crate.getUuid() + ";;&aClick to open!"));
                column++;
                if (column == 8) {
                    row++;
                    column = 1;
                    if (row == 4) {
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() != Material.CHEST) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
        } else {
            Crate crate = availableCrates.get(((row - 1) * 7) + (column - 1));
            //Do something
        }
    }
}
