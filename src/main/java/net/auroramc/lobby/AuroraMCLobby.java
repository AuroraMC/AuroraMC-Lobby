/*
 * Copyright (c) 2021 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.lobby.commands.admin.CommandEffect;
import net.auroramc.lobby.commands.admin.CommandGameMode;
import net.auroramc.lobby.commands.admin.CommandGive;
import net.auroramc.lobby.commands.admin.CommandMob;
import net.auroramc.lobby.listeners.ShutdownEventListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AuroraMCLobby extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new ShutdownEventListener(), this);

        AuroraMCAPI.registerCommand(new CommandEffect());
        AuroraMCAPI.registerCommand(new CommandGameMode());
        AuroraMCAPI.registerCommand(new CommandGive());
        AuroraMCAPI.registerCommand(new CommandMob());
    }

}
