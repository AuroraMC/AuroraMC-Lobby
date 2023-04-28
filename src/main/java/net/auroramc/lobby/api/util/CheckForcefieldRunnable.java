/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.core.api.ServerAPI;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class CheckForcefieldRunnable extends BukkitRunnable {

    private final AuroraMCLobbyPlayer player;

    public CheckForcefieldRunnable(AuroraMCLobbyPlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        List<Entity> players = player.getNearbyEntities(5, 5, 5).stream().filter(entity -> entity instanceof Player).collect(Collectors.toList());
        for (Entity entity : players) {
            AuroraMCLobbyPlayer moved = player;
            AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) ServerAPI.getPlayer((Player) entity);
            if (player.getPreferences().isHubForcefieldEnabled() && (LobbyAPI.getCratePlayer() == null || LobbyAPI.getCratePlayer().equals(moved)) && !moved.getPreferences().isIgnoreHubKnockbackEnabled()) {
                Vector vector = this.player.getLocation().toVector().subtract(player.getLocation().toVector()).setY(4);
                if (vector.getX() > -0.5 && vector.getX() < 0.5) {
                    vector.setX(4);
                }
                if (vector.getZ() > -0.5 && vector.getZ() < 0.5) {
                    vector.setZ(4);
                }
                this.player.setVelocity(vector.normalize().multiply(1.5));
                this.player.playSound(this.player.getLocation(), Sound.CHICKEN_EGG_POP, 100, 1);
            } else if (moved.getPreferences().isHubForcefieldEnabled() && (LobbyAPI.getCratePlayer() == null || LobbyAPI.getCratePlayer().equals(player)) && !player.getPreferences().isIgnoreHubKnockbackEnabled()) {
                Vector vector = player.getLocation().toVector().subtract(this.player.getLocation().toVector()).setY(4);
                if (vector.getX() > -0.5 && vector.getX() < 0.5) {
                    vector.setX(4);
                }
                if (vector.getZ() > -0.5 && vector.getZ() < 0.5) {
                    vector.setZ(4);
                }
                player.setVelocity(vector.normalize().multiply(1.5));
                player.playSound(this.player.getLocation(), Sound.CHICKEN_EGG_POP, 100, 1);
            }
        }
    }
}
