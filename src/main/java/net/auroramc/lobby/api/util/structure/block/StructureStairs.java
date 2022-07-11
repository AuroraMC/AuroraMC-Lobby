/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
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
