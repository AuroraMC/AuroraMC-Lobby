/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.utils.VoidGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.json.JSONObject;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        ((CraftWorld)e.getWorld()).getHandle().generator = new VoidGenerator(LobbyAPI.getLobby());
        if (e.getWorld().getName().equalsIgnoreCase("world")) {
            e.getWorld().setGameRuleValue("doMobSpawning", "false");
            e.getWorld().setGameRuleValue("doDaylightCycle", "false");
            e.getWorld().setGameRuleValue("doFireTick", "false");
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getWorld().getName().equalsIgnoreCase("world")) {
            e.getWorld().setGameRuleValue("doMobSpawning", "false");
            e.getWorld().setGameRuleValue("doDaylightCycle", "false");
            e.getWorld().setGameRuleValue("doFireTick", "false");
            JSONObject mapData = LobbyAPI.getLobbyMap().getMapData();
            if (mapData.has("time")) {
                e.getWorld().setTime(mapData.getInt("time"));
            } else {
                e.getWorld().setTime(6000);
            }
            e.getWorld().setThundering(false);
            e.getWorld().setStorm(false);

            LobbyAPI.spawnEntities();

            JSONObject crateLocation = mapData.getJSONObject("game").getJSONArray("CRATE").getJSONObject(0);
            int x = crateLocation.getInt("x");
            int y = crateLocation.getInt("y");
            int z = crateLocation.getInt("z");
            Location location = new Location(e.getWorld(), x, y, z);
            location.getBlock().setType(Material.CHEST);
            location.setY(location.getY() + 1);
            location.setX(location.getX() + 0.5);
            location.setZ(location.getZ() + 0.5);
            ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
            stand.setVisible(false);
            stand.setCustomName(AuroraMCAPI.getFormatter().convert(AuroraMCAPI.getFormatter().highlight("&a&lOpen Crates")));
            stand.setCustomNameVisible(true);
            stand.setSmall(true);
            stand.setMarker(true);
            stand.setGravity(false);
        }
    }

}
