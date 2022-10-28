package net.auroramc.lobby.api.parkour.plates;

import org.bukkit.Location;
import org.bukkit.Material;

public class StartPoint extends PressurePlate {

    @SuppressWarnings("unused")
    public StartPoint(Location location) {
        super(location);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public Material getMaterial() {
        return Material.WOOD_PLATE;
    }
}
