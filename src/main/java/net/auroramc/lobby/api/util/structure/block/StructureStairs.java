/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.api.util.structure.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Stairs;

public class StructureStairs extends StructureBlock {

    private final BlockFace direction;
    private final boolean inverted;

    public StructureStairs(Material material, BlockFace direction) {
        super(material);
        this.direction = direction;
        this.inverted = false;
    }

    public StructureStairs(Material material, BlockFace direction, boolean inverted) {
        super(material);
        this.direction = direction;
        this.inverted = inverted;
    }

    @Override
    public void place(Location location) {
        super.place(location);
        BlockState state = location.getBlock().getState();
        Stairs stairs = new Stairs(this.material);
        stairs.setFacingDirection(direction);
        stairs.setInverted(inverted);
        state.setData(stairs);
        state.update();
    }
}
