/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.parkour;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.utils.holograms.Hologram;
import net.auroramc.core.api.utils.holograms.HologramLine;
import net.auroramc.core.api.utils.holograms.universal.UniversalHologramLine;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardHologram {

    private final Location location;
    private final Parkour parkour;
    private Hologram hologram;

    public LeaderboardHologram(Location location, Parkour parkour) {
        this.parkour = parkour;
        this.location = location;
    }

    public void generate() {
        if (hologram != null) return;
        hologram = new Hologram(null, location, null);
        refresh();
        hologram.spawn();
    }

    public void remove() {
        if (hologram != null) {
            hologram.despawn();
        }
    }

    public void refresh() {
        List<HologramLine> lines = new ArrayList<>();
        lines.add(new UniversalHologramLine(hologram, "&3&lLeaderboard for " + parkour.getName() + "Parkour:", 1));
        Map<Integer, List<String>> leaderboard = LobbyDatabaseManager.getLeaderboard(parkour);
        int i = 2;
        for (int place : leaderboard.keySet()) {
            List<String> record = leaderboard.get(place);
            lines.add(new UniversalHologramLine(hologram, "&b#" + place + "&r - &b" + record.get(0) + "&r - &b" + Parkour.formatTime(Long.parseLong(record.get(1))), i));
            i++;
        }
        hologram.setLines(lines);
        hologram.update();
    }

    public Location getLocation() {
        return location.clone();
    }

    public Parkour getParkour() {
        return parkour;
    }

}
