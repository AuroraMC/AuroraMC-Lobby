/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Cosmetic;
import net.auroramc.core.api.events.player.PlayerObjectCreationEvent;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.players.PlayerScoreboard;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONArray;

import java.lang.reflect.Field;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        updateHeaderFooter((CraftPlayer) e.getPlayer());
        e.getPlayer().setFlying(false);
        e.getPlayer().setAllowFlight(false);
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
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
        e.setPlayer(player);
        if (!player.isVanished()) {
            for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                player1.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Join", e.getPlayer().getName()));
            }
        }
        PlayerScoreboard scoreboard = player.getScoreboard();
        scoreboard.setTitle("&3-= &b&lAURORAMC&r &3=-");
        scoreboard.setLine(13, "&b&l«RANK»");
        scoreboard.setLine(12, player.getRank().getName());
        scoreboard.setLine(11, " ");
        scoreboard.setLine(10, "&b&l«CROWNS»");
        scoreboard.setLine(9, player.getBank().getCrowns() + "");
        scoreboard.setLine(8, "  ");
        scoreboard.setLine(7, "&b&l«TICKETS»");
        scoreboard.setLine(6, player.getBank().getTickets() + "");
        scoreboard.setLine(5, "   ");
        scoreboard.setLine(4, "&b&l«SERVER»");
        scoreboard.setLine(3, AuroraMCAPI.getServerInfo().getName());
        scoreboard.setLine(2, "    ");
        scoreboard.setLine(1, "&7auroramc.net");

        player.getPlayer().getInventory().setItem(8, LobbyAPI.getLobbyItem().getItem());
        player.getPlayer().getInventory().setItem(7, LobbyAPI.getPrefsItem().getItem());
        player.getPlayer().getInventory().setItem(4, LobbyAPI.getCosmeticsItem().getItem());
        player.getPlayer().getInventory().setItem(0, LobbyAPI.getGamesItem().getItem());
        player.getPlayer().getInventory().setItem(1, LobbyAPI.getStatsItem().getItem());
    }

    private static void updateHeaderFooter(CraftPlayer player2) {
        try {
            IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{\"text\": \"LOBBY\",\"color\":\"dark_aqua\",\"bold\":\"true\"}");
            IChatBaseComponent footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \"Purchase ranks, cosmetics and more at store.auroramc.net!\",\"color\":\"aqua\",\"bold\":\"false\"}");

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
