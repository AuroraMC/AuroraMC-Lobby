/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
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
