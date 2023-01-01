/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates.open;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.EmeraldCrate;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.CrateStructures;
import net.auroramc.lobby.api.util.structure.block.StructureChest;
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

public class EmeraldCrateMenu extends GUI {


    private final AuroraMCLobbyPlayer player;
    private final List<Crate> availableCrates;

    public EmeraldCrateMenu(AuroraMCLobbyPlayer player) {
        super("&a&lEmerald Crates", 4, true);
        border("&a&lEmerald Crates", null);

        this.player = player;

         availableCrates = player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof EmeraldCrate).collect(Collectors.toList());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/f3d5e43de5d4177c4baf2f44161554473a3b0be5430998b5fcd826af943afe3\"}}}".getBytes())));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        ItemMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().convert("&a&lEmerald Crate")));
        meta.setLore(Arrays.asList(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight(";&7Emerald Crates are legendary crates that not;&7only give you a very high chance to find;&7legendary loot, but also contains no duplicates!")).split(";")));
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
            this.setItem(2, 4, new GUIItem(Material.BARRIER, "&c&lYou do not have any available Emerald Crates.", 1, ";&7Claim a Plus Bonus from Cosmonaut Luna;&7in order to get an Emerald Crate!"));
        } else {
            int row = 1;
            int column = 1;
            for (Crate crate : availableCrates) {
                this.setItem(row, column, new GUIItem(Material.ENDER_CHEST, "&a&lEmerald Crate", 1, ";&7UUID: " + crate.getUuid() + ";;&aClick to open!"));
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
        if (item.getType() != Material.ENDER_CHEST) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
        } else {
            Crate crate = availableCrates.get(((row - 1) * 7) + (column - 1));
            player.getPlayer().closeInventory();

            if (crate.open(player) == null) {
                player.getPlayer().closeInventory();
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "You already have all of the rewards for this crate."));
                return;
            }

            if (LobbyAPI.startOpen(crate, player)) {
                Location location = LobbyAPI.getChestBlock().getLocation();
                for (AuroraMCPlayer player1 :  AuroraMCAPI.getPlayers()) {
                    if (player1.getHolograms().containsKey("crates")) {
                        player1.getHolograms().get("crates").despawn();
                    }
                }
                location.getBlock().setType(Material.AIR);
                player.getPlayer().teleport(location.add(0.5, 0, 0.5));
                Location loc = new Location(location.getWorld(), location.getX() - 3, location.getY() - 1, location.getZ() - 3);

                new BukkitRunnable(){
                    int i = 0;
                    byte w = 7;
                    @Override
                    public void run() {
                        if (!player.getPlayer().isOnline()) {
                            this.cancel();
                            return;
                        }
                        if (i < 7) {
                            CrateStructures.getEmeraldCrate().getLevel(i).place(loc);
                            loc.setY(loc.getY() + 1);
                            location.getWorld().playSound(location, Sound.DIG_STONE, 1, 0);
                        }
                        if (i > 0) {
                            Location loc2 = location.clone();
                            loc2.setX(loc2.getX() + 3);
                            loc2.getBlock().setType(Material.STATIONARY_WATER);
                            loc2.getBlock().setData(w);

                            loc2.setX(loc2.getX() - 6);
                            loc2.getBlock().setType(Material.STATIONARY_WATER);
                            loc2.getBlock().setData(w);

                            loc2.setZ(loc2.getZ() - 3);
                            loc2.setX(loc2.getX() + 3);
                            loc2.getBlock().setType(Material.STATIONARY_WATER);
                            loc2.getBlock().setData(w);

                            loc2.setZ(loc2.getZ() + 6);
                            loc2.getBlock().setType(Material.STATIONARY_WATER);
                            loc2.getBlock().setData(w);
                            w--;
                        }
                        i++;
                        if (i >= 9) {
                            Location loc2 = location.clone();
                            loc2.setX(loc2.getX() + 3);
                            (new StructureChest(Material.CHEST, BlockFace.WEST)).place(loc2);

                            loc2.setX(loc2.getX() - 6);
                            (new StructureChest(Material.CHEST, BlockFace.EAST)).place(loc2);


                            loc2.setZ(loc2.getZ() - 3);
                            loc2.setX(loc2.getX() + 3);
                            (new StructureChest(Material.CHEST, BlockFace.SOUTH)).place(loc2);

                            loc2.setZ(loc2.getZ() + 6);
                            (new StructureChest(Material.CHEST, BlockFace.NORTH)).place(loc2);
                            LobbyAPI.crateAnimationFinished();
                            this.cancel();
                        }
                    }
                }.runTaskTimer(AuroraMCAPI.getCore(), 0, 8);
            } else {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "Someone is already opening a crate! Please wait until they are finished to open one!"));
            }
        }
    }
}
