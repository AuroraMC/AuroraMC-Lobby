/*
 * Copyright (c) 2021-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.utils.ZipUtil;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.LobbyMap;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.util.UpdateDataRunnable;
import net.auroramc.lobby.api.util.UpdatePollRunnable;
import net.auroramc.lobby.api.util.UpdateScoreboardRunnable;
import net.auroramc.lobby.api.util.UpdateServersRunnable;
import net.auroramc.lobby.commands.CommandFly;
import net.auroramc.lobby.commands.event.CommandCreateEvent;
import net.auroramc.lobby.listeners.*;
import net.auroramc.lobby.commands.admin.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.logging.Level;

public class AuroraMCLobby extends JavaPlugin {

    private static FileConfiguration maps;
    private static File mapsFile;

    @Override
    public void onEnable() {
        mapsFile = new File(getDataFolder(), "maps.yml");
        if (!mapsFile.exists()) {
            mapsFile.getParentFile().mkdirs();
            copy(getResource("maps.yml"), mapsFile);
        }

        maps = new YamlConfiguration();
        try {
            maps.load(mapsFile);
        } catch (Exception e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
        }
        maps.options().copyHeader(true);

        LobbyAPI.init(this);

        AuroraMCAPI.registerCommand(new CommandEffect());
        AuroraMCAPI.registerCommand(new CommandGameMode());
        AuroraMCAPI.registerCommand(new CommandGive());
        AuroraMCAPI.registerCommand(new CommandMob());
        AuroraMCAPI.registerCommand(new CommandTeleport());
        AuroraMCAPI.registerCommand(new CommandVersion());
        AuroraMCAPI.registerCommand(new CommandFly());
        AuroraMCAPI.registerCommand(new CommandCrate());
        AuroraMCAPI.registerCommand(new CommandCreateEvent());



        LobbyDatabaseManager.downloadMap();

        getLogger().info("Map downloaded, deleting world directory...");
        File mapFolder = new File(Bukkit.getWorldContainer(), "world");
        if (mapFolder.exists()) {
            try {
                FileUtils.deleteDirectory(mapFolder);
            } catch (IOException e) {
                AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
            }
        }
        mapFolder.mkdirs();
        File region = new File(mapFolder, "region");
        region.mkdirs();
        try {
            getLogger().info("Unzipping map...");
            ZipUtil.unzip(getDataFolder().toPath().toAbsolutePath() + "/zip/54.zip", region.toPath().toAbsolutePath().toString());
        } catch (IOException e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
        }

        getLogger().info("Loading map data into memory...");
        File data = new File(Bukkit.getWorldContainer(), "world/region/map.json");
        JSONParser parser = new JSONParser();
        Object object;
        JSONObject jsonObject;
        try {
            FileReader fileReader = new FileReader(data);
            object = parser.parse(fileReader);
            jsonObject = new JSONObject(((org.json.simple.JSONObject) object).toJSONString());
        } catch (IOException | ParseException e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
            getLogger().info("Map loading for a map failed, skipping...");
            return;
        }

        int id = 54;
        String name = jsonObject.getString("name");
        String author = jsonObject.getString("author");
        String game = jsonObject.getString("game_type");


        LobbyAPI.setLobbyMap(new LobbyMap(id, name, author, game, jsonObject));

        getLogger().info("Map loaded, loading other info...");
        LobbyAPI.loadVersionNumbers();
        LobbyAPI.loadGameServers();

        getLogger().info("Registering listeners...");
        Bukkit.getPluginManager().registerEvents(new ShutdownEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new LobbyListener(), this);
        Bukkit.getPluginManager().registerEvents(new ProtocolListener(), this);
        Bukkit.getPluginManager().registerEvents(new LeaveListener(), this);
        Bukkit.getPluginManager().registerEvents(new FakePlayerListener(), this);

        new UpdateServersRunnable().runTaskTimer(ServerAPI.getCore(), 20, 100);
        new UpdateDataRunnable().runTaskTimerAsynchronously(ServerAPI.getCore(), 0, 20);
        new UpdatePollRunnable().runTaskTimerAsynchronously(ServerAPI.getCore(), 36400, 36400);
        new UpdateScoreboardRunnable().runTaskTimer(ServerAPI.getCore(), 400, 400);
        new BukkitRunnable(){
            @Override
            public void run() {
                LobbyAPI.getEasy().getLeaderboard().refresh();
                LobbyAPI.getMedium().getLeaderboard().refresh();
                LobbyAPI.getHard().getLeaderboard().refresh();
            }
        }.runTaskTimerAsynchronously(LobbyAPI.getLobby(), 1200, 1200);

        new BukkitRunnable(){
            @Override
            public void run() {
                TextComponent textComponent = new TextComponent("");

                TextComponent lines = new TextComponent("-----------------------------------------------------");
                lines.setStrikethrough(true);
                lines.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
                textComponent.addExtra(lines);

                textComponent.addExtra("\n");

                TextComponent enjoy = new TextComponent("Join our Discord to be a part of the AuroraMC Community!");
                enjoy.setBold(true);
                enjoy.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                textComponent.addExtra(enjoy);

                textComponent.addExtra("\n \n");

                TextComponent purchase = new TextComponent("Our Discord is the one-stop-shop for everything in the AuroraMC community! Talk with other players, play games, and much more! If you have a rank, you even get cool Discord-exclusive perks! Join now at ");
                textComponent.addExtra(purchase);

                TextComponent store = new TextComponent("discord.auroramc.net");
                store.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join our Discord community!").color(net.md_5.bungee.api.ChatColor.GREEN).create()));
                store.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.auroramc.net"));
                store.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                textComponent.addExtra(store);

                TextComponent link = new TextComponent("!\n" +
                        " \n" +
                        "Link your Discord account to remove this message.\n");
                textComponent.addExtra(link);
                textComponent.addExtra("\n");
                textComponent.addExtra(lines);
                for (AuroraMCServerPlayer player : ServerAPI.getPlayers()) {
                    if (player.getLinkedDiscord() == null) {
                        player.sendMessage(textComponent);
                    }
                }
            }
        }.runTaskTimerAsynchronously(LobbyAPI.getLobby(), 36000, 36000);
        getLogger().info("Startup complete.");
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
        }
    }

    public static FileConfiguration getMaps() {
        return maps;
    }

    public static File getMapsFile() {
        return mapsFile;
    }
}
