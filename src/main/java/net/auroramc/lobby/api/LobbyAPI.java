/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.ServerInfo;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.backend.GameServerInfo;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.util.Changelog;
import net.auroramc.lobby.api.util.CommunityPoll;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LobbyAPI {

    private static final String MONKEY_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY0ODY2OTgxMzA0NSwKICAicHJvZmlsZUlkIiA6ICIyZjZlMTAxNTUyZmM0Zjg1OTEwODJjNWY0ZmRlMWFjNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJMb29maWkiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjliZjMyMzAxNDAyMjFkYjVkYjcyNjgwMjcyOTVhZDE3ZTkxMWE4NzFhMDVhN2QwMWIwYTVhMzdmNDY5MmQwOCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    private static final String MONKEY_SIGNATURE = "r59qGCO6MS9xU2fMYK7TZoN8Lli1yj5Lf+ag+i5QlNg8/1htjeokhJUOZzNCgdxAuHfwihnbBaAKMfGgZmzn0QVM5kxY5/sYWWZ23mcATY2kbu1t+wuEFAlvmIRB+Ea3CcECKIr4Z/0XOM1qEHY3fKQFYvIM+06TUYz5yKCxSApaIQ1zPFZCgpdgaGbj8PBjlNI+fIShETN7cDS4LkT8xbLxkdOwKuM2rQGRE1ojGBpfc9ekh+r2tJP5EvrTVma9LpVKZHCvsxUjtcnf8FgCaorljjpPGHcxuFajT805YOp3IIuHgVNlZS23zzaPDqG7qhnwjPcHdkFtHXC+Xh57wYOFBcd/B65Xn8j0rA9kcA1eSr0Q67Hp9LH8niioD3TXnlmNWbQ/yCBlL5OYHfx6kbfxp3kNrYDmuCOzINXxICV0ytaQUzSyOTQqEApdevu32pIMyR3doXFSdkK44kpGd00ZidHME7F1Y4LLK2UYuZ/Pod4+lzqlyV6xGd/GTeLIfZcryubiJmTU6t0U8vwJ8ZZWdYCFz8m2PKUQLPAQkeVC/qGP1t/BltAMwhqHdDIDBFPcA5cVFulwF6LcoBMjwqcggy3mUuELubl5lkfy02lzVu+zZJ/KZOTbeM7HOESoSiqxk2Ewmt/qstovT3UZrMnd4+XhnHKY/VO5sKEYLXI=";

    private static LobbyMap map;
    private static AuroraMCLobby lobby;

    private static final GUIItem prefsItem;
    private static final GUIItem cosmeticsItem;
    private static final GUIItem gamesItem;
    private static final GUIItem lobbyItem;

    private static Map<String, String> versionNumbers;
    private static Map<String, List<Changelog>> changelogs;
    private static Changelog latestChangelog;
    private static CommunityPoll poll;

    private static final Map<String, GameServerInfo> gameServers;

    private static EntityPlayer monkeyEntity;
    private static EntityPlayer arcadeEntity;
    private static EntityPlayer cqEntity;
    private static EntityPlayer paintballEntity;
    private static EntityPlayer backstabEntity;


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
        changelogs = LobbyDatabaseManager.getChangelogs();
        latestChangelog = LobbyDatabaseManager.getLatestChangelog();
        poll = LobbyDatabaseManager.getPoll();
    }

    public static void checkForPoll() {
        poll = LobbyDatabaseManager.getPoll();
        changelogs = LobbyDatabaseManager.getChangelogs();
        latestChangelog = LobbyDatabaseManager.getLatestChangelog();
    }

    public static void loadGameServers() {
        List<ServerInfo> infos = LobbyDatabaseManager.getServers();
        assert infos != null;
        for (ServerInfo info : infos) {
            gameServers.put(info.getName(), new GameServerInfo(info));
        }
    }

    public static void spawnEntities() {
        GameProfile profile;
        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("&3&lThe Monke"));
        profile.getProperties().put("textures", new Property("textures", MONKEY_SKIN, MONKEY_SIGNATURE));
        monkeyEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        monkeyEntity.setLocation(-11.5, 61.0, 14.5, -145.0f, 0f);
        AuroraMCAPI.registerFakePlayer(monkeyEntity);


        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("&e&lArcade Mode"));
        profile.getProperties().put("textures", new Property("textures", MONKEY_SKIN, MONKEY_SIGNATURE));
        arcadeEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        arcadeEntity.setLocation(-5, 61.0, 2.5, -120f, 0f);
        AuroraMCAPI.registerFakePlayer(arcadeEntity);

        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("&a&lPaintball"));
        profile.getProperties().put("textures", new Property("textures", MONKEY_SKIN, MONKEY_SIGNATURE));
        paintballEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        paintballEntity.setLocation(9, 61.0, 11, 89f, 0f);
        AuroraMCAPI.registerFakePlayer(paintballEntity);

        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("&bCrystal Quest"));
        profile.getProperties().put("textures", new Property("textures", MONKEY_SKIN, MONKEY_SIGNATURE));
        cqEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        cqEntity.setLocation(0, 61.0, 7, 180f, 0f);
        AuroraMCAPI.registerFakePlayer(cqEntity);

        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("&c&lBackstab"));
        profile.getProperties().put("textures", new Property("textures", MONKEY_SKIN, MONKEY_SIGNATURE));
        backstabEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        backstabEntity.setLocation(5, 61.0, 5, 136f, 0f);
        AuroraMCAPI.registerFakePlayer(backstabEntity);
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

    public static EntityPlayer getMonkeyEntity() {
        return monkeyEntity;
    }

    public static GameServerInfo getGameServer(String server) {
        return gameServers.get(server);
    }

    public static Map<String, GameServerInfo> getGameServers() {
        return gameServers;
    }

    public static Map<String, List<Changelog>> getChangelogs() {
        return changelogs;
    }

    public static Changelog getLatestChangelog() {
        return latestChangelog;
    }

    public static Changelog getLatestChangelog(String game) {
        if (!changelogs.containsKey(game)) {
            return null;
        }
        Changelog latest = null;
        for (Changelog changelog : changelogs.get(game)) {
            if (latest == null) {
                latest = changelog;
            } else {
                if (changelog.getTimestamp() > latest.getTimestamp()) {
                    latest = changelog;
                }
            }
        }
        return latest;
    }

    public static CommunityPoll getPoll() {
        return poll;
    }

    public static EntityPlayer getArcadeEntity() {
        return arcadeEntity;
    }

    public static EntityPlayer getBackstabEntity() {
        return backstabEntity;
    }

    public static EntityPlayer getCqEntity() {
        return cqEntity;
    }

    public static EntityPlayer getPaintballEntity() {
        return paintballEntity;
    }
}
