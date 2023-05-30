/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.api.parkour.plates;

import org.bukkit.Location;
import org.bukkit.Material;

public class Checkpoint extends PressurePlate {

    private int checkpointNo;

    @SuppressWarnings("unused")
    public Checkpoint(Location location, int checkpointNo) {
        super(location);
        this.checkpointNo = checkpointNo;
    }

    public int getType() {
        return 3;
    }

    @Override
    public Material getMaterial() {
        return Material.GOLD_PLATE;
    }

    public int getCheckpointNo() {
        return checkpointNo;
    }

    public void setCheckpointNo(int checkpointNo) {
        this.checkpointNo = checkpointNo;
    }
}
