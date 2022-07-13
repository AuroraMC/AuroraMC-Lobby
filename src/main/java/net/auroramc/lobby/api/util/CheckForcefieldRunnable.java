/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
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
        List<Entity> players = player.getPlayer().getNearbyEntities(5, 5, 5).stream().filter(entity -> entity instanceof Player).collect(Collectors.toList());
        for (Entity entity : players) {
            AuroraMCLobbyPlayer moved = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(player.getPlayer());
            AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer((Player) entity);
            if (player.getPreferences().isHubForcefieldEnabled() && (LobbyAPI.getCratePlayer() == null || LobbyAPI.getCratePlayer().getPlayer().equals(moved.getPlayer())) && !moved.getPreferences().isIgnoreHubKnockbackEnabled()) {
                Vector vector = this.player.getPlayer().getLocation().toVector().subtract(player.getPlayer().getLocation().toVector()).setY(4);
                if (vector.getX() > -0.5 && vector.getX() < 0.5) {
                    vector.setX(4);
                }
                if (vector.getZ() > -0.5 && vector.getZ() < 0.5) {
                    vector.setZ(4);
                }
                this.player.getPlayer().setVelocity(vector.normalize().multiply(1.5));
                this.player.getPlayer().playSound(this.player.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 100, 1);
            } else if (moved.getPreferences().isHubForcefieldEnabled() && (LobbyAPI.getCratePlayer() == null || LobbyAPI.getCratePlayer().getPlayer().equals(player.getPlayer())) && !player.getPreferences().isIgnoreHubKnockbackEnabled()) {
                Vector vector = player.getPlayer().getLocation().toVector().subtract(this.player.getPlayer().getPlayer().getLocation().toVector()).setY(4);
                if (vector.getX() > -0.5 && vector.getX() < 0.5) {
                    vector.setX(4);
                }
                if (vector.getZ() > -0.5 && vector.getZ() < 0.5) {
                    vector.setZ(4);
                }
                player.getPlayer().setVelocity(vector.normalize().multiply(1.5));
                player.getPlayer().playSound(this.player.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 100, 1);
            }
        }
    }
}
