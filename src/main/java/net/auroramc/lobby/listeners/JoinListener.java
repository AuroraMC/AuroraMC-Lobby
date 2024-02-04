/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.permissions.Permission;
import net.auroramc.api.permissions.Rank;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.events.player.PlayerObjectCreationEvent;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.player.scoreboard.PlayerScoreboard;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
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
import java.util.logging.Level;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        Rank rank = AuroraMCAPI.getDbManager().getRank(e.getUniqueId());
        if (!rank.hasPermission("elite")) {
            if (ServerAPI.getPlayers().size() >= 80) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "This lobby is currently full. In order to bypass this, you need to purchase a rank!");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
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
        updateHeaderFooter(e.getPlayer());
        AuroraMCLobbyPlayer player = new AuroraMCLobbyPlayer(e.getPlayer());

        new BukkitRunnable(){
            @Override
            public void run() {
                if (player.getLinkedDiscord() == null) {
                    TextComponent textComponent = new TextComponent("");

                    TextComponent lines = new TextComponent("-----------------------------------------------------");
                    lines.setStrikethrough(true);
                    lines.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
                    textComponent.addExtra(lines);

                    textComponent.addExtra("\n");

                    TextComponent enjoy = new TextComponent("Join our Discord to be a part of the AuroraMC Community!");
                    enjoy.setBold(true);
                    enjoy.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                    textComponent.addExtra(enjoy);

                    textComponent.addExtra("\n \n");

                    TextComponent purchase = new TextComponent("Our Discord is the one-stop-shop for everything in the AuroraMC community! Talk with other players, play games, and much more! If you have a rank, you even get cool Discord-exclusive perks! Join now at ");
                    textComponent.addExtra(purchase);

                    TextComponent store = new TextComponent("discord.auroramc.net");
                    store.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join our Discord community!").color(net.md_5.bungee.api.ChatColor.GREEN).create()));
                    store.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.auroramc.net"));
                    store.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                    textComponent.addExtra(store);

                    TextComponent link = new TextComponent("!\n" +
                            " \n" +
                            "Link your Discord account to remove this message.\n");
                    textComponent.addExtra(link);
                    textComponent.addExtra("\n");
                    textComponent.addExtra(lines);
                    player.sendMessage(textComponent);
                }
            }
        }.runTaskLaterAsynchronously(ServerAPI.getCore(), 100);
        new BukkitRunnable(){
            @Override
            public void run() {
                player.setAllowFlight(true);
            }
        }.runTask(ServerAPI.getCore());
        for (AuroraMCServerPlayer player1 : ServerAPI.getPlayers()) {
            if (!player1.equals(player) && player1.isLoaded()) {
                if (!player.getPreferences().isHubVisibilityEnabled() || (player1.getPreferences().isHubInvisibilityEnabled() && !player.hasPermission("moderation"))) {
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            player.hidePlayer(player1);
                        }
                    }.runTask(ServerAPI.getCore());
                }
                if (player1.isLoaded()) {
                    if (!player1.getPreferences().isHubVisibilityEnabled() || (player.getPreferences().isHubInvisibilityEnabled() && !player1.hasPermission("moderation"))) {
                        new BukkitRunnable(){
                            @Override
                            public void run() {
                                player1.hidePlayer(player);
                            }
                        }.runTask(ServerAPI.getCore());
                    }
                }
            }
        }


        e.setPlayer(player);
        if (!player.isVanished()) {
            if (player.hasPermission(Permission.MASTER.getId()) && !player.getPreferences().isHubInvisibilityEnabled()) {
                if (player.isDisguised()) {
                    if (player.getActiveDisguise().getRank().hasPermission(Permission.MASTER.getId())) {
                        for (AuroraMCServerPlayer player1 : ServerAPI.getPlayers()) {
                            if (player1.equals(player)) {
                                if (player.getPreferences().isHideDisguiseNameEnabled()) {
                                    player.sendMessage(new TextComponent(TextFormatter.convert(player.getRank().getPrefixColor() + "&l" + player.getRank().getPrefixAppearance() + " " + player.getName() + " has joined the lobby!")));
                                    continue;
                                }
                            }
                            player1.sendMessage(new TextComponent(TextFormatter.convert(player.getActiveDisguise().getRank().getPrefixColor() + "&l" + player.getActiveDisguise().getRank().getPrefixAppearance() + " " + player.getActiveDisguise().getName() + " has joined the lobby!")));
                        }
                        if (player.getPreferences().isHideDisguiseNameEnabled()) {
                            player.sendMessage(new TextComponent(TextFormatter.convert(player.getRank().getPrefixColor() + "&l" + player.getRank().getPrefixAppearance() + " " + player.getName() + " has joined the lobby!")));
                        } else {
                            player.sendMessage(new TextComponent(TextFormatter.convert(player.getActiveDisguise().getRank().getPrefixColor() + "&l" + player.getActiveDisguise().getRank().getPrefixAppearance() + " " + player.getActiveDisguise().getName() + " has joined the lobby!")));
                        }
                    }
                } else {
                    for (AuroraMCServerPlayer player1 : ServerAPI.getPlayers()) {
                        player1.sendMessage(new TextComponent(TextFormatter.convert(player.getRank().getPrefixColor() + "&l" + player.getRank().getPrefixAppearance() + " " + player.getName() + " has joined the lobby!")));
                    }
                    player.sendMessage(new TextComponent(TextFormatter.convert(player.getRank().getPrefixColor() + "&l" + player.getRank().getPrefixAppearance() + " " + player.getName() + " has joined the lobby!")));
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
            scoreboard.setLine(3, AuroraMCAPI.getInfo().getName());
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

        team = scoreboard2.registerNewTeam("ln");
        team.setPrefix("§6§lLieutenant§r");
        team.addEntry(" §e§lLuna");

        team = scoreboard2.registerNewTeam("skye");
        team.setPrefix("§3§lSergeant§r ");
        team.addEntry("§b§lSkye");

        team = scoreboard2.registerNewTeam("cmt");
        team.setPrefix("§5§lColonel§r ");
        team.addEntry("§d§lComet");

        player.getInventory().setItem(8, LobbyAPI.getLobbyItem().getItemStack());
        player.getInventory().setItem(7, LobbyAPI.getPrefsItem().getItemStack());
        player.getInventory().setItem(4, LobbyAPI.getCosmeticsItem().getItemStack());
        player.getInventory().setItem(0, LobbyAPI.getGamesItem().getItemStack());
        player.getInventory().setItem(1, LobbyAPI.getStatsItem(player.getName()).getItemStack());

        if (LobbyAPI.getPoll() != null) {
            if (!LobbyDatabaseManager.hasVoted(LobbyAPI.getPoll().getId(), player.getId())) {
                player.sendMessage(TextFormatter.pluginMessage("Community Polls", "There is currently a poll active that you haven't voted in! Visit **Lieutenant Luna** to vote! Every vote counts!"));
            }
        }
    }

    private static void updateHeaderFooter(AuroraMCServerPlayer player) {
        try {
            CraftPlayer player2 = player.getCraft();
            IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{\"text\": \"§3§lAURORAMC NETWORK         §b§lAURORAMC.NET\",\"color\":\"dark_aqua\",\"bold\":\"false\"}");
            IChatBaseComponent footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \"\n§fYou are currently connected to §b" + ((player.isDisguised() && player.getPreferences().isHideDisguiseNameEnabled())?"§oHidden":AuroraMCAPI.getInfo().getName()) + "\n\n" +
                    "§rForums §3§l» §bauroramc.net\n" +
                    "§rStore §3§l» §bstore.auroramc.net\n" +
                    "§rDiscord §3§l» §bdiscord.auroramc.net\n" +
                    "\",\"color\":\"aqua\",\"bold\":\"false\"}");

            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
            Field ff = packet.getClass().getDeclaredField("a");
            ff.setAccessible(true);
            ff.set(packet, header);

            ff = packet.getClass().getDeclaredField("b");
            ff.setAccessible(true);
            ff.set(packet, footer);

            player2.getHandle().playerConnection.sendPacket(packet);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", ex);
        }
    }


}
