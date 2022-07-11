/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.ServerInfo;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.backend.GameServerInfo;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.Changelog;
import net.auroramc.lobby.api.util.CommunityPoll;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LobbyAPI {

    private static final String MONKEY_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY0ODY2OTgxMzA0NSwKICAicHJvZmlsZUlkIiA6ICIyZjZlMTAxNTUyZmM0Zjg1OTEwODJjNWY0ZmRlMWFjNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJMb29maWkiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjliZjMyMzAxNDAyMjFkYjVkYjcyNjgwMjcyOTVhZDE3ZTkxMWE4NzFhMDVhN2QwMWIwYTVhMzdmNDY5MmQwOCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9";
    private static final String MONKEY_SIGNATURE = "r59qGCO6MS9xU2fMYK7TZoN8Lli1yj5Lf+ag+i5QlNg8/1htjeokhJUOZzNCgdxAuHfwihnbBaAKMfGgZmzn0QVM5kxY5/sYWWZ23mcATY2kbu1t+wuEFAlvmIRB+Ea3CcECKIr4Z/0XOM1qEHY3fKQFYvIM+06TUYz5yKCxSApaIQ1zPFZCgpdgaGbj8PBjlNI+fIShETN7cDS4LkT8xbLxkdOwKuM2rQGRE1ojGBpfc9ekh+r2tJP5EvrTVma9LpVKZHCvsxUjtcnf8FgCaorljjpPGHcxuFajT805YOp3IIuHgVNlZS23zzaPDqG7qhnwjPcHdkFtHXC+Xh57wYOFBcd/B65Xn8j0rA9kcA1eSr0Q67Hp9LH8niioD3TXnlmNWbQ/yCBlL5OYHfx6kbfxp3kNrYDmuCOzINXxICV0ytaQUzSyOTQqEApdevu32pIMyR3doXFSdkK44kpGd00ZidHME7F1Y4LLK2UYuZ/Pod4+lzqlyV6xGd/GTeLIfZcryubiJmTU6t0U8vwJ8ZZWdYCFz8m2PKUQLPAQkeVC/qGP1t/BltAMwhqHdDIDBFPcA5cVFulwF6LcoBMjwqcggy3mUuELubl5lkfy02lzVu+zZJ/KZOTbeM7HOESoSiqxk2Ewmt/qstovT3UZrMnd4+XhnHKY/VO5sKEYLXI=";

    private static final String CQ_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDA0OTk1NTY3MiwKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YTY1YTEzMGRiYTJmMmVhZWI0MzM0NWQ3ZGNkMWUzNGI5YjAwZGQzNGY2ZWMwNThkYzNjZWZkMDNiNzAxMjFlIgogICAgfQogIH0KfQ==";
    private static final String CQ_SIGNATURE = "mt5PsPucovhZRN8+k8mcu2jHtrSTOSY736B0A3Z6JJvj2760mXTmXLR0V14TN+aWicXftaokslG/iOCLNbWukSfNYbrKS+DR55f3SHOUmast4eQ6lz9JP4yrLv7ceVyG/r9SQkXzrpkG+vSsotvG8krqN/Eh1VgXU8O3xPa4PsDs3EUj/dexsfHumlQrgMEkjE4K6xcBnYCm5AhOo293bJTq3/B33/5IGepz6EwRHhRiVghcacE5XL/Cx3EEShyTIMRYG++ixfuhF+iCPadP4LjaSWpK2oFJ2u57ZotsjqeDo9nKqg76frdFwP4psv8s+sHjw7BFwBX7dl8a9yOTHYPlcSw1Ocr8P54mfCo7FFyEQhjxmcH66/elu3LVdmqed5dg9vA6wWgMxlfIutvb3pkPQZGwdTeuG5uYUPBT93adgs7xi9i48YqizcUyO9/5FcPP7B0RFEPQ5FLh06Jnb3xsU7VHumL4UaXshwH73uRW4wYykMhH/xXHz84UMlz3iQL342bwjPCsi9w7MqxnAhE/Us0uZFFUh2kgXDPPItl9CKZo57NDmh9m79nXQNX/ETdxxm2YfQ2NryYuqs5K9TsdTPfGQVW+M08Akb+zZaHCmo0MBS4B9Kdr4f7n+1UigMrUJu6jUmSEd1qayAv5o26VU6mjLhMg/JLuQRWNFNE=";

    private static final String ARCADE_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDA1MDYxMDI4NywKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZjkzYTEzZWRhMjA0ZGZjN2FmOTY0NzU3N2M5YzllNjQ4MWExOTlmMGRjMTQ1ODU0NmRiNmJiNjZlYjljOTllIgogICAgfQogIH0KfQ==";
    private static final String ARCADE_SIGNATURE = "JHLqvfIuIjU+kLjBcqlqOEwI+9THFklCmg1Oum14qKKRRDM+dlv4Ee9oWYibD9BJvEVFfQxKWSBKu2JQlYlpyZMZr60CnjREZhWrK6GdD5ZAPfLbDul6dwpkb8MAawvNleAbtb5OwFy9Jz5E/XSNojffRXkQkt/mqiI6MYiBQgk+ldKVWs3tqftVYe+x9vRGTf7PW/3gh4fmO0IZ9oUF/IZinQd/cX0j+7/KJ3/E8mEg6gnIDo2tVZ2aitRlnDoiHKgEFnmfUDgpQbv8EN8ffDbZCXEQoLm0A/xPx+q+bnnW7AoW0eTGDzsvkfDGo9CFP1Y8vV1bd2/VFpprUp2kEuWgGiQ9TUKs5b8Vu/lDMXswrhz0xl/6UqEqw2Vx0v8ZkbfohYpWB9uhPpH6BVc3ap1fNUpWIS/xyO/NyGMYuyZHAvaHCM/Lvs39iNW5YdhsNgzU0FnEa7hjMvg1sqN0sKYO8nSEAFIsNGVA8XYAz+a9VjKHwE974jjBam5iW3daHHIgKfSHkqfIPDPZiCFjk229qjRw5gYCatR7iXFNmrlIAEv3ntINrUtDus69MW8xL8YlEcS8NRnL7OPc7ZuI+0sogqqDgSNr96CFHxPhFqbIDjYoQRxW3RpMDoa6FNuwzAw3q9xpQfYnc1g8cJIRCS3zrZ8a8Cemwv67zeMKzAU=";

    private static final String PAINTBALL_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDExOTc2NTUxNywKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lMjBlYmM4NTJhNGQxYzZhNzM2ODdjODM2OTVmYzE4YmU4NTVjNjI3NzIyYzJhYzQxOWYxMWUxZjAwN2QwNDhlIgogICAgfQogIH0KfQ==";
    private static final String PAINTBALL_SIGNATURE = "rmVcbIioK1+kpxwtrlDyeZ5ZQvlvI6CyKGsTuX3vCfIePH90EZS9moGA2y1fFdLjcusJ6fBM8yVcqda8+rDX/Et0rVFa53Z9uYLmnaUdLZkbbbFFeX54if6Gy+MvSWe5L9dnATzIheUNntxfRE4z9mAU2dmAr9gbiz1cd92uB4VUF2QwS7M5mAf4dP/svGXnRng9LmV1FCGR29tV2186SQSrTi33mugEVXDPO3N4Kw0XI90DOKINvjP29UjXuR9JEzW+IbJNxKWz0xJmMBWZUx+LNpdkNpEbdFyB0iD12Yx87+tXKOl4+JjkVOR4W3sFoO220/HUPGCP9x5lEf4iZgOcLeguKIjua9/0t6yW0kc5ns5Wj+2beTVFNeb6y8BqsLjuOV6NtBUuh7PPAB1K4mV18/t9qxdUpkFYurFYJzUo7LfJQQSeAncRPzD0CwQZrIiCnEbDkCVCDIqe3amFKELugOklf2TAkOMNvu56YinkmTAb9REE2PSAyTGPR9wVp8v+xLCQ7b7vhbuxjU/ezHzrgib6BJt1+6rjSMOxb3A4cu8GhRtfxkIIzvBprBIuf8brGjG2DsbacZgLxb46BrkZUCLSWVOBJeJsNPcz5lMQUpHyfXOLi4BVZ2Z8WbAirMU2RQO4UxQFP8huX61KtA8QjMWjZTbRS77hhaIiQSg=";

    private static final String DUELS_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDA1MDgwMTU5NCwKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lOWQ0YTZhNDIzMGE5MDkxZDQwNTM5ZDhiOWVjNzZiMjhkMjMwMTZkYmM1MGRiMTk3OTgyZDg5NDY1YzQ3NDEiCiAgICB9CiAgfQp9";
    private static final String DUELS_SIGNATURE = "cmYL17h7mXEQkI/lb/YIktvLUu2+fWIJIf56cC56R7Gh6l8gbLq75B9Aokq9K6DCfYAUtalkyODW4FTdRXSh05Fh3oUFQYz6InPP7LJvl1IprhGX9vPlZJfRfTKo6D6ZT/jPOtLebCu7F6TQlT++5LmFa0X4wFiG9DZMBGSy6BMfzxubAEVVO12CMPCT2FTtLFidRwUewGqtmB2ticOfFJ/DWT2udeomSMjO48+nwfq5aaXYH+cNeswUuPLXrNdmYTTKSyhHPSX0Ws9ScmpVIYFY7QOC+q6sRIEisuE7oHSDBS1fmoXFohFcIMRuOyhX0LHWewhVymS9OjlsrQwhKF6yGKmESl78y1YoO4H2XxLKvdOnKSd2iaUC5KXlNBV5+Lg04c/gJBi2ZLA77vyZaQnOU+AO9ldMvY+PFN4L85XioRv1lY8kC2vxzfjy5FAWAnmSXa/SIw4fdREaUopiGCqYUJug5JAUhKStP79rlVrrGRqM7Xq/f1aysQZJrloB0iBrC6E72Eb7uEMnroTB1ejE3Kl1mF6mTlrtMSeS0DNR7VRX5IjeIwP1mvNQOZoFEODw17IqwWuhyg1qZvDF13PaW1Ik6k8Wfx7i+YOPot+ILQy0/JObixZnON6V7zb+HMqp3KzAyr0zIroPyHBrpSmhHFejxGxUiLJUOQCiFeQ=";

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
    private static EntityPlayer duelsEntity;

    private static Block chestBlock;
    private static ArmorStand chestStand;

    private static Crate currentCrate;
    private static AuroraMCLobbyPlayer cratePlayer;
    private static boolean crateAnimationFinished;


    static {
        lobbyItem = new GUIItem(Material.NETHER_STAR, "&a&lSwitch Lobbies");
        gamesItem = new GUIItem(Material.COMPASS, "&a&lBrowse Games");
        prefsItem = new GUIItem(Material.REDSTONE_COMPARATOR, "&a&lView Preferences");
        cosmeticsItem = new GUIItem(Material.EMERALD, "&a&lView Cosmetics");
        gameServers = new HashMap<>();

        currentCrate = null;
        cratePlayer = null;
        crateAnimationFinished = true;
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
        monkeyEntity.setLocation(4.5, 71.0, 0.5, -145.0f, 0f);
        AuroraMCAPI.registerFakePlayer(monkeyEntity);


        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("Arcade Mode§r "));
        profile.getProperties().put("textures", new Property("textures", ARCADE_SKIN, ARCADE_SIGNATURE));
        arcadeEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        arcadeEntity.setLocation(-9.5, 64.0, 11.5, -145f, 0f);
        AuroraMCAPI.registerFakePlayer(arcadeEntity);

        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("Paintball§r "));
        profile.getProperties().put("textures", new Property("textures", PAINTBALL_SKIN, PAINTBALL_SIGNATURE));
        paintballEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        paintballEntity.setLocation(-13.5, 64.0, 8.5, -145f, 0f);
        AuroraMCAPI.registerFakePlayer(paintballEntity);

        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("Crystal Quest "));
        profile.getProperties().put("textures", new Property("textures", CQ_SKIN, CQ_SIGNATURE));
        cqEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        cqEntity.setLocation(-5.5, 64.0, 6.5, -145f, 0f);
        AuroraMCAPI.registerFakePlayer(cqEntity);

        profile = new GameProfile(UUID.randomUUID(), AuroraMCAPI.getFormatter().convert("Duels§r "));
        profile.getProperties().put("textures", new Property("textures", DUELS_SKIN, DUELS_SIGNATURE));
        duelsEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        duelsEntity.setLocation(-5.5, 64.0, 14.5, -145f, 0f);
        AuroraMCAPI.registerFakePlayer(duelsEntity);
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

    public static EntityPlayer getDuelsEntity() {
        return duelsEntity;
    }

    public static EntityPlayer getCqEntity() {
        return cqEntity;
    }

    public static EntityPlayer getPaintballEntity() {
        return paintballEntity;
    }

    public static Block getChestBlock() {
        return chestBlock;
    }

    public static void setChestBlock(Block chestBlock) {
        LobbyAPI.chestBlock = chestBlock;
    }

    public static ArmorStand getChestStand() {
        return chestStand;
    }

    public static void setChestStand(ArmorStand chestStand) {
        LobbyAPI.chestStand = chestStand;
    }

    public synchronized static boolean startOpen(Crate crate, AuroraMCLobbyPlayer player) {
        if (currentCrate != null || cratePlayer != null) {
            return false;
        }
        LobbyAPI.cratePlayer = player;
        LobbyAPI.currentCrate = crate;
        crateAnimationFinished = false;
        return true;
    }

    public static void crateAnimationFinished() {
        crateAnimationFinished = true;
    }

    public static void finishOpen() {
        currentCrate = null;
        cratePlayer = null;
    }

    public static AuroraMCLobbyPlayer getCratePlayer() {
        return cratePlayer;
    }

    public static Crate getCurrentCrate() {
        return currentCrate;
    }

    public static boolean isCrateAnimationFinished() {
        return crateAnimationFinished;
    }
}
