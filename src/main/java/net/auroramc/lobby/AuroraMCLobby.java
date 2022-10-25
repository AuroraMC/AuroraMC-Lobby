/*
 * Copyright (c) 2021 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby;

import net.auroramc.core.api.utils.ZipUtil;
import net.auroramc.core.api.utils.holograms.Hologram;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.LobbyMap;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.util.UpdateDataRunnable;
import net.auroramc.lobby.api.util.UpdatePollRunnable;
import net.auroramc.lobby.api.util.UpdateScoreboardRunnable;
import net.auroramc.lobby.api.util.UpdateServersRunnable;
import net.auroramc.lobby.commands.CommandFly;
import net.auroramc.lobby.listeners.*;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.lobby.commands.admin.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AuroraMCLobby extends JavaPlugin {

    @Override
    public void onEnable() {


        LobbyAPI.init(this);

        AuroraMCAPI.registerCommand(new CommandEffect());
        AuroraMCAPI.registerCommand(new CommandGameMode());
        AuroraMCAPI.registerCommand(new CommandGive());
        AuroraMCAPI.registerCommand(new CommandMob());
        AuroraMCAPI.registerCommand(new CommandTeleport());
        AuroraMCAPI.registerCommand(new CommandVersion());
        AuroraMCAPI.registerCommand(new CommandFly());
        AuroraMCAPI.registerCommand(new CommandCrate());

        LobbyDatabaseManager.downloadMap();
        File mapFolder = new File(Bukkit.getWorldContainer(), "world");
        if (mapFolder.exists()) {
            try {
                FileUtils.deleteDirectory(mapFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapFolder.mkdirs();
        File region = new File(mapFolder, "region");
        region.mkdirs();
        try {
            ZipUtil.unzip(getDataFolder().toPath().toAbsolutePath() + "/zip/54.zip", region.toPath().toAbsolutePath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        File data = new File(region, "map.json");
        JSONParser parser = new JSONParser();
        Object object;
        JSONObject jsonObject;
        try {
            FileReader fileReader = new FileReader(data);
            object = parser.parse(fileReader);
            jsonObject = new JSONObject(((org.json.simple.JSONObject) object).toJSONString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            getLogger().info("Map loading for a map failed, skipping...");
            return;
        }

        int id = 22;
        String name = jsonObject.getString("name");
        String author = jsonObject.getString("author");

        LobbyAPI.setLobbyMap(new LobbyMap(id, name, author, jsonObject));
        LobbyAPI.loadVersionNumbers();
        LobbyAPI.loadGameServers();

        Bukkit.getPluginManager().registerEvents(new ShutdownEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProtocolListener(), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new FakePlayerListener(), this);

        new UpdateServersRunnable().runTaskTimer(AuroraMCAPI.getCore(), 20, 100);
        new UpdateDataRunnable().runTaskTimer(AuroraMCAPI.getCore(), 0, 20);
        new UpdatePollRunnable().runTaskTimer(AuroraMCAPI.getCore(), 36400, 36400);
        new UpdateScoreboardRunnable().runTaskTimer(AuroraMCAPI.getCore(), 400, 400);

        LobbyAPI.loadParkours();
    }
}
