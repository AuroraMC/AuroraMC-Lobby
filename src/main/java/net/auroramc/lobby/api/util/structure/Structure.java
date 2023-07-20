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

public class Structure {

    private final int x;
    private final int y;
    private final int z;
    private final Map<Integer, StructureLevel> levels;

    public Structure(int x, int y, int z) {
        this.levels = new HashMap<>();
        this.x = x;
        this.y = y;
        this.z = z;
        for (int i = 0;i < y;i++) {
            levels.put(i, new StructureLevel(this, x, z, i));
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void put(int x, int y, int z, StructureBlock block) {
        levels.get(y).put(x, z, block);
    }

    public StructureLevel getLevel(int level) {
        return levels.get(level);
    }

    public void place(Location bottomCorner)  {
        for (int i = 0;i < y;i++) {
            Location levelCorner = bottomCorner.clone();
            levelCorner.setY(levelCorner.getY() + i);
            levels.get(i).place(levelCorner);
        }
    }

}
