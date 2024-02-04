/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.utils;

import net.auroramc.common.cosmetics.crates.DiamondCrate;
import net.auroramc.common.cosmetics.crates.EmeraldCrate;
import net.auroramc.common.cosmetics.crates.GoldCrate;
import net.auroramc.common.cosmetics.crates.IronCrate;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CrateUtil {

    public static final long IRON_CRATE_PRICE = 5000;
    public static final long GOLD_CRATE_PRICE = 15000;
    public static final long DIAMOND_CRATE_PRICE = 30000;


    public static IronCrate generateIronCrate(int owner) {
        UUID uuid = UUID.randomUUID();
        IronCrate crate = new IronCrate(uuid, owner, null, System.currentTimeMillis(), -1);
        new BukkitRunnable() {
            @Override
            public void run() {
                LobbyDatabaseManager.newCrate(crate);
            }
        }.runTaskAsynchronously(ServerAPI.getCore());
        return crate;
    }
    public static GoldCrate generateGoldCrate(int owner) {
        UUID uuid = UUID.randomUUID();
        GoldCrate crate = new GoldCrate(uuid, owner, null, System.currentTimeMillis(), -1);
        new BukkitRunnable() {
            @Override
            public void run() {
                LobbyDatabaseManager.newCrate(crate);
            }
        }.runTaskAsynchronously(ServerAPI.getCore());
        return crate;
    }

    public static DiamondCrate generateDiamondCrate(int owner) {
        UUID uuid = UUID.randomUUID();
        DiamondCrate crate = new DiamondCrate(uuid, owner, null, System.currentTimeMillis(), -1);
        new BukkitRunnable() {
            @Override
            public void run() {
                LobbyDatabaseManager.newCrate(crate);
            }
        }.runTaskAsynchronously(ServerAPI.getCore());
        return crate;
    }

    public static EmeraldCrate generateEmeraldCrate(int owner) {
        UUID uuid = UUID.randomUUID();
        EmeraldCrate crate = new EmeraldCrate(uuid, owner, null, System.currentTimeMillis(), -1);
        new BukkitRunnable() {
            @Override
            public void run() {
                LobbyDatabaseManager.newCrate(crate);
            }
        }.runTaskAsynchronously(ServerAPI.getCore());
        return crate;
    }

}
