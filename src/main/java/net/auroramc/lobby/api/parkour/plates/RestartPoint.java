package net.auroramc.lobby.api.parkour.plates;

import org.bukkit.Location;
import org.bukkit.Material;

public class RestartPoint extends PressurePlate {

    @SuppressWarnings("unused")
    public RestartPoint(Location location) {
        super(location);
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public Material getMaterial() {
        return Material.AIR;
    }
}
