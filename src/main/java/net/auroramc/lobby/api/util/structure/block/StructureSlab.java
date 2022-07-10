/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util.structure.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.Step;

public class StructureSlab extends StructureBlock {

    private final boolean inverted;

    public StructureSlab(Material material, boolean inverted) {
        super(material);
        this.inverted = inverted;
    }

    @Override
    public void place(Location location) {
        super.place(location);
        BlockState state = location.getBlock().getState();
        Step step = new Step(this.material);
        step.setInverted(inverted);
        state.setData(step);
        state.update();
    }

}
