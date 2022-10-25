/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.events.player.PlayerLeaveEvent;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.CrateStructures;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.material.Chest;
import org.json.JSONObject;

public class LeaveListener implements Listener {

    @EventHandler
    public void onLeave(PlayerLeaveEvent e) {
        if (e.getPlayer().isLoaded()) {
            AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) e.getPlayer();
            player.deactivateForcefield();
            player.getStats().addLobbyTime(System.currentTimeMillis() - player.getJoinTimestamp(), true);
            if (LobbyAPI.getCratePlayer() != null && LobbyAPI.getCratePlayer().getPlayer().equals(e.getPlayer().getPlayer())) {
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
                LobbyAPI.finishOpen();
            }
        }
    }

}
