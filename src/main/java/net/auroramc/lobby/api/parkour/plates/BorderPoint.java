/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.api.parkour.plates;

import org.bukkit.Location;
import org.bukkit.Material;

public class BorderPoint extends PressurePlate {

    @SuppressWarnings("unused")
    public BorderPoint(Location location) {
        super(location);
    }

    @Override
    public int getType() {
        return 4;
    }

    @Override
    public Material getMaterial() {
        return Material.AIR;
    }
}
