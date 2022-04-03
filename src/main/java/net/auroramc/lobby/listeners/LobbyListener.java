/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.events.player.PlayerPreferenceChangeEvent;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.gui.cosmetics.Cosmetics;
import net.auroramc.core.gui.preferences.Preferences;
import net.auroramc.core.gui.stats.stats.Stats;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.GameMenu;
import net.auroramc.lobby.gui.LobbySwitcher;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.json.JSONArray;


public class LobbyListener implements Listener {

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
                        AuroraMCPlayer target = AuroraMCAPI.getPlayer((Player) e.getEntity());
                        AuroraMCPlayer damager = AuroraMCAPI.getPlayer((Player) ((EntityDamageByEntityEvent) e).getDamager());
                        if (damager.hasPermission("elite") && target.hasPermission("moderation")) {
                            if (((AuroraMCLobbyPlayer)target).canBePunched() || damager.hasPermission("admin")) {
                                target.getPlayer().setVelocity(new Vector(0, 10, 0));
                                target.getPlayer().getLocation().getWorld().createExplosion(target.getPlayer().getLocation().getBlockX(), target.getPlayer().getLocation().getBlockY(), target.getPlayer().getLocation().getBlockZ(), 2, false, false);
                                ((AuroraMCLobbyPlayer)target).punched();
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
            if (e.getItem() != null && e.getItem().getType() != Material.AIR) {
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
    public void onInvMove(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPreferenceChange(PlayerPreferenceChangeEvent e) {
        e.getPlayer().getPlayer().setAllowFlight(e.getPlayer().getPreferences().isHubFlightEnabled());
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
}
