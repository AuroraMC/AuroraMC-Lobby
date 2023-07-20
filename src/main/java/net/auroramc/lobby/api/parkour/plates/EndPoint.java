/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

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
