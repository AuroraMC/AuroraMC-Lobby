/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

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
