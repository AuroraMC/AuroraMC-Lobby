/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util.structure;

import net.auroramc.lobby.api.util.structure.block.StructureBlock;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class StructureLevel {

    private final Structure structure;
    private final int x;
    private final int z;
    private final int level;
    private final Map<Integer, StructureRow> rows;

    public StructureLevel(Structure structure, int x, int z, int level) {
        this.structure = structure;
        this.rows = new HashMap<>();
        this.x = x;
        this.level = level;
        this.z = z;
        for (int i = 0;i < x;i++) {
            rows.put(i, new StructureRow(this, x, z));
        }
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    public int getLevel() {
        return level;
    }

    public Structure getStructure() {
        return structure;
    }

    public StructureRow getRow(int row) {
        return rows.get(row);
    }

    public void put(int x, int z, StructureBlock block) {
        rows.get(x).put(z, block);
    }

    public void place(Location corner) {
        for (int i = 0;i < x;i++) {
            Location end = corner.clone();
            end.setX(end.getX() + i);
            rows.get(i).place(end);
        }
    }

}
