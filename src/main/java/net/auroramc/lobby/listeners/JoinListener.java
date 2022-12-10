/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.events.player.PlayerObjectCreationEvent;
import net.auroramc.core.api.permissions.Permission;
import net.auroramc.core.api.permissions.Rank;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.players.scoreboard.PlayerScoreboard;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.ServerState;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.json.JSONArray;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        Rank rank = AuroraMCAPI.getDbManager().getRank(e.getUniqueId());
        if (!rank.hasPermission("elite")) {
            if (AuroraMCAPI.getPlayers().size() >= 80) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "This lobby is currently full. In order to bypass this, you need to purchase a rank!");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        updateHeaderFooter((CraftPlayer) e.getPlayer());
        e.getPlayer().setFlying(false);
        e.getPlayer().setAllowFlight(false);
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
        e.getPlayer().setHealth(20);
        e.getPlayer().setFoodLevel(30);
        e.getPlayer().getInventory().clear();
        e.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        e.getPlayer().setExp(0);
        e.getPlayer().setLevel(0);
        e.getPlayer().getEnderChest().clear();
        for (PotionEffect pe : e.getPlayer().getActivePotionEffects()) {
            e.getPlayer().removePotionEffect(pe.getType());
        }
        JSONArray spawnLocations = LobbyAPI.getLobbyMap().getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
        if (spawnLocations == null || spawnLocations.length() == 0) {
            LobbyAPI.getLobby().getLogger().info("An invalid lobby was supplied, assuming 0, 64, 0 spawn position.");
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

    }

    @EventHandler
    public void onObjectCreate(PlayerObjectCreationEvent e) {
        AuroraMCLobbyPlayer player = new AuroraMCLobbyPlayer(e.getPlayer());
        new BukkitRunnable(){
            @Override
            public void run() {
                player.getPlayer().setAllowFlight(true);
            }
        }.runTask(AuroraMCAPI.getCore());
        for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
            if (!player1.equals(player) && player1.isLoaded()) {
                if (!player.getPreferences().isHubVisibilityEnabled() || (player1.getPreferences().isHubInvisibilityEnabled() && !player.hasPermission("moderation"))) {
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.getPlayer().hidePlayer(player1.getPlayer());
                        }
                    }.runTask(AuroraMCAPI.getCore());
                }
                if (player1.isLoaded()) {
                    if (!player1.getPreferences().isHubVisibilityEnabled() || (player.getPreferences().isHubInvisibilityEnabled() && !player1.hasPermission("moderation"))) {
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                player1.getPlayer().hidePlayer(player.getPlayer());
                            }
                        }.runTask(AuroraMCAPI.getCore());
                    }
                }
            }
        }


        e.setPlayer(player);
        if (!player.isVanished()) {
            if (player.hasPermission(Permission.MASTER.getId()) && !player.getPreferences().isHubInvisibilityEnabled()) {
                if (player.isDisguised()) {
                    if (player.getActiveDisguise().getRank().hasPermission(Permission.MASTER.getId())) {
                        for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                            if (player1.equals(player)) {
                                if (player.getPreferences().isHideDisguiseNameEnabled()) {
                                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().convert("&" + player.getRank().getPrefixColor() + "&l" + player.getRank().getPrefixAppearance() + " " + player.getName() + " has joined the lobby!"));
                                    continue;
                                }
                            }
                            player1.getPlayer().sendMessage(AuroraMCAPI.getFormatter().convert("&" + player.getActiveDisguise().getRank().getPrefixColor() + "&l" + player.getActiveDisguise().getRank().getPrefixAppearance() + " " + player.getActiveDisguise().getName() + " has joined the lobby!"));
                        }
                    }
                } else {
                    for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                        player1.getPlayer().sendMessage(AuroraMCAPI.getFormatter().convert("&" + player.getRank().getPrefixColor() + "&l" + player.getRank().getPrefixAppearance() + " " + player.getPlayer().getName() + " has joined the lobby!"));
                    }
                }
            }
        }
        PlayerScoreboard scoreboard = player.getScoreboard();
        scoreboard.setTitle("&3-= &b&lAURORAMC&r &3=-");
        scoreboard.setLine(14, " ");
        scoreboard.setLine(13, "&c&l«RANK»");
        scoreboard.setLine(12, player.getRank().getName());
        scoreboard.setLine(11, " ");
        scoreboard.setLine(10, "&6&l«CROWNS»");

        double crowns = player.getBank().getCrowns();
        String suffC = "";

        if (crowns >= 1000000) {
            crowns = crowns / 10000;
            crowns = Math.floor(crowns) / 100d;
            suffC = " million";
            if (crowns >= 1000) {
                crowns = crowns / 10;
                crowns = Math.floor(crowns) / 100d;
                suffC = " billion";
                if (crowns >= 1000) {
                    crowns = crowns / 10;
                    crowns = Math.floor(crowns) / 100d;
                    suffC = " trillion";
                }
            }
        }

        double tickets = player.getBank().getTickets();
        String suffT = "";

        if (tickets >= 1000000) {
            if (tickets >= 1000000000) {
                if (tickets >= 1000000000000d) {
                    tickets = tickets / 10000000000L;
                    tickets = Math.floor(tickets) / 100d;
                    suffT = " trillion";
                } else {
                    tickets = tickets / 10000000;
                    tickets = Math.floor(tickets) / 100d;
                    suffT = " billion";
                }
            } else {
                tickets = tickets / 10000;
                tickets = Math.floor(tickets) / 100d;
                suffT = " million";
            }
        }

        scoreboard.setLine(9, ((suffC.equals(""))?String.format("%,d",player.getBank().getCrowns()):crowns + suffC));
        scoreboard.setLine(8, "  ");
        scoreboard.setLine(7, "&d&l«TICKETS»");
        scoreboard.setLine(6, ((suffT.equals(""))?String.format("%,d",player.getBank().getTickets()):tickets + suffT));
        scoreboard.setLine(5, "   ");
        scoreboard.setLine(4, "&a&l«SERVER»");
        if (player.getPreferences().isHideDisguiseNameEnabled() && player.isDisguised()) {
            scoreboard.setLine(3, "&oHidden");
        } else {
            scoreboard.setLine(3, AuroraMCAPI.getServerInfo().getName());
        }
        scoreboard.setLine(2, "    ");
        scoreboard.setLine(1, "&7auroramc.net");

        Scoreboard scoreboard2 = player.getScoreboard().getScoreboard();
        Team team = scoreboard2.registerNewTeam("cq");
        team.setPrefix("§b§l");
        team.setSuffix("§3§lFEATURED!");
        team.addEntry("Crystal Quest ");

        team = scoreboard2.registerNewTeam("ds");
        team.setPrefix("§c§l");
        team.setSuffix("§7v" + LobbyAPI.getVersionNumber("DUELS").trim());
        team.addEntry("Duels§r ");

        team = scoreboard2.registerNewTeam("pb");
        team.setPrefix("§a§l");
        team.setSuffix("§7v" + LobbyAPI.getVersionNumber("PAINTBALL").trim());
        team.addEntry("Paintball§r ");

        team = scoreboard2.registerNewTeam("ac");
        team.setPrefix("§e§l");
        team.setSuffix("§7v" + LobbyAPI.getVersionNumber("ARCADE_MODE").trim());
        team.addEntry("Arcade Mode§r ");

        player.getPlayer().getInventory().setItem(8, LobbyAPI.getLobbyItem().getItem());
        player.getPlayer().getInventory().setItem(7, LobbyAPI.getPrefsItem().getItem());
        player.getPlayer().getInventory().setItem(4, LobbyAPI.getCosmeticsItem().getItem());
        player.getPlayer().getInventory().setItem(0, LobbyAPI.getGamesItem().getItem());
        player.getPlayer().getInventory().setItem(1, LobbyAPI.getStatsItem(player.getName()).getItem());

        if (LobbyAPI.getPoll() != null) {
            if (!LobbyDatabaseManager.hasVoted(LobbyAPI.getPoll().getId(), player.getId())) {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Community Polls", "There is currently a poll active that you haven't voted in! Visit **The Monke** to vote! Every vote counts!"));
            }
        }
    }

    private static void updateHeaderFooter(CraftPlayer player2) {
        try {
            IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§3§lAURORAMC\",\"color\":\"dark_aqua\",\"bold\":\"false\"}");
            IChatBaseComponent footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \"\n§3You are currently connected to §b" + AuroraMCAPI.getServerInfo().getName() + "\"\n\n§bPurchase ranks, cosmetics and more at store.auroramc.net!\",\"color\":\"aqua\",\"bold\":\"false\"}");

            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
            Field ff = packet.getClass().getDeclaredField("a");
            ff.setAccessible(true);
            ff.set(packet, header);

            ff = packet.getClass().getDeclaredField("b");
            ff.setAccessible(true);
            ff.set(packet, footer);

            player2.getHandle().playerConnection.sendPacket(packet);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }


}
