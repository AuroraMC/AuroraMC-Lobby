/*
 * Copyright (c) 2022-2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.lobby.api.util.structure.block;

import org.bukkit.Location;
import org.bukkit.Material;

public class StructureBlock {

    protected final Material material;
    protected final byte data;

    public StructureBlock() {
        this.material = Material.AIR;
        this.data = (byte)0;
    }

    public StructureBlock(Material material) {
        this.material = material;
        this.data = (byte)0;
    }

    public StructureBlock(Material material, byte data) {
        this.material = material;
        this.data = data;
    }

    public void place(Location location) {
        location.getBlock().setType(material);
        location.getBlock().setData(data);
    }

}
