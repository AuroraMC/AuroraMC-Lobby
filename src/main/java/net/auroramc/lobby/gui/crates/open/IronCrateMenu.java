/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates.open;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.cosmetics.Crate;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.common.cosmetics.crates.IronCrate;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.CrateStructures;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Chest;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class IronCrateMenu extends GUI {


    private final AuroraMCLobbyPlayer player;
    private final List<Crate> availableCrates;

    public IronCrateMenu(AuroraMCLobbyPlayer player) {
        super("&7&lIron Crates", 4, true);
        border("&7&lIron Crates", null);

        this.player = player;

        availableCrates = player.getCrates().stream().filter(crate -> crate.getLoot() == null && crate instanceof IronCrate).collect(Collectors.toList());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", Base64.getEncoder().encodeToString("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/f7aadff9ddc546fdcec6ed5919cc39dfa8d0c07ff4bc613a19f2e6d7f2593\"}}}".getBytes())));
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1);
        head.setDurability((short)3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setDisplayName(TextFormatter.convert("&7&lIron Crate"));
        meta.setLore(Arrays.asList(TextFormatter.convert(TextFormatter.highlightRaw(";&7Iron Crates are the most common;&7and have the lowest chances to;&7win awesome stuff.")).split(";")));
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


        if (availableCrates.size() == 0) {
            this.setItem(2, 4, new GUIItem(Material.BARRIER, "&c&lYou do not have any available Iron Crates.", 1, ";&7Purchase Iron Crates using the main menu;&7or at store.auroramc.net!"));
        } else {
            int row = 1;
            int column = 1;
            for (Crate crate : availableCrates) {
                this.setItem(row, column, new GUIItem(Material.CHEST, "&7&lIron Crate", 1, ";&7UUID: " + crate.getUuid() + ";;&aClick to open!"));
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
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
        } else {
            Crate crate = availableCrates.get(((row - 1) * 7) + (column - 1));
            player.closeInventory();

            if (LobbyAPI.startOpen(crate, player)) {
                Location location = LobbyAPI.getChestBlock().getLocation();
                for (AuroraMCServerPlayer player1 :  ServerAPI.getPlayers()) {
                    if (player1.getHolograms().containsKey("crates")) {
                        player1.getHolograms().get("crates").despawn();
                    }
                }
                location.getBlock().setType(Material.AIR);
                player.teleport(location.add(0.5, 0, 0.5));
                Location loc = new Location(location.getWorld(), location.getX() - 3, location.getY() - 1, location.getZ() - 3);

                CrateStructures.getIronCrate().place(loc);

                Location anvil = new Location(location.getWorld(), location.getX(), location.getY() + 2, location.getZ() + 3);
                Location chest = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 3);

                FallingBlock block = anvil.getWorld().spawnFallingBlock(anvil, Material.ANVIL, (byte)0);
                block.setDropItem(false);
                block.setHurtEntities(false);
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        block.remove();
                        if (!player.isOnline()) {
                            this.cancel();
                            return;
                        }
                        chest.getBlock().setType(Material.CHEST);
                        player.playSound(chest, Sound.ANVIL_LAND, 1, 100);

                        anvil.setZ(anvil.getZ() - 6);
                        chest.setZ(chest.getZ() - 6);
                        FallingBlock block = anvil.getWorld().spawnFallingBlock(anvil, Material.ANVIL, (byte)0);
                        block.setDropItem(false);
                        block.setHurtEntities(false);
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                block.remove();
                                if (!player.isOnline()) {
                                    this.cancel();
                                    return;
                                }
                                chest.getBlock().setType(Material.CHEST);
                                player.playSound(chest, Sound.ANVIL_LAND, 1, 100);
                                BlockState c = chest.getBlock().getState();
                                c.setData(new Chest(BlockFace.SOUTH));
                                c.update();

                                anvil.setX(anvil.getX() + 3);
                                anvil.setZ(anvil.getZ() + 3);
                                chest.setX(chest.getX() + 3);
                                chest.setZ(chest.getZ() + 3);
                                FallingBlock block = anvil.getWorld().spawnFallingBlock(anvil, Material.ANVIL, (byte)0);
                                block.setDropItem(false);
                                block.setHurtEntities(false);
                                new BukkitRunnable(){
                                    @Override
                                    public void run() {
                                        block.remove();
                                        if (!player.isOnline()) {
                                            this.cancel();
                                            return;
                                        }
                                        chest.getBlock().setType(Material.CHEST);
                                        player.playSound(chest, Sound.ANVIL_LAND, 1, 100);
                                        BlockState c = chest.getBlock().getState();
                                        c.setData(new Chest(BlockFace.WEST));
                                        c.update();

                                        anvil.setX(anvil.getX() - 6);
                                        chest.setX(chest.getX() - 6);
                                        FallingBlock block = anvil.getWorld().spawnFallingBlock(anvil, Material.ANVIL, (byte)0);
                                        block.setDropItem(false);
                                        block.setHurtEntities(false);
                                        new BukkitRunnable(){
                                            @Override
                                            public void run() {
                                                block.remove();
                                                if (!player.isOnline()) {
                                                    this.cancel();
                                                    return;
                                                }
                                                chest.getBlock().setType(Material.CHEST);
                                                player.playSound(chest, Sound.ANVIL_LAND, 1, 100);
                                                BlockState c = chest.getBlock().getState();
                                                c.setData(new Chest(BlockFace.EAST));
                                                c.update();
                                                LobbyAPI.crateAnimationFinished();
                                            }
                                        }.runTaskLater(ServerAPI.getCore(), 11);
                                    }
                                }.runTaskLater(ServerAPI.getCore(), 11);
                            }
                        }.runTaskLater(ServerAPI.getCore(), 11);
                    }
                }.runTaskLater(ServerAPI.getCore(), 11);
            } else {
                player.sendMessage(TextFormatter.pluginMessage("Crates", "Someone is already opening a crate! Please wait until they are finished to open one!"));
            }
        }
    }
}
