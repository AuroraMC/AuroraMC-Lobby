/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.ServerInfo;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.backend.GameServerInfo;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.gui.GameServerListing;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyAPI {

    private static LobbyMap map;
    private static AuroraMCLobby lobby;

    private static final GUIItem prefsItem;
    private static final GUIItem cosmeticsItem;
    private static final GUIItem gamesItem;
    private static final GUIItem lobbyItem;

    private static Map<String, String> versionNumbers;

    private static final Map<String, GameServerInfo> gameServers;


    static {
        lobbyItem = new GUIItem(Material.NETHER_STAR, "&a&lSwitch Lobbies");
        gamesItem = new GUIItem(Material.COMPASS, "&a&lBrowse Games");
        prefsItem = new GUIItem(Material.REDSTONE_COMPARATOR, "&a&lView Preferences");
        cosmeticsItem = new GUIItem(Material.EMERALD, "&a&lView Cosmetics");
        gameServers = new HashMap<>();
    }


    public static void setLobbyMap(LobbyMap map) {
        LobbyAPI.map = map;
    }

    public static void init(AuroraMCLobby lobby) {
        LobbyAPI.lobby = lobby;
    }

    public static String getVersionNumber(String game) {
        return versionNumbers.get(game);
    }

    public static void loadVersionNumbers() {
        versionNumbers = LobbyDatabaseManager.getVersionNumbers();
    }

    public static void loadGameServers() {
        List<ServerInfo> infos = LobbyDatabaseManager.getServers();
        assert infos != null;
        for (ServerInfo info : infos) {
            gameServers.put(info.getName(), new GameServerInfo(info));
        }
    }

     public static void addGameServer(String serverName) {
        gameServers.put(serverName, new GameServerInfo(AuroraMCAPI.getDbManager().getServerDetailsByName(serverName, AuroraMCAPI.getServerInfo().getNetwork().name())));
     }

     public static void removeGameServer(String serverName) {
        gameServers.remove(serverName);
     }

    public static LobbyMap getLobbyMap() {
        return map;
    }

    public static AuroraMCLobby getLobby() {
        return lobby;
    }

    public static GUIItem getPrefsItem() {
        return prefsItem;
    }

    public static GUIItem getLobbyItem() {
        return lobbyItem;
    }

    public static GUIItem getCosmeticsItem() {
        return cosmeticsItem;
    }

    public static GUIItem getGamesItem() {
        return gamesItem;
    }

    public static GUIItem getStatsItem(String playerName) {
        return new GUIItem(Material.SKULL_ITEM, "&a&lView Statistics", 1, null, (short)3, false, playerName);
    }


    public static GameServerInfo getGameServer(String server) {
        return gameServers.get(server);
    }

    public static Map<String, GameServerInfo> getGameServers() {
        return gameServers;
    }
}
