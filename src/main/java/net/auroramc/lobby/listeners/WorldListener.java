/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.parkour.plates.PressurePlate;
import net.auroramc.lobby.utils.VoidGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Chest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
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
            e.getWorld().setGameRuleValue("randomTickSpeed", "0");
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (e.getWorld().getName().equalsIgnoreCase("world")) {
            e.getWorld().setGameRuleValue("doMobSpawning", "false");
            e.getWorld().setGameRuleValue("doDaylightCycle", "false");
            e.getWorld().setGameRuleValue("doFireTick", "false");
            e.getWorld().setGameRuleValue("randomTickSpeed", "0");
            LobbyAPI.getLobby().getLogger().info("Loading parkours...");
            LobbyAPI.loadParkours();
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
            Chest chest = new Chest(direction);
            state.setData(chest);
            state.update();

            LobbyAPI.setChestBlock(location.getBlock());
            LobbyAPI.getEasy().generateHolograms();
            LobbyAPI.getMedium().generateHolograms();
            LobbyAPI.getHard().generateHolograms();
            LobbyAPI.getEasy().getAllPoints().forEach(PressurePlate::placeMaterial);
            LobbyAPI.getMedium().getAllPoints().forEach(PressurePlate::placeMaterial);
            LobbyAPI.getHard().getAllPoints().forEach(PressurePlate::placeMaterial);

        }
    }

}
