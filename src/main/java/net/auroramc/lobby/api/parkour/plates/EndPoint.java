package net.auroramc.lobby.api.parkour.plates;

import org.bukkit.Location;
import org.bukkit.Material;

public class EndPoint extends PressurePlate {
    @SuppressWarnings("unused")
    public EndPoint(Location location) {
        super(location);
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public Material getMaterial() {
        return Material.IRON_PLATE;
    }
}
