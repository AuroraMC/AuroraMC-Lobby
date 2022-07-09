/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates.open;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.GoldCrate;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Chest;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GoldCrateMenu extends GUI {


    private final AuroraMCLobbyPlayer player;
    private final List<Crate> availableCrates;

    public GoldCrateMenu(AuroraMCLobbyPlayer player) {
        super("&6&lGold Crates", 4, true);
        border("&6&lGold Crates", null);
        this.player = player;

         availableCrates = player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof GoldCrate).collect(Collectors.toList());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/487ff5845bd61e98b16d21915f700edf454497acd3d5a7ae2dbefccacbd5abe3\"}}}".getBytes())));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        ItemMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&6&lGold Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight(";&7Gold Crates are rare crates that;&7contain more legendary loot!")).split(";")));
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
            this.setItem(2, 4, new GUIItem(Material.BARRIER, "&c&lYou do not have any available Gold Crates.", 1, ";&7Purchase Gold Crates using the main menu;&7or at store.auroramc.net!"));
        } else {
            int row = 1;
            int column = 1;
            for (Crate crate : availableCrates) {
                this.setItem(row, column, new GUIItem(Material.CHEST, "&6&lGold Crate", 1, ";&7UUID: " + crate.getUuid() + ";;&aClick to open!"));
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
            player.getPlayer().closeInventory();

            if (LobbyAPI.startOpen(crate, player)) {
                Location location = LobbyAPI.getChestBlock().getLocation();
                for (Entity entity : location.getWorld().getNearbyEntities(location, 2, 2, 2)) {
                    if (entity.getEntityId() == LobbyAPI.getChestStand().getEntityId()) {
                        entity.remove();
                    }
                }
                location.getBlock().setType(Material.AIR);
                Location loc = new Location(location.getWorld(), location.getX() + 3, location.getY() - 1, location.getZ() + 3);

                //Set Blocks
                loc.getBlock().setType(Material.GOLD_BLOCK);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.RED_SANDSTONE);
                loc.getBlock().setData((byte)2);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.STONE_SLAB2);
                loc.setX(loc.getX() - 6);
                loc.setY(loc.getY() - 2);
                loc.getBlock().setType(Material.GOLD_BLOCK);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.RED_SANDSTONE);
                loc.getBlock().setData((byte)2);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.STONE_SLAB2);
                loc.setZ(loc.getZ() - 6);
                loc.setY(loc.getY() - 2);
                loc.getBlock().setType(Material.GOLD_BLOCK);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.RED_SANDSTONE);
                loc.getBlock().setData((byte)2);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.STONE_SLAB2);
                loc.setX(loc.getX() + 6);
                loc.setY(loc.getY() - 2);
                loc.getBlock().setType(Material.GOLD_BLOCK);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.RED_SANDSTONE);
                loc.getBlock().setData((byte)2);
                loc.setY(loc.getY() + 1);
                loc.getBlock().setType(Material.STONE_SLAB2);

                //Reset loc.
                loc = location.clone();
                loc.setY(loc.getY() - 1);

                loc.setX(loc.getX() + 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);
                loc.setZ(loc.getZ() + 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);
                loc.setX(loc.getX() - 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);
                loc.setX(loc.getX() - 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);
                loc.setZ(loc.getZ() - 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);
                loc.setZ(loc.getZ() - 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);
                loc.setX(loc.getX() + 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);
                loc.setX(loc.getX() + 2);
                loc.getBlock().setType(Material.STAINED_CLAY);
                loc.getBlock().setData((byte)4);

                //Reset loc.
                loc = location.clone();
                loc.setY(loc.getY() - 1);

                loc.setX(loc.getX() + 1);
                loc.setZ(loc.getZ() + 1);
                loc.getBlock().setType(Material.GOLD_BLOCK);
                loc.setZ(loc.getZ() - 2);
                loc.getBlock().setType(Material.GOLD_BLOCK);
                loc.setX(loc.getX() - 2);
                loc.getBlock().setType(Material.GOLD_BLOCK);
                loc.setZ(loc.getZ() + 2);
                loc.getBlock().setType(Material.GOLD_BLOCK);


                Location chest = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 3);
                chest.getBlock().setType(Material.STATIONARY_LAVA);
                chest.getBlock().setData((byte)6);
                new BukkitRunnable(){
                    byte i = 4;
                    @Override
                    public void run() {
                        if (i < 0) {
                            chest.getBlock().setType(Material.CHEST);
                            player.getPlayer().playSound(chest, Sound.BLAZE_HIT, 100, 1);
                            chest.setZ(chest.getZ() - 6);

                            chest.getBlock().setType(Material.STATIONARY_LAVA);
                            chest.getBlock().setData((byte)6);
                            new BukkitRunnable(){
                                byte i = 4;
                                @Override
                                public void run() {
                                    if (i  < 0) {
                                        chest.getBlock().setType(Material.CHEST);
                                        BlockState c = chest.getBlock().getState();
                                        c.setData(new Chest(BlockFace.SOUTH));
                                        c.update();
                                        player.getPlayer().playSound(chest, Sound.BLAZE_HIT, 100, 1);
                                        chest.setX(chest.getX() + 3);
                                        chest.setZ(chest.getZ() + 3);

                                        chest.getBlock().setType(Material.STATIONARY_LAVA);
                                        chest.getBlock().setData((byte)6);
                                        new BukkitRunnable(){
                                            byte i = 4;
                                            @Override
                                            public void run() {
                                                if (i < 0) {
                                                    chest.getBlock().setType(Material.CHEST);
                                                    BlockState c = chest.getBlock().getState();
                                                    c.setData(new Chest(BlockFace.WEST));
                                                    c.update();
                                                    player.getPlayer().playSound(chest, Sound.BLAZE_HIT, 100, 1);
                                                    chest.setX(chest.getX() - 6);

                                                    chest.getBlock().setType(Material.STATIONARY_LAVA);
                                                    chest.getBlock().setData((byte)3);
                                                    new BukkitRunnable(){
                                                        byte i = 4;
                                                        @Override
                                                        public void run() {
                                                            if (i == -1) {
                                                                chest.getBlock().setType(Material.CHEST);
                                                                BlockState c = chest.getBlock().getState();
                                                                c.setData(new Chest(BlockFace.EAST));
                                                                c.update();
                                                                player.getPlayer().playSound(chest, Sound.BLAZE_HIT, 100, 1);
                                                                this.cancel();
                                                            }
                                                            chest.getBlock().setData(i);
                                                            i-=2;
                                                        }
                                                    }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
                                                    this.cancel();
                                                    return;
                                                }
                                                chest.getBlock().setData(i);
                                                i-=2;
                                            }
                                        }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
                                        this.cancel();
                                        return;
                                    }
                                    chest.getBlock().setData(i);
                                    i-=2;
                                }
                            }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
                            this.cancel();
                        }
                        chest.getBlock().setData(i);
                        i-=2;
                    }
                }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
            } else {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "Someone is already opening a crate! Please wait until they are finished to open one!"));
            }
        }
    }
}
