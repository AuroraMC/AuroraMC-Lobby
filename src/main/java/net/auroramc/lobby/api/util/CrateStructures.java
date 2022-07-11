/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.lobby.api.util.structure.Structure;
import net.auroramc.lobby.api.util.structure.block.StructureBlock;
import net.auroramc.lobby.api.util.structure.block.StructureChest;
import net.auroramc.lobby.api.util.structure.block.StructureStairs;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class CrateStructures {

    private static final Structure baseCrate;
    private static final Structure ironCrate;
    private static final Structure goldCrate;
    private static final Structure diamondCrate;
    private static final Structure emeraldCrate;

    static {
        baseCrate = new Structure(7, 7, 7);
        baseCrate.put(0, 0, 0, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(1, 0, 0, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.SOUTH));
        baseCrate.put(2, 0, 0, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.SOUTH));
        baseCrate.put(3, 0, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, true));
        baseCrate.put(4, 0, 0, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.SOUTH));
        baseCrate.put(5, 0, 0, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.SOUTH));
        baseCrate.put(6, 0, 0, new StructureBlock(Material.HARD_CLAY));

        baseCrate.put(0, 0, 1, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.EAST));
        baseCrate.put(1, 0, 1, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        baseCrate.put(2, 0, 1, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(3, 0, 1, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(4, 0, 1, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(5, 0, 1, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        baseCrate.put(6, 0, 1, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.WEST));

        baseCrate.put(0, 0, 2, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.EAST));
        baseCrate.put(1, 0, 2, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(2, 0, 2, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH));
        baseCrate.put(3, 0, 2, new StructureStairs(Material.JUNGLE_WOOD_STAIRS, BlockFace.NORTH));
        baseCrate.put(4, 0, 2, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH));
        baseCrate.put(5, 0, 2, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(6, 0, 2, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.WEST));

        baseCrate.put(0, 0, 3, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, true));
        baseCrate.put(1, 0, 3, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(2, 0, 3, new StructureStairs(Material.JUNGLE_WOOD_STAIRS, BlockFace.WEST));
        baseCrate.put(3, 0, 3, new StructureBlock(Material.GLOWSTONE));
        baseCrate.put(4, 0, 3, new StructureStairs(Material.JUNGLE_WOOD_STAIRS, BlockFace.EAST));
        baseCrate.put(5, 0, 3, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(6, 0, 3, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, true));

        baseCrate.put(0, 0, 4, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.EAST));
        baseCrate.put(1, 0, 4, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(2, 0, 4, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH));
        baseCrate.put(3, 0, 4, new StructureStairs(Material.JUNGLE_WOOD_STAIRS, BlockFace.SOUTH));
        baseCrate.put(4, 0, 4, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH));
        baseCrate.put(5, 0, 4, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(6, 0, 4, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.WEST));

        baseCrate.put(0, 0, 5, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.EAST));
        baseCrate.put(1, 0, 5, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        baseCrate.put(2, 0, 5, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(3, 0, 5, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(4, 0, 5, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(5, 0, 5, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        baseCrate.put(6, 0, 5, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.WEST));

        baseCrate.put(0, 0, 6, new StructureBlock(Material.HARD_CLAY));
        baseCrate.put(1, 0, 6, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.NORTH));
        baseCrate.put(2, 0, 6, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.NORTH));
        baseCrate.put(3, 0, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, true));
        baseCrate.put(4, 0, 6, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.NORTH));
        baseCrate.put(5, 0, 6, new StructureStairs(Material.ACACIA_STAIRS, BlockFace.NORTH));
        baseCrate.put(6, 0, 6, new StructureBlock(Material.HARD_CLAY));

        for (int y = 1; y < 7; y++) {
            for (int x = 0;x < 7;x++) {
                for (int z = 0; z < 7;z++) {
                    baseCrate.put(x, y, z, new StructureBlock());
                }
            }
        }



        ironCrate = new Structure(7, 2, 7);
        ironCrate.put(6, 0, 6, new StructureBlock(Material.IRON_BLOCK));
        ironCrate.put(6, 0, 0, new StructureBlock(Material.IRON_BLOCK));
        ironCrate.put(0, 0, 6, new StructureBlock(Material.IRON_BLOCK));
        ironCrate.put(0, 0, 0, new StructureBlock(Material.IRON_BLOCK));
        ironCrate.put(6, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        ironCrate.put(6, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        ironCrate.put(0, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        ironCrate.put(0, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));

        goldCrate = new Structure(7, 3, 7);
        goldCrate.put(6, 0, 6, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(6, 0, 0, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(0, 0, 6, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(0, 0, 0, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(6, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        goldCrate.put(6, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        goldCrate.put(0, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        goldCrate.put(0, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        goldCrate.put(6, 2, 6, new StructureBlock(Material.STONE_SLAB2));
        goldCrate.put(6, 2, 0, new StructureBlock(Material.STONE_SLAB2));
        goldCrate.put(0, 2, 6, new StructureBlock(Material.STONE_SLAB2));
        goldCrate.put(0, 2, 0, new StructureBlock(Material.STONE_SLAB2));
        goldCrate.put(2, 0, 2, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(2, 0, 4, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(4, 0, 2, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(4, 0, 4, new StructureBlock(Material.GOLD_BLOCK));
        goldCrate.put(1, 0, 1, new StructureBlock(Material.STAINED_CLAY, (byte)4));
        goldCrate.put(1, 0, 3, new StructureBlock(Material.STAINED_CLAY, (byte)4));
        goldCrate.put(1, 0, 5, new StructureBlock(Material.STAINED_CLAY, (byte)4));
        goldCrate.put(3, 0, 1, new StructureBlock(Material.STAINED_CLAY, (byte)4));
        goldCrate.put(3, 0, 5, new StructureBlock(Material.STAINED_CLAY, (byte)4));
        goldCrate.put(5, 0, 1, new StructureBlock(Material.STAINED_CLAY, (byte)4));
        goldCrate.put(5, 0, 3, new StructureBlock(Material.STAINED_CLAY, (byte)4));
        goldCrate.put(5, 0, 5, new StructureBlock(Material.STAINED_CLAY, (byte)4));

        diamondCrate = new Structure(7, 4, 7);
        diamondCrate.put(6, 0, 6, new StructureBlock(Material.DIAMOND_BLOCK));
        diamondCrate.put(6, 0, 0, new StructureBlock(Material.DIAMOND_BLOCK));
        diamondCrate.put(0, 0, 6, new StructureBlock(Material.DIAMOND_BLOCK));
        diamondCrate.put(0, 0, 0, new StructureBlock(Material.DIAMOND_BLOCK));
        diamondCrate.put(6, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        diamondCrate.put(6, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        diamondCrate.put(0, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        diamondCrate.put(0, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        diamondCrate.put(6, 2, 6, new StructureBlock(Material.STAINED_CLAY, (byte)1));
        diamondCrate.put(6, 2, 0, new StructureBlock(Material.STAINED_CLAY, (byte)1));
        diamondCrate.put(0, 2, 6, new StructureBlock(Material.STAINED_CLAY, (byte)1));
        diamondCrate.put(0, 2, 0, new StructureBlock(Material.STAINED_CLAY, (byte)1));
        diamondCrate.put(6, 3, 6, new StructureBlock(Material.STONE_SLAB2));
        diamondCrate.put(6, 3, 0, new StructureBlock(Material.STONE_SLAB2));
        diamondCrate.put(0, 3, 6, new StructureBlock(Material.STONE_SLAB2));
        diamondCrate.put(0, 3, 0, new StructureBlock(Material.STONE_SLAB2));
        diamondCrate.put(0, 0, 3, new StructureBlock(Material.DOUBLE_STEP));
        diamondCrate.put(3, 0, 0, new StructureBlock(Material.DOUBLE_STEP));
        diamondCrate.put(3, 0, 6, new StructureBlock(Material.DOUBLE_STEP));
        diamondCrate.put(6, 0, 3, new StructureBlock(Material.DOUBLE_STEP));
        diamondCrate.put(0, 0, 1, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.EAST));
        diamondCrate.put(0, 0, 2, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.EAST));
        diamondCrate.put(0, 0, 4, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.EAST));
        diamondCrate.put(0, 0, 5, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.EAST));
        diamondCrate.put(6, 0, 1, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.WEST));
        diamondCrate.put(6, 0, 2, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.WEST));
        diamondCrate.put(6, 0, 4, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.WEST));
        diamondCrate.put(6, 0, 5, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.WEST));
        diamondCrate.put(1, 0, 0, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.SOUTH));
        diamondCrate.put(2, 0, 0, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.SOUTH));
        diamondCrate.put(4, 0, 0, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.SOUTH));
        diamondCrate.put(5, 0, 0, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.SOUTH));
        diamondCrate.put(1, 0, 6, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.NORTH));
        diamondCrate.put(2, 0, 6, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.NORTH));
        diamondCrate.put(4, 0, 6, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.NORTH));
        diamondCrate.put(5, 0, 6, new StructureStairs(Material.BIRCH_WOOD_STAIRS, BlockFace.NORTH));
        diamondCrate.put(1, 0, 2, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(5, 0, 2, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(2, 0, 1, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(4, 0, 1, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(1, 0, 4, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(2, 0, 5, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(4, 0, 5, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(5, 0, 4, new StructureBlock(Material.STAINED_CLAY, (byte)3));
        diamondCrate.put(3, 0, 3, new StructureBlock(Material.SEA_LANTERN));

        emeraldCrate = new Structure(7, 7, 7);

        //Bottom level
        emeraldCrate.put(6, 0, 6, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(6, 0, 0, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(0, 0, 6, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(0, 0, 0, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(2, 0, 2, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(4, 0, 4, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(2, 0, 4, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(4, 0, 2, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(0, 0, 3, new StructureBlock(Material.SEA_LANTERN));
        emeraldCrate.put(3, 0, 0, new StructureBlock(Material.SEA_LANTERN));
        emeraldCrate.put(3, 0, 6, new StructureBlock(Material.SEA_LANTERN));
        emeraldCrate.put(6, 0, 3, new StructureBlock(Material.SEA_LANTERN));
        emeraldCrate.put(2, 0, 3, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.WEST));
        emeraldCrate.put(3, 0, 2, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.NORTH));
        emeraldCrate.put(3, 0, 4, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.SOUTH));
        emeraldCrate.put(4, 0, 3, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.EAST));
        emeraldCrate.put(0, 0, 1, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.EAST));
        emeraldCrate.put(0, 0, 2, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.EAST));
        emeraldCrate.put(0, 0, 4, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.EAST));
        emeraldCrate.put(0, 0, 5, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.EAST));
        emeraldCrate.put(6, 0, 1, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.WEST));
        emeraldCrate.put(6, 0, 2, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.WEST));
        emeraldCrate.put(6, 0, 4, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.WEST));
        emeraldCrate.put(6, 0, 5, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.WEST));
        emeraldCrate.put(1, 0, 0, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.SOUTH));
        emeraldCrate.put(2, 0, 0, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.SOUTH));
        emeraldCrate.put(4, 0, 0, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.SOUTH));
        emeraldCrate.put(5, 0, 0, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.SOUTH));
        emeraldCrate.put(1, 0, 6, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.NORTH));
        emeraldCrate.put(2, 0, 6, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.NORTH));
        emeraldCrate.put(4, 0, 6, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.NORTH));
        emeraldCrate.put(5, 0, 6, new StructureStairs(Material.SANDSTONE_STAIRS, BlockFace.NORTH));
        emeraldCrate.put(3, 0, 3, new StructureBlock(Material.SEA_LANTERN));

        emeraldCrate.put(1, 0, 1, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(1, 0, 2, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(1, 0, 3, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(1, 0, 4, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(1, 0, 5, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(2, 0, 1, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(3, 0, 1, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(4, 0, 1, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(5, 0, 1, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(5, 0, 2, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(5, 0, 3, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(5, 0, 4, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(5, 0, 5, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(4, 0, 5, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(4, 0, 5, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(3, 0, 5, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(2, 0, 5, new StructureBlock(Material.PRISMARINE, (byte)2));
        emeraldCrate.put(1, 0, 5, new StructureBlock(Material.PRISMARINE, (byte)2));


        //Corners
        emeraldCrate.put(6, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(6, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(0, 1, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(0, 1, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(6, 2, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        emeraldCrate.put(6, 2, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        emeraldCrate.put(0, 2, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        emeraldCrate.put(0, 2, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)1));
        emeraldCrate.put(6, 3, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(6, 3, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(0, 3, 6, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(0, 3, 0, new StructureBlock(Material.RED_SANDSTONE, (byte)2));
        emeraldCrate.put(6, 4, 6, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(6, 4, 0, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(0, 4, 6, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(0, 4, 0, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(6, 5, 6, new StructureBlock(Material.STONE_SLAB2));
        emeraldCrate.put(6, 5, 0, new StructureBlock(Material.STONE_SLAB2));
        emeraldCrate.put(0, 5, 6, new StructureBlock(Material.STONE_SLAB2));
        emeraldCrate.put(0, 5, 0, new StructureBlock(Material.STONE_SLAB2));

        //Stair structure
        emeraldCrate.put(1, 3, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, true));
        emeraldCrate.put(1, 4, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, false));
        emeraldCrate.put(2, 4, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, true));
        emeraldCrate.put(2, 5, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, false));
        emeraldCrate.put(5, 3, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, true));
        emeraldCrate.put(5, 4, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, false));
        emeraldCrate.put(4, 4, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, true));
        emeraldCrate.put(4, 5, 0, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, false));
        emeraldCrate.put(3, 4, 0, new StructureBlock(Material.STONE_SLAB2));
        emeraldCrate.put(3, 5, 0, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(3, 6, 0, new StructureBlock(Material.STONE_SLAB2));

        emeraldCrate.put(1, 3, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, true));
        emeraldCrate.put(1, 4, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, false));
        emeraldCrate.put(2, 4, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, true));
        emeraldCrate.put(2, 5, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, false));
        emeraldCrate.put(5, 3, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, true));
        emeraldCrate.put(5, 4, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, false));
        emeraldCrate.put(4, 4, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.EAST, true));
        emeraldCrate.put(4, 5, 6, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.WEST, false));
        emeraldCrate.put(3, 4, 6, new StructureBlock(Material.STONE_SLAB2));
        emeraldCrate.put(3, 5, 6, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(3, 6, 6, new StructureBlock(Material.STONE_SLAB2));

        emeraldCrate.put(0, 3, 1, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, true));
        emeraldCrate.put(0, 4, 1, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, false));
        emeraldCrate.put(0, 4, 2, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, true));
        emeraldCrate.put(0, 5, 2, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, false));
        emeraldCrate.put(0, 3, 5, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, true));
        emeraldCrate.put(0, 4, 5, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, false));
        emeraldCrate.put(0, 4, 4, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, true));
        emeraldCrate.put(0, 5, 4, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, false));
        emeraldCrate.put(0, 4, 3, new StructureBlock(Material.STONE_SLAB2));
        emeraldCrate.put(0, 5, 3, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(0, 6, 3, new StructureBlock(Material.STONE_SLAB2));

        emeraldCrate.put(6, 3, 1, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, true));
        emeraldCrate.put(6, 4, 1, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, false));
        emeraldCrate.put(6, 4, 2, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, true));
        emeraldCrate.put(6, 5, 2, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, false));
        emeraldCrate.put(6, 3, 5, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, true));
        emeraldCrate.put(6, 4, 5, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, false));
        emeraldCrate.put(6, 4, 4, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.SOUTH, true));
        emeraldCrate.put(6, 5, 4, new StructureStairs(Material.RED_SANDSTONE_STAIRS, BlockFace.NORTH, false));
        emeraldCrate.put(6, 4, 3, new StructureBlock(Material.STONE_SLAB2));
        emeraldCrate.put(6, 5, 3, new StructureBlock(Material.EMERALD_BLOCK));
        emeraldCrate.put(6, 6, 3, new StructureBlock(Material.STONE_SLAB2));


    }

    public static Structure getDiamondCrate() {
        return diamondCrate;
    }

    public static Structure getEmeraldCrate() {
        return emeraldCrate;
    }

    public static Structure getGoldCrate() {
        return goldCrate;
    }

    public static Structure getIronCrate() {
        return ironCrate;
    }

    public static Structure getBaseCrate() {
        return baseCrate;
    }
}
