/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class CheckForcefieldRunnable extends BukkitRunnable {

    private final AuroraMCLobbyPlayer player;

    public CheckForcefieldRunnable(AuroraMCLobbyPlayer player) {
        this.player = player;
    }

    @Override
    public void run() {
        List<Entity> players = player.getPlayer().getNearbyEntities(7, 7, 7).stream().filter(entity -> entity instanceof Player).collect(Collectors.toList());
        for (Entity entity : players) {
            AuroraMCLobbyPlayer moved = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer(player.getPlayer());
            AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) AuroraMCAPI.getPlayer((Player) entity);
            if (player.getPreferences().isHubForcefieldEnabled() && !moved.getPreferences().isIgnoreHubKnockbackEnabled()) {
                this.player.getPlayer().setVelocity(this.player.getPlayer().getLocation().toVector().subtract(player.getPlayer().getLocation().toVector()).setY(3).multiply(1.5));
                this.player.getPlayer().playSound(this.player.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 100, 1);
            } else if (moved.getPreferences().isHubForcefieldEnabled() && !player.getPreferences().isIgnoreHubKnockbackEnabled()) {
                player.getPlayer().setVelocity(player.getPlayer().getLocation().toVector().subtract(this.player.getPlayer().getPlayer().getLocation().toVector()).setY(3).multiply(1.5));
                player.getPlayer().playSound(this.player.getPlayer().getLocation(), Sound.CHICKEN_EGG_POP, 100, 1);
            }
        }
    }
}
