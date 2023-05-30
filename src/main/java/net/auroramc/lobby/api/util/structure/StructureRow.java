/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.api.util.structure;

import net.auroramc.lobby.api.util.structure.block.StructureBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class StructureRow {

    private final StructureLevel structureLevel;
    private final int row;
    private final int z;
    private final Map<Integer, StructureBlock> blocks;

    public StructureRow(StructureLevel structureLevel, int z, int row) {
        this.structureLevel = structureLevel;
        this.blocks = new HashMap<>();
        this.row = row;
        this.z = z;
        for (int i = 0;i < z;i++) {
            blocks.put(i, null);
        }
    }

    public int getZ() {
        return z;
    }

    public int getRow() {
        return row;
    }

    public void put(int z, StructureBlock block) {
        blocks.put(z, block);
    }

    public StructureLevel getStructureLevel() {
        return structureLevel;
    }

    public void place(Location end) {
        for (int i = 0;i < z;i++) {
            if (blocks.get(i) == null) {
                continue;
            }
            Location block = end.clone();
            block.setZ(end.getZ() + i);
            blocks.get(i).place(block);
        }
    }
}
