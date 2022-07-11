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
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.CrateStructures;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Chest;
import org.bukkit.material.Stairs;
import org.bukkit.scheduler.BukkitRunnable;

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
            player.getPlayer().closeInventory();

            if (LobbyAPI.startOpen(crate, player)) {
                Location location = LobbyAPI.getChestBlock().getLocation();
                for (Entity entity : location.getWorld().getNearbyEntities(location, 2, 2, 2)) {
                    if (entity.getEntityId() == LobbyAPI.getChestStand().getEntityId() || entity instanceof ArmorStand) {
                        entity.remove();
                    }
                }
                location.getBlock().setType(Material.AIR);
                player.getPlayer().teleport(location.add(0.5, 0, 0.5));
                Location loc = new Location(location.getWorld(), location.getX() - 3, location.getY() - 1, location.getZ() - 3);

                CrateStructures.getDiamondCrate().place(loc);

                Location chest = new Location(location.getWorld(), location.getX(), location.getY() + 3, location.getZ() + 3);
                chest.getBlock().setType(Material.CHEST);
                chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                new BukkitRunnable(){
                    byte i = 2;
                    @Override
                    public void run() {
                        if (!player.getPlayer().isOnline()) {
                            this.cancel();
                            return;
                        }
                        if (i < 0) {
                            chest.setZ(chest.getZ() - 6);
                            chest.setY(chest.getY() + 3);

                            chest.getBlock().setType(Material.CHEST);
                            BlockState c = chest.getBlock().getState();
                            c.setData(new Chest(BlockFace.SOUTH));
                            c.update();
                            chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                            new BukkitRunnable(){
                                byte i = 2;
                                @Override
                                public void run() {
                                    if (!player.getPlayer().isOnline()) {
                                        this.cancel();
                                        return;
                                    }
                                    if (i  < 0) {
                                        chest.setX(chest.getX() + 3);
                                        chest.setZ(chest.getZ() + 3);
                                        chest.setY(chest.getY() + 3);

                                        chest.getBlock().setType(Material.CHEST);
                                        BlockState c = chest.getBlock().getState();
                                        c.setData(new Chest(BlockFace.WEST));
                                        c.update();
                                        chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                                        new BukkitRunnable(){
                                            byte i = 2;
                                            @Override
                                            public void run() {
                                                if (!player.getPlayer().isOnline()) {
                                                    this.cancel();
                                                    return;
                                                }
                                                if (i < 0) {
                                                    chest.setX(chest.getX() - 6);
                                                    chest.setY(chest.getY() + 3);

                                                    chest.getBlock().setType(Material.CHEST);
                                                    BlockState c = chest.getBlock().getState();
                                                    c.setData(new Chest(BlockFace.EAST));
                                                    c.update();
                                                    chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                                                    new BukkitRunnable(){
                                                        byte i = 2;
                                                        @Override
                                                        public void run() {
                                                            if (!player.getPlayer().isOnline()) {
                                                                this.cancel();
                                                                return;
                                                            }
                                                            if (i < 0) {
                                                                LobbyAPI.crateAnimationFinished();
                                                                this.cancel();
                                                                return;
                                                            }
                                                            chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                                                            chest.getBlock().setType(Material.AIR);
                                                            chest.setY(chest.getY() - 1);
                                                            chest.getBlock().setType(Material.CHEST);
                                                            BlockState c = chest.getBlock().getState();
                                                            c.setData(new Chest(BlockFace.EAST));
                                                            c.update();
                                                            i--;
                                                        }
                                                    }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
                                                    this.cancel();
                                                    return;
                                                }
                                                chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                                                chest.getBlock().setType(Material.AIR);
                                                chest.setY(chest.getY() - 1);
                                                chest.getBlock().setType(Material.CHEST);
                                                BlockState c = chest.getBlock().getState();
                                                c.setData(new Chest(BlockFace.WEST));
                                                c.update();
                                                i--;
                                            }
                                        }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
                                        this.cancel();
                                        return;
                                    }
                                    chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                                    chest.getBlock().setType(Material.AIR);
                                    chest.setY(chest.getY() - 1);
                                    chest.getBlock().setType(Material.CHEST);
                                    BlockState c = chest.getBlock().getState();
                                    c.setData(new Chest(BlockFace.SOUTH));
                                    c.update();
                                    i--;
                                }
                            }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
                            this.cancel();
                            return;
                        }
                        chest.getWorld().playSound(chest, Sound.WOOD_CLICK, 100, 1);
                        chest.getBlock().setType(Material.AIR);
                        chest.setY(chest.getY() - 1);
                        chest.getBlock().setType(Material.CHEST);
                        i--;
                    }
                }.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
            } else {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "Someone is already opening a crate! Please wait until they are finished to open one!"));
            }
        }
    }

    private void placeCorner(Location loc) {
        loc.getBlock().setType(Material.DIAMOND_BLOCK);
        loc.setY(loc.getY() + 1);
        loc.getBlock().setType(Material.RED_SANDSTONE);
        loc.getBlock().setData((byte)1);
        loc.setY(loc.getY() + 1);
        loc.getBlock().setType(Material.STAINED_CLAY);
        loc.getBlock().setData((byte)1);
        loc.setY(loc.getY() + 1);
        loc.getBlock().setType(Material.STONE_SLAB2);
    }
}
