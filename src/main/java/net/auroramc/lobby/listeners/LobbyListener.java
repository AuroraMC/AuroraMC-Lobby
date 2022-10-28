/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Cosmetic;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.cosmetics.Gadget;
import net.auroramc.core.api.events.player.PlayerPreferenceChangeEvent;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.gui.cosmetics.Cosmetics;
import net.auroramc.core.gui.preferences.Preferences;
import net.auroramc.core.gui.stats.stats.Stats;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.parkour.Parkour;
import net.auroramc.lobby.api.parkour.ParkourRun;
import net.auroramc.lobby.api.parkour.plates.Checkpoint;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.CrateStructures;
import net.auroramc.lobby.gui.GameMenu;
import net.auroramc.lobby.gui.LobbySwitcher;
import net.auroramc.lobby.gui.crates.ViewCrates;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockAction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Chest;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LobbyListener implements Listener {

    private final List<Player> cancelNextEvent = new ArrayList<>();

    private static int highX = 0, lowX = 0, highY = 0, lowY = 0, highZ = 0, lowZ = 0;
    final double STILL = -0.0784000015258789;
    private static final List<Player> hasTeleported = new ArrayList<>();

    static {
        JSONObject a = LobbyAPI.getLobbyMap().getMapData().getJSONObject("border_a");
        JSONObject b = LobbyAPI.getLobbyMap().getMapData().getJSONObject("border_b");
        if (a.getInt("x") > b.getInt("x")) {
            highX = a.getInt("x");
            lowX = b.getInt("x");
        } else {
            highX = b.getInt("x");
            lowX = a.getInt("x");
        }

        if (a.getInt("y") > b.getInt("y")) {
            highY = a.getInt("y");
            lowY = b.getInt("y");
        } else {
            highY = b.getInt("y");
            lowY = a.getInt("y");
        }

        if (a.getInt("z") > b.getInt("z")) {
            highZ = a.getInt("z");
            lowZ = b.getInt("z");
        } else {
            highZ = b.getInt("z");
            lowZ = a.getInt("z");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                if (AuroraMCAPI.getPlayer(e.getPlayer()).getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(5))) {
                    AuroraMCAPI.getPlayer(e.getPlayer()).getStats().achievementGained(AuroraMCAPI.getAchievement(5), 1, true);
                }
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
            if (e.getEntity() instanceof Player) {
                AuroraMCLobbyPlayer target = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer((Player) e.getEntity());
                if (target.isInParkour()) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                                e.setCancelled(true);
                    } else if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        if (hasTeleported.contains(target.getPlayer())) {
                            return;
                        }
                    }

                    e.setCancelled(true);
                    target.getPlayer().setFallDistance(0);

                    Location l = target.getActiveParkourRun().getParkour().getRestartPoint().getLocation().clone();
                    if (target.getActiveParkourRun().getLastReached() != 0) {
                        l = target.getActiveParkourRun().getParkour().getCheckpoint(target.getActiveParkourRun().getLastReached()).getLocation().clone();
                    }
                    l.setX(l.getX() + 0.5);
                    l.setY(l.getY() + 0.5);
                    l.setZ(l.getZ() + 0.5);
                    target.getPlayer().setVelocity(new Vector(0, 0, 0));
                    target.getPlayer().teleport(l);
                    target.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have been teleported to your last checkpoint."));
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        hasTeleported.add(target.getPlayer());
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                hasTeleported.remove(target.getPlayer());
                            }
                        }.runTaskLater(LobbyAPI.getLobby(), 5);
                    }
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    JSONArray spawnLocations = LobbyAPI.getLobbyMap().getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
                    int x, y, z;
                    x = spawnLocations.getJSONObject(0).getInt("x");
                    y = spawnLocations.getJSONObject(0).getInt("y");
                    z = spawnLocations.getJSONObject(0).getInt("z");
                    float yaw = spawnLocations.getJSONObject(0).getFloat("yaw");
                    e.getEntity().teleport(new Location(Bukkit.getWorld("world"), x, y, z, yaw, 0));
                    e.getEntity().setFallDistance(0);
                    e.getEntity().setVelocity(new Vector());
                } else if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                    e.getEntity().setFireTicks(0);
                }
                if (e instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                    if (event.getDamager() instanceof Player) {
                        AuroraMCPlayer damager = AuroraMCAPI.getPlayer((Player) ((EntityDamageByEntityEvent) e).getDamager());
                        if (damager.hasPermission("elite") && target.hasPermission("moderation") && !target.isDisguised()) {
                            if (target.canBePunched() || damager.hasPermission("admin")) {
                                target.getPlayer().setVelocity(new Vector(0, 10, 0));
                                target.getPlayer().getLocation().getWorld().createExplosion(target.getPlayer().getLocation().getBlockX(), target.getPlayer().getLocation().getBlockY(), target.getPlayer().getLocation().getBlockZ(), 2, false, false);
                                target.punched();
                            } else {
                                damager.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Lobby", "**" + target.getName() + "** has been punched too recently. You cannot punch them again yet."));
                            }
                        }
                    }
                }
                e.setCancelled(true);

            } else if (e.getEntity() instanceof ArmorStand || e.getEntity() instanceof Painting || e.getEntity() instanceof ItemFrame) {
                e.setCancelled(true);
            }

        if (e.getEntity() instanceof Rabbit && !((Rabbit)e.getEntity()).isAdult()) {
            if (e.getEntity().isInsideVehicle()) {
                if (e.getEntity().getVehicle() instanceof Damageable) {
                    e.setCancelled(true);
                    Damageable damageable = (Damageable) e.getEntity().getVehicle();
                    if (e instanceof EntityDamageByEntityEvent) {
                        damageable.damage(e.getDamage(), ((EntityDamageByEntityEvent) e).getDamager());
                    } else {
                        damageable.damage(e.getDamage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().getPassenger() != null) {
            if (e.getEntity().getPassenger() instanceof Rabbit && !((Rabbit)e.getEntity().getPassenger()).isAdult()) {
                if (e.getEntity().getPassenger().getPassenger() != null) {
                    e.getEntity().getPassenger().getPassenger().remove();
                }
                e.getEntity().getPassenger().remove();
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
            if (e.getEntity() instanceof Player && e.getFoodLevel() < 25) {
                e.setCancelled(true);
                e.setFoodLevel(30);
            }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
            if (e.toWeatherState()) {
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPaintingBreak(HangingBreakEvent e) {
        e.setCancelled(true);
    }



    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
        {
            AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
            if (player.isInParkour()) {
                if (e.getItem() != null && e.getItem().getType() != Material.AIR) {
                    /*if (cancelNextEvent.contains(e.getPlayer())) {
                        cancelNextEvent.remove(e.getPlayer());
                        return;
                    }
                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        cancelNextEvent.add(e.getPlayer());
                    }*/
                    switch (e.getItem().getType()) {
                        case BED: {
                            player.getActiveParkourRun().end(ParkourRun.FailCause.LEAVE);
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have left the parkour."));
                            return;
                        }
                        case GOLD_PLATE: {
                            player.getPlayer().setFallDistance(0);
                            Location l = player.getActiveParkourRun().getParkour().getRestartPoint().getLocation().clone();
                            player.getPlayer().setVelocity(new Vector(0, 0, 0));
                            player.getPlayer().teleport(l);
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have been teleported to your last checkpoint!"));
                            hasTeleported.add(player.getPlayer());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    hasTeleported.remove(player.getPlayer());
                                }
                            }.runTaskLater(LobbyAPI.getLobby(), 5);
                            return;
                        }
                        case WOOD_DOOR: {
                            player.getPlayer().setFallDistance(0);
                            Location l = player.getActiveParkourRun().getParkour().getRestartPoint().getLocation().clone();
                            player.getPlayer().setVelocity(new Vector(0, 0, 0));
                            player.getPlayer().teleport(l);
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have been teleported to the restart point!"));
                            hasTeleported.add(player.getPlayer());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    hasTeleported.remove(player.getPlayer());
                                }
                            }.runTaskLater(LobbyAPI.getLobby(), 5);
                            return;
                        }
                    }
                }
                return;
            }
        }

        if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CHEST && e.getClickedBlock().getLocation().equals(LobbyAPI.getChestBlock().getLocation()) && !((AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(e.getPlayer())).isInParkour()) {
            AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
            ViewCrates crates = new ViewCrates(player);
            crates.open(player);
            AuroraMCAPI.openGUI(player, crates);
            e.setCancelled(true);
            return;
        } else if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.CHEST && LobbyAPI.getCratePlayer() != null && LobbyAPI.getCratePlayer().getPlayer().equals(e.getPlayer()) && LobbyAPI.isCrateAnimationFinished()) {
            CraftBlock block = (CraftBlock) e.getClickedBlock();
            PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(new BlockPosition(block.getX(), block.getY(), block.getZ()), Blocks.CHEST, 1, 1);
            for (Player player : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
            AuroraMCLobbyPlayer cratePlayer = LobbyAPI.getCratePlayer();
            Crate.CrateReward reward = LobbyAPI.getCurrentCrate().open(LobbyAPI.getCratePlayer());
            long amount = cratePlayer.getCrates().stream().filter(crate -> crate.getOpened() > 0).count();
            if (amount > 0) {
                cratePlayer.getHolograms().get("crates").getLines().get(2).setText("&fYou have &b" + amount + " &fcrates to open!");
            } else {
                cratePlayer.getHolograms().get("crates").removeLine(2);
            }
            if (reward.getCosmetic() != null) {
                Location loc2 = block.getLocation().clone();
                loc2.add(0.5, 1.5, 0.5);
                Item item = block.getLocation().getWorld().dropItem(loc2, new ItemStack(reward.getCosmetic().getMaterial(), 1, reward.getCosmetic().getData()));
                item.setPickupDelay(1000000);
                item.setVelocity(new Vector());
                Location location = block.getLocation().clone();
                location.add(0.5, 1.5, 0.5);
                ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
                stand.setVisible(false);
                stand.setCustomName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight(reward.getCosmetic().getDisplayName() + "&r (" + reward.getCosmetic().getRarity().getDisplayName() + "&r)")));
                stand.setCustomNameVisible(true);
                stand.setSmall(true);
                stand.setMarker(true);
                stand.setGravity(false);
                cratePlayer.getUnlockedCosmetics().add(reward.getCosmetic());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AuroraMCAPI.getDbManager().addCosmetic(cratePlayer.getPlayer().getUniqueId(), reward.getCosmetic());
                    }
                }.runTaskAsynchronously(AuroraMCAPI.getCore());
                switch (reward.getCosmetic().getRarity()) {
                    case COMMON: {
                        cratePlayer.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "You just found a " + reward.getCosmetic().getRarity().getDisplayName() + " **" + reward.getCosmetic().getDisplayName() + "** (**" + reward.getCosmetic().getType().getName() + "**)"));
                        break;
                    }
                    case UNCOMMON: {
                        cratePlayer.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "You just found a " + reward.getCosmetic().getRarity().getDisplayName() + " **" + reward.getCosmetic().getDisplayName() + "** (**" + reward.getCosmetic().getType().getName() + "**)"));
                        Location loc = block.getLocation().add(0.5, 0, 0.5);
                        org.bukkit.entity.Firework firework = loc.getWorld().spawn(loc, org.bukkit.entity.Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.setPower(0);
                        meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(85, 255, 85)).trail(true).flicker(true).with(FireworkEffect.Type.BURST).build());
                        firework.setFireworkMeta(meta);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                firework.detonate();
                            }
                        }.runTaskLater(AuroraMCAPI.getCore(), 2);
                        break;
                    }
                    case RARE: {
                        cratePlayer.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "You just found a " + reward.getCosmetic().getRarity().getDisplayName() + " **" + reward.getCosmetic().getDisplayName() + "** (**" + reward.getCosmetic().getType().getName() + "**)"));
                        Location loc = block.getLocation().add(0.5, 0, 0.5);
                        org.bukkit.entity.Firework firework = loc.getWorld().spawn(loc, org.bukkit.entity.Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.setPower(0);
                        meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(85, 85, 255)).trail(true).flicker(true).with(FireworkEffect.Type.BURST).build());
                        firework.setFireworkMeta(meta);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                firework.detonate();
                            }
                        }.runTaskLater(AuroraMCAPI.getCore(), 2);
                        break;
                    }
                    case EPIC: {
                        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "**" + cratePlayer.getPlayer().getName() + "** just found a " + reward.getCosmetic().getRarity().getDisplayName() + " **" + reward.getCosmetic().getDisplayName() + "** (**" + reward.getCosmetic().getType().getName() + "**)"));
                            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.PORTAL_TRAVEL, 100, 0);
                            new BukkitRunnable() {
                                int i = 0;

                                @Override
                                public void run() {
                                    Location location = block.getLocation().add(0.5, 0, 0.5);
                                    ;
                                    org.bukkit.entity.Firework firework = location.getWorld().spawn(location, org.bukkit.entity.Firework.class);
                                    FireworkMeta meta = firework.getFireworkMeta();
                                    meta.setPower(0);
                                    meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(255, 85, 255)).trail(true).flicker(true).with(FireworkEffect.Type.BURST).build());
                                    firework.setFireworkMeta(meta);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            firework.detonate();
                                        }
                                    }.runTaskLater(AuroraMCAPI.getCore(), 2);
                                    i++;
                                    if (i > 5) {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(AuroraMCAPI.getCore(), 0, 2);
                        }
                        break;
                    }
                    case LEGENDARY: {
                        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "**" + cratePlayer.getPlayer().getName() + "** just found a " + reward.getCosmetic().getRarity().getDisplayName() + " **" + reward.getCosmetic().getDisplayName() + "** (**" + reward.getCosmetic().getType().getName() + "**)"));
                            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 100, 0);
                        }
                        new BukkitRunnable() {
                            int i = 0;

                            @Override
                            public void run() {
                                Location loc = block.getLocation().add(0.5, 0, 0.5);
                                org.bukkit.entity.Firework firework = loc.getWorld().spawn(loc, org.bukkit.entity.Firework.class);
                                FireworkMeta meta = firework.getFireworkMeta();
                                meta.setPower(0);
                                meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(255, 170, 0)).trail(true).flicker(true).with(FireworkEffect.Type.BURST).build());
                                firework.setFireworkMeta(meta);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        firework.detonate();
                                    }
                                }.runTaskLater(AuroraMCAPI.getCore(), 2);
                                i++;
                                if (i > 10) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(AuroraMCAPI.getCore(), 0, 5);
                        break;
                    }
                    case MYTHICAL: {
                        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "**" + cratePlayer.getPlayer().getName() + "** just found a " + reward.getCosmetic().getRarity().getDisplayName() + " **" + reward.getCosmetic().getDisplayName() + "** (**" + reward.getCosmetic().getType().getName() + "**)"));
                            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENDERDRAGON_DEATH, 100, 0);
                        }
                        new BukkitRunnable() {
                            int i = 0;

                            @Override
                            public void run() {
                                Location location = block.getLocation().add(0.5, 0, 0.5);
                                org.bukkit.entity.Firework firework = location.getWorld().spawn(location, org.bukkit.entity.Firework.class);
                                FireworkMeta meta = firework.getFireworkMeta();
                                meta.setPower(0);
                                meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(255, 85, 85)).trail(true).flicker(true).with(FireworkEffect.Type.BURST).build());
                                firework.setFireworkMeta(meta);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        firework.detonate();
                                    }
                                }.runTaskLater(AuroraMCAPI.getCore(), 2);
                                i++;
                                if (i > 20) {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(AuroraMCAPI.getCore(), 0, 5);
                        break;
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        stand.remove();
                        item.remove();
                        JSONObject crateLocation = LobbyAPI.getLobbyMap().getMapData().getJSONObject("game").getJSONArray("CRATE").getJSONObject(0);
                        int x = crateLocation.getInt("x");
                        int y = crateLocation.getInt("y");
                        int z = crateLocation.getInt("z");
                        Location location = new Location(Bukkit.getWorld("world"), x, y, z);
                        Location loc = new Location(location.getWorld(), location.getX() - 3, location.getY() - 1, location.getZ() - 3);
                        CrateStructures.getBaseCrate().place(loc);
                        Block block = location.getBlock();
                        block.setType(Material.CHEST);
                        BlockState state = block.getState();
                        BlockFace direction;
                        float yaw = crateLocation.getFloat("yaw");
                        if (yaw <= -135 || yaw >= 135) {
                            direction = BlockFace.NORTH;
                        } else if (yaw > -135 && yaw < -45) {
                            direction = BlockFace.EAST;
                        } else if (yaw >= -45 && yaw <= 45) {
                            direction = BlockFace.SOUTH;
                        } else {
                            direction = BlockFace.WEST;
                        }
                        org.bukkit.material.Chest chest = new Chest(direction);
                        state.setData(chest);
                        state.update();
                        LobbyAPI.setChestBlock(block);
                        location.setY(location.getY() + 1);
                        location.setX(location.getX() + 0.5);
                        location.setZ(location.getZ() + 0.5);
                        for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                            if (player1.getHolograms().containsKey("crates")) {
                                player1.getHolograms().get("crates").spawn();
                            }
                        }
                    }
                }.runTaskLater(AuroraMCAPI.getCore(), 60);
            } else if (reward.getRank() != null) {
                Item item = block.getLocation().getWorld().dropItem(block.getLocation().clone().add(0.5, 1.5, 0.5), new ItemStack(Material.DIAMOND));
                item.setPickupDelay(1000000);
                item.setVelocity(new Vector());
                Location location = block.getLocation().clone();
                location.setY(location.getY() + 0.5);
                location.setX(location.getX() + 0.5);
                location.setZ(location.getZ() + 0.5);
                ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
                stand.setVisible(false);
                stand.setCustomName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("&" + reward.getRank().getPrefixColor() + reward.getRank().getName() + " Rank&r (" + Cosmetic.Rarity.MYTHICAL.getDisplayName() + "&r)")));
                stand.setCustomNameVisible(true);
                stand.setSmall(true);
                stand.setMarker(true);
                stand.setGravity(false);
                for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "**" + cratePlayer.getPlayer().getName() + "** just found &" + reward.getRank().getPrefixColor() + reward.getRank().getName() + " Rank** (**" + Cosmetic.Rarity.MYTHICAL.getDisplayName() + "**)"));
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENDERDRAGON_DEATH, 100, 0);
                }
                cratePlayer.setRank(reward.getRank());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AuroraMCAPI.getDbManager().setRank(cratePlayer, reward.getRank());
                    }
                }.runTaskAsynchronously(AuroraMCAPI.getCore());
                new BukkitRunnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        Location location = block.getLocation().add(0.5, 0, 0.5);
                        org.bukkit.entity.Firework firework = location.getWorld().spawn(location, org.bukkit.entity.Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.setPower(0);
                        meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(255, 85, 85)).trail(true).flicker(true).with(FireworkEffect.Type.BURST).build());
                        firework.setFireworkMeta(meta);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                firework.detonate();
                            }
                        }.runTaskLater(AuroraMCAPI.getCore(), 2);
                        i++;
                        if (i > 20) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(AuroraMCAPI.getCore(), 0, 5);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        stand.remove();
                        item.remove();
                        JSONObject crateLocation = LobbyAPI.getLobbyMap().getMapData().getJSONObject("game").getJSONArray("CRATE").getJSONObject(0);
                        int x = crateLocation.getInt("x");
                        int y = crateLocation.getInt("y");
                        int z = crateLocation.getInt("z");
                        Location location = new Location(Bukkit.getWorld("world"), x, y, z);
                        Location loc = new Location(location.getWorld(), location.getX() - 3, location.getY() - 1, location.getZ() - 3);
                        CrateStructures.getBaseCrate().place(loc);
                        Block block = location.getBlock();
                        block.setType(Material.CHEST);
                        BlockState state = block.getState();
                        BlockFace direction;
                        float yaw = crateLocation.getFloat("yaw");
                        if (yaw <= -135 || yaw >= 135) {
                            direction = BlockFace.NORTH;
                        } else if (yaw > -135 && yaw < -45) {
                            direction = BlockFace.EAST;
                        } else if (yaw >= -45 && yaw <= 45) {
                            direction = BlockFace.SOUTH;
                        } else {
                            direction = BlockFace.WEST;
                        }
                        org.bukkit.material.Chest chest = new Chest(direction);
                        state.setData(chest);
                        state.update();
                        LobbyAPI.setChestBlock(block);
                        location.setY(location.getY() + 1);
                        location.setX(location.getX() + 0.5);
                        location.setZ(location.getZ() + 0.5);
                        for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                            if (player1.getHolograms().containsKey("crates")) {
                                player1.getHolograms().get("crates").spawn();
                            }
                        }

                    }
                }.runTaskLater(AuroraMCAPI.getCore(), 200);

            } else {
                Item item = block.getLocation().getWorld().dropItem(block.getLocation().clone().add(0.5, 1.5, 0.5), new ItemStack(Material.NETHER_STAR));
                item.setPickupDelay(1000000);
                item.setVelocity(new Vector());
                Location location = block.getLocation().clone();
                location.setY(location.getY() + 0.5);
                location.setX(location.getX() + 0.5);
                location.setZ(location.getZ() + 0.5);
                ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
                stand.setVisible(false);
                stand.setCustomName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("&b" + reward.getPlusDays() + " Plus Days&r (" + Cosmetic.Rarity.MYTHICAL.getDisplayName() + "&r)")));
                stand.setCustomNameVisible(true);
                stand.setSmall(true);
                stand.setMarker(true);
                stand.setGravity(false);
                for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "**" + cratePlayer.getPlayer().getName() + "** just found &b" + reward.getPlusDays() + " Plus Days** (**" + Cosmetic.Rarity.MYTHICAL.getDisplayName() + "**)"));
                    player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENDERDRAGON_DEATH, 100, 0);
                }
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("AddPlusDays");
                out.writeUTF(cratePlayer.getName());
                out.writeInt(reward.getPlusDays());
                cratePlayer.getPlayer().sendPluginMessage(AuroraMCAPI.getCore(), "BungeeCord", out.toByteArray());
                new BukkitRunnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        Location location = block.getLocation().add(0.5, 0, 0.5);
                        org.bukkit.entity.Firework firework = location.getWorld().spawn(location, org.bukkit.entity.Firework.class);
                        FireworkMeta meta = firework.getFireworkMeta();
                        meta.setPower(0);
                        meta.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(255, 85, 85)).trail(true).flicker(true).with(FireworkEffect.Type.BURST).build());
                        firework.setFireworkMeta(meta);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                firework.detonate();
                            }
                        }.runTaskLater(AuroraMCAPI.getCore(), 2);
                        i++;
                        if (i > 20) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(AuroraMCAPI.getCore(), 0, 5);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        stand.remove();
                        item.remove();
                        JSONObject crateLocation = LobbyAPI.getLobbyMap().getMapData().getJSONObject("game").getJSONArray("CRATE").getJSONObject(0);
                        int x = crateLocation.getInt("x");
                        int y = crateLocation.getInt("y");
                        int z = crateLocation.getInt("z");
                        Location location = new Location(Bukkit.getWorld("world"), x, y, z);
                        Location loc = new Location(location.getWorld(), location.getX() - 3, location.getY() - 1, location.getZ() - 3);
                        CrateStructures.getBaseCrate().place(loc);
                        Block block = location.getBlock();
                        block.setType(Material.CHEST);
                        BlockState state = block.getState();
                        BlockFace direction;
                        float yaw = crateLocation.getFloat("yaw");
                        if (yaw <= -135 || yaw >= 135) {
                            direction = BlockFace.NORTH;
                        } else if (yaw > -135 && yaw < -45) {
                            direction = BlockFace.EAST;
                        } else if (yaw >= -45 && yaw <= 45) {
                            direction = BlockFace.SOUTH;
                        } else {
                            direction = BlockFace.WEST;
                        }
                        org.bukkit.material.Chest chest = new Chest(direction);
                        state.setData(chest);
                        state.update();
                        LobbyAPI.setChestBlock(block);
                        location.setY(location.getY() + 1);
                        location.setX(location.getX() + 0.5);
                        location.setZ(location.getZ() + 0.5);
                        for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                            if (player1.getHolograms().containsKey("crates")) {
                                player1.getHolograms().get("crates").spawn();
                            }
                        }
                        LobbyAPI.finishOpen();

                    }
                }.runTaskLater(AuroraMCAPI.getCore(), 200);
            }
            LobbyAPI.getCurrentCrate().opened(reward);
            LobbyAPI.finishOpen();
            e.setCancelled(true);
            return;
        }
        if (e.getItem() != null && e.getItem().getType() != Material.AIR) {
            if (e.getPlayer().getInventory().getHeldItemSlot() == 3 && !((AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(e.getPlayer())).isInParkour()) {
                AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
                if (player.getActiveCosmetics().containsKey(Cosmetic.CosmeticType.GADGET)) {
                    Gadget gadget = (Gadget) player.getActiveCosmetics().get(Cosmetic.CosmeticType.GADGET);
                    if (e.getItem().getType() == Material.FISHING_ROD && e.getClickedBlock() != null) {
                        return;
                    }
                    if (System.currentTimeMillis() - player.getLastUsed().getOrDefault(gadget, 0L) < gadget.getCooldown() * 1000L) {
                        double amount = ((player.getLastUsed().getOrDefault(gadget, 0L) + (gadget.getCooldown() * 1000L)) - System.currentTimeMillis()) / 100d;
                        long amount1 = Math.round(amount);
                        if (amount1 < 0) {
                            amount1 = 0;
                        }
                        player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Gadgets", "You cannot use this gadget for **" + (amount1 / 10f) + " seconds**."));
                        e.setUseItemInHand(Event.Result.DENY);
                        e.setUseInteractedBlock(Event.Result.DENY);
                        return;
                    }
                    if (gadget.getId() == 801) {
                        e.setCancelled(false);
                    }
                    if (e.getClickedBlock() != null) {
                        gadget.onUse(player, e.getClickedBlock().getLocation());
                    } else {
                        gadget.onUse(player, player.getPlayer().getLocation());
                    }
                    player.getLastUsed().put(gadget, System.currentTimeMillis());
                }
                return;
            }
            switch (e.getItem().getType()) {
                case EMERALD: {
                    e.setCancelled(true);
                    AuroraMCPlayer player = AuroraMCAPI.getPlayer(e.getPlayer());
                    Cosmetics cosmetics = new Cosmetics(player);
                    cosmetics.open(player);
                    AuroraMCAPI.openGUI(player, cosmetics);
                    break;
                }
                case REDSTONE_COMPARATOR: {
                    e.setCancelled(true);
                    AuroraMCPlayer player = AuroraMCAPI.getPlayer(e.getPlayer());
                    Preferences prefs = new Preferences(player);
                    prefs.open(player);
                    AuroraMCAPI.openGUI(player, prefs);
                    break;
                }
                case SKULL_ITEM: {
                    e.setCancelled(true);
                    AuroraMCPlayer player = AuroraMCAPI.getPlayer(e.getPlayer());
                    Stats stats = new Stats(player, player.getName(), player.getStats(), player.getActiveSubscription(), player.getId());
                    stats.open(player);
                    AuroraMCAPI.openGUI(player, stats);
                    break;
                }
                case COMPASS: {
                    e.setCancelled(true);
                    AuroraMCPlayer player = AuroraMCAPI.getPlayer(e.getPlayer());
                    GameMenu menu = new GameMenu(player);
                    menu.open(player);
                    AuroraMCAPI.openGUI(player, menu);
                    break;
                }
                case NETHER_STAR: {
                    e.setCancelled(true);
                    AuroraMCPlayer player = AuroraMCAPI.getPlayer(e.getPlayer());
                    LobbySwitcher menu = new LobbySwitcher(player);
                    menu.open(player);
                    AuroraMCAPI.openGUI(player, menu);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
            e.setCancelled(true);
    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent e) {
        if (e.getBlock().isLiquid()) {
            e.setCancelled(true);
        }
    }



    @EventHandler
    public void onInvMove(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (e.isFlying() && player.isInParkour()) {
            player.getActiveParkourRun().end(ParkourRun.FailCause.FLY);
            player.parkourEnd();
        } else {
            if (e.isFlying() && (!player.getPreferences().isHubFlightEnabled() || (!player.hasPermission("elite") && !player.hasPermission("plus")))) {
                e.getPlayer().setAllowFlight(false);
                e.setCancelled(true);
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().normalize().multiply(2.2));
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENDERDRAGON_WINGS, 1, 100);
            }
        }
    }

    @EventHandler
    public void onPreferenceChange(PlayerPreferenceChangeEvent e) {
        if (!e.getPlayer().getPreferences().isHubFlightEnabled()) {
            e.getPlayer().getPlayer().setFlying(false);
        }
        if (e.getPlayer().getPreferences().isHubVisibilityEnabled()) {
            for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                if (player.equals(e.getPlayer())) {
                    continue;
                }
                if (!player.isVanished() || player.getRank().getId() <= e.getPlayer().getRank().getId()) {
                    e.getPlayer().getPlayer().showPlayer(player.getPlayer());
                }
            }
        } else {
            for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                if (!player.equals(e.getPlayer())) {
                    e.getPlayer().getPlayer().hidePlayer(player.getPlayer());
                }
            }
        }

        if (e.getPlayer().getPreferences().isHubInvisibilityEnabled()) {
            for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                if (player.equals(e.getPlayer())) {
                    continue;
                }
                if (!player.hasPermission("moderation")) {
                    player.getPlayer().hidePlayer(e.getPlayer().getPlayer());
                }
            }
        } else {
            for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
                if (player.equals(e.getPlayer())) {
                    continue;
                }
                player.getPlayer().showPlayer(e.getPlayer().getPlayer());
            }
        }
        if (e.getPlayer().getPreferences().isHubSpeedEnabled()) {
            e.getPlayer().getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 1, true, false));
        } else {
            e.getPlayer().getPlayer().removePotionEffect(PotionEffectType.SPEED);
        }

        if (!e.getPlayer().getPreferences().isHubForcefieldEnabled() && ((AuroraMCLobbyPlayer)e.getPlayer()).getRunnable() != null) {
            ((AuroraMCLobbyPlayer) e.getPlayer()).activateForcefield();
        } else if (e.getPlayer().getPreferences().isHubForcefieldEnabled() && ((AuroraMCLobbyPlayer)e.getPlayer()).getRunnable() == null) {
            ((AuroraMCLobbyPlayer) e.getPlayer()).deactivateForcefield();
        }


    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        AuroraMCPlayer player = AuroraMCAPI.getPlayer(e.getPlayer());
        if (player instanceof AuroraMCLobbyPlayer) {
            AuroraMCLobbyPlayer p = (AuroraMCLobbyPlayer) player;
            if (p.isInParkour()) {
                if (e.getPlayer().isOnGround()) {
                    p.getActiveParkourRun().touchedGround();
                }
                if (e.getPlayer().getVelocity().getY() > STILL) {
                    if (p.getActiveParkourRun().hasTouchedGround() && !player.getPlayer().isOnGround()) {
                        p.getActiveParkourRun().leftGround();
                        p.getActiveParkourRun().jumped();
                    }
                }
                Location from = e.getFrom().clone();
                Location to = e.getTo().clone();
                from.setY(0);
                to.setY(0);
                double distance = Math.abs(from.distance(to));
                {
                    p.getActiveParkourRun().addTravel(distance);
                }
            }
        }
        if (e.getTo().getX() < lowX || e.getTo().getX() > highX || e.getTo().getY() < lowY || e.getTo().getY() > highY || e.getTo().getZ() < lowZ || e.getTo().getZ() > highZ) {
            //Call entity damage event so the games can handle them appropriately.
            if (player instanceof AuroraMCLobbyPlayer) {
                AuroraMCLobbyPlayer p = (AuroraMCLobbyPlayer) player;
                if (p.isInParkour()) {
                    Location l = p.getActiveParkourRun().getParkour().getRestartPoint().getLocation().clone();
                    if(p.getActiveParkourRun().getLastReached() != 0) {
                        l = p.getActiveParkourRun().getParkour().getCheckpoint(p.getActiveParkourRun().getLastReached()).getLocation().clone();
                    }
                    e.getPlayer().teleport(l.add(0.5,0.5, 0.5));
                }
            }
            JSONArray spawnLocations = LobbyAPI.getLobbyMap().getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
            if (spawnLocations == null || spawnLocations.length() == 0) {
                LobbyAPI.getLobby().getLogger().info("An invalid waiting lobby was supplied, assuming 0, 64, 0 spawn position.");
                e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0, 64, 0));
            } else {
                int x, y, z;
                x = spawnLocations.getJSONObject(0).getInt("x");
                y = spawnLocations.getJSONObject(0).getInt("y");
                z = spawnLocations.getJSONObject(0).getInt("z");
                float yaw = spawnLocations.getJSONObject(0).getFloat("yaw");
                e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), x, y, z, yaw, 0));
            }
            if (LobbyAPI.getLobbyMap().getMapData().getInt("time") > 12000) {
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
            }
        } else if (!e.getPlayer().getAllowFlight() && (new Location(e.getTo().getWorld(), e.getTo().getX(), e.getTo().getY() - 1, e.getTo().getZ())).getBlock().getType() != Material.AIR && (player instanceof AuroraMCLobbyPlayer && ((AuroraMCLobbyPlayer) player).isInParkour())) {
            e.getPlayer().setAllowFlight(true);
        } else if (LobbyAPI.getCratePlayer() != null && e.getPlayer().equals(LobbyAPI.getCratePlayer().getPlayer())) {
            JSONObject crateLocation = LobbyAPI.getLobbyMap().getMapData().getJSONObject("game").getJSONArray("CRATE").getJSONObject(0);
            int x = crateLocation.getInt("x");
            int y = crateLocation.getInt("y");
            int z = crateLocation.getInt("z");
            Location location = new Location(Bukkit.getWorld("world"), x + 0.5, y, z + 0.5);
            if (Math.abs(e.getTo().getX() - location.getX()) > 4 || Math.abs(e.getTo().getZ() - location.getZ()) > 4 || Math.abs(e.getTo().getY() - location.getY()) > 4) {
                e.getPlayer().teleport(location);
            }
        } else if (LobbyAPI.getCratePlayer() != null) {
            JSONObject crateLocation = LobbyAPI.getLobbyMap().getMapData().getJSONObject("game").getJSONArray("CRATE").getJSONObject(0);
            int x = crateLocation.getInt("x");
            int y = crateLocation.getInt("y");
            int z = crateLocation.getInt("z");
            Location location = new Location(Bukkit.getWorld("world"), x + 0.5, y, z + 0.5);
            if (Math.abs(e.getTo().getX() - location.getX()) <= 4 && Math.abs(e.getTo().getZ() - location.getZ()) <= 4 && Math.abs(e.getTo().getY() - location.getY()) <= 4) {
                Vector vector = e.getPlayer().getLocation().toVector().subtract(location.toVector()).setY(4);
                e.getPlayer().setVelocity(vector.normalize().multiply(1.5));
                e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 100, 1);
            }
        } else if (!e.getTo().getBlock().equals(e.getFrom().getBlock())) {
            if (player instanceof AuroraMCLobbyPlayer) {
                AuroraMCLobbyPlayer p = (AuroraMCLobbyPlayer) player;
                switch (e.getTo().getBlock().getType()) {
                    case WOOD_PLATE: {
                        if (e.getTo().getBlock().equals(LobbyAPI.getEasy().getStart().getLocation().getBlock())) {
                            //Wants to start easy parkour
                            if (p.isInParkour()) {
                                if (p.getActiveParkourRun().getParkour().equals(LobbyAPI.getEasy())) {
                                    p.getActiveParkourRun().restart();
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have restarted the parkour!"));
                                } else {
                                    p.getActiveParkourRun().end(ParkourRun.FailCause.NEW_PARKOUR);
                                    p.parkourStart(LobbyAPI.getEasy());
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have started the **Easy** parkour!"));
                                }
                            } else {
                                player.getPlayer().setFlying(false);
                                if ((!player.getPreferences().isHubFlightEnabled() || (!player.hasPermission("elite") && !player.hasPermission("plus")))) {
                                    player.getPlayer().setAllowFlight(false);
                                }
                                p.parkourStart(LobbyAPI.getEasy());
                                p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have started the **Easy** parkour!"));
                            }
                        } else if (e.getTo().getBlock().equals(LobbyAPI.getMedium().getStart().getLocation().getBlock())) {
                            if (p.isInParkour()) {
                                if (p.getActiveParkourRun().getParkour().equals(LobbyAPI.getMedium())) {
                                    p.getActiveParkourRun().restart();
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have restarted the parkour!"));
                                } else {
                                    p.getActiveParkourRun().end(ParkourRun.FailCause.NEW_PARKOUR);
                                    p.parkourStart(LobbyAPI.getMedium());
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have started the **Medium** parkour!"));
                                }
                            } else {
                                player.getPlayer().setFlying(false);
                                if ((!player.getPreferences().isHubFlightEnabled() || (!player.hasPermission("elite") && !player.hasPermission("plus")))) {
                                    player.getPlayer().setAllowFlight(false);
                                }
                                p.parkourStart(LobbyAPI.getMedium());
                                p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have started the **Medium** parkour!"));
                            }
                        } else if (e.getTo().getBlock().equals(LobbyAPI.getHard().getStart().getLocation().getBlock())) {
                            if (p.isInParkour()) {
                                if (p.getActiveParkourRun().getParkour().equals(LobbyAPI.getHard())) {
                                    p.getActiveParkourRun().restart();
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have restarted the parkour!"));
                                } else {
                                    p.getActiveParkourRun().end(ParkourRun.FailCause.NEW_PARKOUR);
                                    p.parkourStart(LobbyAPI.getHard());
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have started the **Hard** parkour!"));
                                }
                            } else {
                                player.getPlayer().setFlying(false);
                                if ((!player.getPreferences().isHubFlightEnabled() || (!player.hasPermission("elite") && !player.hasPermission("plus")))) {
                                    player.getPlayer().setAllowFlight(false);
                                }
                                p.parkourStart(LobbyAPI.getHard());
                                p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have started the **Hard** parkour!"));
                            }
                        }
                        break;
                    }
                    case IRON_PLATE: {
                        //End plate
                        if (e.getTo().getBlock().equals(LobbyAPI.getEasy().getEndPoint().getLocation().getBlock())) {
                            if (p.isInParkour()) {
                                if (p.getActiveParkourRun().getParkour().equals(LobbyAPI.getEasy())) {
                                    p.getActiveParkourRun().end(null);
                                    if ((!player.getPreferences().isHubFlightEnabled() || (!player.hasPermission("elite") && !player.hasPermission("plus")))) {
                                        player.getPlayer().setAllowFlight(true);
                                    }
                                } else {
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "This end point is for a different parkour!"));
                                }
                            } else {
                                p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You must start a parkour in order to end one!"));
                            }
                        } else if (e.getTo().getBlock().equals(LobbyAPI.getMedium().getEndPoint().getLocation().getBlock())) {
                            if (p.isInParkour()) {
                                if (p.getActiveParkourRun().getParkour().equals(LobbyAPI.getMedium())) {
                                    p.getActiveParkourRun().end(null);
                                    if ((!player.getPreferences().isHubFlightEnabled() || (!player.hasPermission("elite") && !player.hasPermission("plus")))) {
                                        player.getPlayer().setAllowFlight(true);
                                    }
                                } else {
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "This end point is for a different parkour!"));
                                }
                            } else {
                                p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You must start a parkour in order to end one!"));
                            }
                        } else if (e.getTo().getBlock().equals(LobbyAPI.getHard().getEndPoint().getLocation().getBlock())) {
                            if (p.isInParkour()) {
                                if (p.getActiveParkourRun().getParkour().equals(LobbyAPI.getHard())) {
                                    p.getActiveParkourRun().end(null);
                                    if ((!player.getPreferences().isHubFlightEnabled() || (!player.hasPermission("elite") && !player.hasPermission("plus")))) {
                                        player.getPlayer().setAllowFlight(true);
                                    }
                                } else {
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "This end point is for a different parkour!"));
                                }
                            } else {
                                p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You must start a parkour in order to end one!"));
                            }
                        }
                        break;
                    }
                    case GOLD_PLATE: {
                        //checkpoint plate
                        Checkpoint where = null;
                        for (Checkpoint checkpoint : LobbyAPI.getEasy().getCheckpoints()) {
                            if (e.getTo().getBlock().equals(checkpoint.getLocation().getBlock())) {
                                where = checkpoint;
                                break;
                            }
                        }
                        for (Checkpoint checkpoint : LobbyAPI.getMedium().getCheckpoints()) {
                            if (e.getTo().getBlock().equals(checkpoint.getLocation().getBlock())) {
                                where = checkpoint;
                                break;
                            }
                        }
                        for (Checkpoint checkpoint : LobbyAPI.getHard().getCheckpoints()) {
                            if (e.getTo().getBlock().equals(checkpoint.getLocation().getBlock())) {
                                where = checkpoint;
                                break;
                            }
                        }
                        if (where != null) {
                            if (p.isInParkour()) {
                                if (p.getActiveParkourRun().getParkour().equals(where.getParkour())) {
                                    p.getActiveParkourRun().checkpoint(where);
                                } else {
                                    p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "This checkpoint is for a different parkour!"));
                                }
                            } else {
                                p.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You must start a parkour in order to reach a checkpoint!"));
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}
