/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.backend.info.ServerInfo;
import net.auroramc.api.cosmetics.Crate;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.api.utils.holograms.Hologram;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.parkour.Parkour;
import net.auroramc.lobby.api.parkour.Reward;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.Changelog;
import net.auroramc.lobby.api.util.CommunityPoll;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyAPI {

    private static final String LUNA_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY3MjYwMjQ0NTAzOCwKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NDYyMDkzMWViMDg5ZWRkY2UyNzkzNmNlMTIyYTQ2ODM3ZTFmYmU2NjA4YTYzMmQ5Njk2NGI3NzQyMGUzYWIiCiAgICB9CiAgfQp9";
    private static final String LUNA_SIGNATURE = "k0+/l28ge5BpqrSURb9BZ/WGTie+hIqezCptb2cUXdU9/72VS5UF3Y1q1q2E0xqhMah++SvScyXIfjmneg9j5Dzivbc2ksTJ6llmNLgi+E/0WjpXH4mjuXSH1n3C9sMsKByJZ0z+c/tZeGdLWFdCEEwVLKQCBGNyVskTp4wDNVS3iLOuMVbX0hDkXkRxCACVXmflG5qCKHN5pzjgfToJKBZGrv9bkK9ypzjocHBEb0jZYBaGnddENb4msaOA2iSVSaxD0IYjTggjo079p9SyrWZISW070+gImHXCJHi4BO6S6mzUQik5ySp3wY14Dc4jP+FZDB83LwGSF20Eyl+ib3pP3Zx5VyQdsZjxdoWhmicauV3Uxr30HHrmcKhBCFywrIQcw/tyNvlN9Zoq7KU2JVA8E2Y49fMhg3heDjr5XCyMu1Mjnn5BzVk7McbK1DFjCBH5meWiM+xAKPlNRgAY1/kkjtHME8I4gn3QQMapxsctRXMMdMCUBtDtnMuNun8NIk9mnnEpnWYvjCIiFmAT4ndHSx9X7+EHpH/bCZJlgfdTGm18rn489UpauJqPk0rr52rpeSSdqLVEcqwIXssFirIPzSaXwUynseSk4s3pB1hxnrT98KH1rt+rihBqHQI+m6K0uZQzDfz+Xy502ZQTBFdEYBLNPeQmFVXgYhfpL4Q=";

    private static final String CQ_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDA0OTk1NTY3MiwKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YTY1YTEzMGRiYTJmMmVhZWI0MzM0NWQ3ZGNkMWUzNGI5YjAwZGQzNGY2ZWMwNThkYzNjZWZkMDNiNzAxMjFlIgogICAgfQogIH0KfQ==";
    private static final String CQ_SIGNATURE = "mt5PsPucovhZRN8+k8mcu2jHtrSTOSY736B0A3Z6JJvj2760mXTmXLR0V14TN+aWicXftaokslG/iOCLNbWukSfNYbrKS+DR55f3SHOUmast4eQ6lz9JP4yrLv7ceVyG/r9SQkXzrpkG+vSsotvG8krqN/Eh1VgXU8O3xPa4PsDs3EUj/dexsfHumlQrgMEkjE4K6xcBnYCm5AhOo293bJTq3/B33/5IGepz6EwRHhRiVghcacE5XL/Cx3EEShyTIMRYG++ixfuhF+iCPadP4LjaSWpK2oFJ2u57ZotsjqeDo9nKqg76frdFwP4psv8s+sHjw7BFwBX7dl8a9yOTHYPlcSw1Ocr8P54mfCo7FFyEQhjxmcH66/elu3LVdmqed5dg9vA6wWgMxlfIutvb3pkPQZGwdTeuG5uYUPBT93adgs7xi9i48YqizcUyO9/5FcPP7B0RFEPQ5FLh06Jnb3xsU7VHumL4UaXshwH73uRW4wYykMhH/xXHz84UMlz3iQL342bwjPCsi9w7MqxnAhE/Us0uZFFUh2kgXDPPItl9CKZo57NDmh9m79nXQNX/ETdxxm2YfQ2NryYuqs5K9TsdTPfGQVW+M08Akb+zZaHCmo0MBS4B9Kdr4f7n+1UigMrUJu6jUmSEd1qayAv5o26VU6mjLhMg/JLuQRWNFNE=";

    private static final String ARCADE_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDA1MDYxMDI4NywKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZjkzYTEzZWRhMjA0ZGZjN2FmOTY0NzU3N2M5YzllNjQ4MWExOTlmMGRjMTQ1ODU0NmRiNmJiNjZlYjljOTllIgogICAgfQogIH0KfQ==";
    private static final String ARCADE_SIGNATURE = "JHLqvfIuIjU+kLjBcqlqOEwI+9THFklCmg1Oum14qKKRRDM+dlv4Ee9oWYibD9BJvEVFfQxKWSBKu2JQlYlpyZMZr60CnjREZhWrK6GdD5ZAPfLbDul6dwpkb8MAawvNleAbtb5OwFy9Jz5E/XSNojffRXkQkt/mqiI6MYiBQgk+ldKVWs3tqftVYe+x9vRGTf7PW/3gh4fmO0IZ9oUF/IZinQd/cX0j+7/KJ3/E8mEg6gnIDo2tVZ2aitRlnDoiHKgEFnmfUDgpQbv8EN8ffDbZCXEQoLm0A/xPx+q+bnnW7AoW0eTGDzsvkfDGo9CFP1Y8vV1bd2/VFpprUp2kEuWgGiQ9TUKs5b8Vu/lDMXswrhz0xl/6UqEqw2Vx0v8ZkbfohYpWB9uhPpH6BVc3ap1fNUpWIS/xyO/NyGMYuyZHAvaHCM/Lvs39iNW5YdhsNgzU0FnEa7hjMvg1sqN0sKYO8nSEAFIsNGVA8XYAz+a9VjKHwE974jjBam5iW3daHHIgKfSHkqfIPDPZiCFjk229qjRw5gYCatR7iXFNmrlIAEv3ntINrUtDus69MW8xL8YlEcS8NRnL7OPc7ZuI+0sogqqDgSNr96CFHxPhFqbIDjYoQRxW3RpMDoa6FNuwzAw3q9xpQfYnc1g8cJIRCS3zrZ8a8Cemwv67zeMKzAU=";

    private static final String PAINTBALL_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDExOTc2NTUxNywKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lMjBlYmM4NTJhNGQxYzZhNzM2ODdjODM2OTVmYzE4YmU4NTVjNjI3NzIyYzJhYzQxOWYxMWUxZjAwN2QwNDhlIgogICAgfQogIH0KfQ==";
    private static final String PAINTBALL_SIGNATURE = "rmVcbIioK1+kpxwtrlDyeZ5ZQvlvI6CyKGsTuX3vCfIePH90EZS9moGA2y1fFdLjcusJ6fBM8yVcqda8+rDX/Et0rVFa53Z9uYLmnaUdLZkbbbFFeX54if6Gy+MvSWe5L9dnATzIheUNntxfRE4z9mAU2dmAr9gbiz1cd92uB4VUF2QwS7M5mAf4dP/svGXnRng9LmV1FCGR29tV2186SQSrTi33mugEVXDPO3N4Kw0XI90DOKINvjP29UjXuR9JEzW+IbJNxKWz0xJmMBWZUx+LNpdkNpEbdFyB0iD12Yx87+tXKOl4+JjkVOR4W3sFoO220/HUPGCP9x5lEf4iZgOcLeguKIjua9/0t6yW0kc5ns5Wj+2beTVFNeb6y8BqsLjuOV6NtBUuh7PPAB1K4mV18/t9qxdUpkFYurFYJzUo7LfJQQSeAncRPzD0CwQZrIiCnEbDkCVCDIqe3amFKELugOklf2TAkOMNvu56YinkmTAb9REE2PSAyTGPR9wVp8v+xLCQ7b7vhbuxjU/ezHzrgib6BJt1+6rjSMOxb3A4cu8GhRtfxkIIzvBprBIuf8brGjG2DsbacZgLxb46BrkZUCLSWVOBJeJsNPcz5lMQUpHyfXOLi4BVZ2Z8WbAirMU2RQO4UxQFP8huX61KtA8QjMWjZTbRS77hhaIiQSg=";

    private static final String DUELS_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY1MDA1MDgwMTU5NCwKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lOWQ0YTZhNDIzMGE5MDkxZDQwNTM5ZDhiOWVjNzZiMjhkMjMwMTZkYmM1MGRiMTk3OTgyZDg5NDY1YzQ3NDEiCiAgICB9CiAgfQp9";
    private static final String DUELS_SIGNATURE = "cmYL17h7mXEQkI/lb/YIktvLUu2+fWIJIf56cC56R7Gh6l8gbLq75B9Aokq9K6DCfYAUtalkyODW4FTdRXSh05Fh3oUFQYz6InPP7LJvl1IprhGX9vPlZJfRfTKo6D6ZT/jPOtLebCu7F6TQlT++5LmFa0X4wFiG9DZMBGSy6BMfzxubAEVVO12CMPCT2FTtLFidRwUewGqtmB2ticOfFJ/DWT2udeomSMjO48+nwfq5aaXYH+cNeswUuPLXrNdmYTTKSyhHPSX0Ws9ScmpVIYFY7QOC+q6sRIEisuE7oHSDBS1fmoXFohFcIMRuOyhX0LHWewhVymS9OjlsrQwhKF6yGKmESl78y1YoO4H2XxLKvdOnKSd2iaUC5KXlNBV5+Lg04c/gJBi2ZLA77vyZaQnOU+AO9ldMvY+PFN4L85XioRv1lY8kC2vxzfjy5FAWAnmSXa/SIw4fdREaUopiGCqYUJug5JAUhKStP79rlVrrGRqM7Xq/f1aysQZJrloB0iBrC6E72Eb7uEMnroTB1ejE3Kl1mF6mTlrtMSeS0DNR7VRX5IjeIwP1mvNQOZoFEODw17IqwWuhyg1qZvDF13PaW1Ik6k8Wfx7i+YOPot+ILQy0/JObixZnON6V7zb+HMqp3KzAyr0zIroPyHBrpSmhHFejxGxUiLJUOQCiFeQ=";

    private static final String SMP_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY4Mjk4NTM2MTMyMSwKICAicHJvZmlsZUlkIiA6ICIwN2RkYjYxZmU0NmI0MDEyYjUyMWQ1ZjVmZWFhZWQ5ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJIZWxpb2xvZ3kiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FmNzkxODA2Mjk1YWYyYTI3YTJlMDg5Mjk0NzBmNjg4NGMyNWFkZGMzZTk2MTZiYjI4MzJlYzFjY2U1N2VkNSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9LAogICAgIkNBUEUiIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNDBjMGUwM2RkMjRhMTFiMTVhOGIzM2MyYTdlOWUzMmFiYjIwNTFiMjQ4MWQwYmE3ZGVmZDYzNWNhN2E5MzMiCiAgICB9CiAgfQp9";
    private static final String SMP_SIGNATURE = "W6BDEoujmYCKUwBX4P8XKI9SqGPGhJbUp6jR0jQb3EbeMZvLQ0Sq1nz+mjQ+IGdHMViPBhEtPZWICySy2iysDZFNX1ewcgtRj2+rVUX7kJ7H85fmdHrwpGrbNgkogNBr4pDxlub3bK6taW/Ub247umStYZkg029rxdNllVIg1F9GFeszfP0D/x5oO4xMi7zxqqsK9E/G2NuwA5yTKE/fiVKpltoQ26JHBEHotnMcT13ivlybVdrbGQch6EPphxp/yEozW7/v42uGC0jCgsihpZ8F/brug/QK8z2u1ECXsiQZHOGxUO1afYyQfJ3LPMnC/z49wV9Q1z/Ka1lJD3WCIqYSkRCmjpMPbxPQqAVCdOpNhilBc4wgVDkBZ+Gbnai+pXJJPT4W6JcT9eiaESuKaMnCNryYUi6tFfvtYWmBISc/bIRzck6w04SdLA+pRCGzQ6BCG+zzJmH7+LcLinhV7h03Z+9LdBZAHmarm6fF2qPp13/sRDYHoC1iZaYI3ksw4QGIYy53vnjb+/ONo0QQttmK86rsQd8cV+xiHAbrA16ukNbOjB1YzQUX/42v1inO1lvmfpVtZdSn0PUb8UNryFWIC3rAR7zbAiSXxIjtSkKR5614hzNaEQPDrC3bapEDNjGf6UuQTU6kK0Wh3eK6CSzQoAuWPb1RjBgd2UniOtk=";

    private static LobbyMap map;
    private static AuroraMCLobby lobby;

    private static final GUIItem prefsItem;
    private static final GUIItem cosmeticsItem;
    private static final GUIItem gamesItem;
    private static final GUIItem lobbyItem;

    private static final GUIItem checkpointItem;
    private static final GUIItem restartItem;
    private static final GUIItem cancelItem;

    private static Map<String, String> versionNumbers;
    private static Map<String, List<Changelog>> changelogs;
    private static Changelog latestChangelog;
    private static CommunityPoll poll;

    private static final Map<String, ServerInfo> gameServers;
    private static final Map<String, Integer> gameTotals;
    private static final Map<String, Hologram> gameHolos;

    private static EntityPlayer lunaEntity;
    private static EntityPlayer cometEntity;
    private static EntityPlayer skyeEntity;
    private static EntityPlayer arcadeEntity;
    private static EntityPlayer cqEntity;
    private static EntityPlayer paintballEntity;
    private static EntityPlayer duelsEntity;
    private static EntityPlayer smpEntity;

    private static Block chestBlock;

    private static Crate currentCrate;
    private static AuroraMCLobbyPlayer cratePlayer;
    private static boolean crateAnimationFinished;

    private static Parkour easy;
    private static Parkour medium;
    private static Parkour hard;


    static {
        lobbyItem = new GUIItem(Material.NETHER_STAR, "&a&lSwitch Lobbies");
        gamesItem = new GUIItem(Material.COMPASS, "&a&lServer Navigation");
        prefsItem = new GUIItem(Material.REDSTONE_COMPARATOR, "&a&lView Preferences");
        cosmeticsItem = new GUIItem(Material.EMERALD, "&a&lView Cosmetics");

        checkpointItem = new GUIItem(Material.EYE_OF_ENDER, "&a&lTeleport to Last Checkpoint");
        restartItem = new GUIItem(Material.WOOD_PLATE, "&c&lRestart");
        cancelItem = new GUIItem(Material.BED, "&c&lCancel");
        gameServers = new HashMap<>();
        gameTotals = new HashMap<>();
        gameHolos = new HashMap<>();

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
            gameServers.put(info.getName(), info);
        }
    }

    public static void spawnEntities() {
        GameProfile profile;
        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert(" &e&lLuna"));
        profile.getProperties().put("textures", new Property("textures", LUNA_SKIN, LUNA_SIGNATURE));
        lunaEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        lunaEntity.setLocation(7.5, 70.0, 12.5, 145.0f, 0f);
        ServerAPI.registerFakePlayer(lunaEntity);


        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("&d&lComet"));
        profile.getProperties().put("textures", new Property("textures", LUNA_SKIN, LUNA_SIGNATURE));
        cometEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        cometEntity.setLocation(-33.5, 71.0, 13.5,-45f, 0f);
        ServerAPI.registerFakePlayer(cometEntity);

        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("&b&lSkye"));
        profile.getProperties().put("textures", new Property("textures", LUNA_SKIN, LUNA_SIGNATURE));
        skyeEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        skyeEntity.setLocation(60.5, 70.0, 70.5, 145.0f, 0f);
        ServerAPI.registerFakePlayer(skyeEntity);

        /*
            ==================================================================================
                                             GAME NPC'S
            ==================================================================================
         */



        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("Arcade Mode§r "));
        profile.getProperties().put("textures", new Property("textures", ARCADE_SKIN, ARCADE_SIGNATURE));
        arcadeEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        arcadeEntity.setLocation(-17.5, 70.0, 48.5, -180f, 0f);
        ServerAPI.registerFakePlayer(arcadeEntity);

        Hologram hologram = new Hologram(null, new Location(Bukkit.getWorld("world"), -17.5, 72.3f, 48.5), null);
        hologram.addLine(1, "&b" + gameTotals.getOrDefault("ARCADE_MODE", 0) + " &fPlayers Online");
        hologram.spawn();
        gameHolos.put("ARCADE_MODE", hologram);

        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("Paintball§r "));
        profile.getProperties().put("textures", new Property("textures", PAINTBALL_SKIN, PAINTBALL_SIGNATURE));
        paintballEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        paintballEntity.setLocation(-2.5, 70.0, 41.5, -180f, 0f);
        ServerAPI.registerFakePlayer(paintballEntity);

        hologram = new Hologram(null, new Location(Bukkit.getWorld("world"), -2.5, 72.3f, 41.5), null);
        hologram.addLine(1, "&b" + gameTotals.getOrDefault("PAINTBALL", 0) + " &fPlayers Online");
        hologram.spawn();
        gameHolos.put("PAINTBALL", hologram);

        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("Crystal Quest "));
        profile.getProperties().put("textures", new Property("textures", CQ_SKIN, CQ_SIGNATURE));
        cqEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        cqEntity.setLocation(-10.5, 70.0, 28.5, -180f, 0f);
        ServerAPI.registerFakePlayer(cqEntity);

        hologram = new Hologram(null, new Location(Bukkit.getWorld("world"), -10.5f, 72.3f, 28.5f), null);
        hologram.addLine(1, "&b" + gameTotals.getOrDefault("CRYSTAL_QUEST", 0) + " &fPlayers Online");
        hologram.spawn();
        gameHolos.put("CRYSTAL_QUEST", hologram);

        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("Duels§r "));
        profile.getProperties().put("textures", new Property("textures", DUELS_SKIN, DUELS_SIGNATURE));
        duelsEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        duelsEntity.setLocation(3.5, 70.0, 41.5, -180f, 0f);
        ServerAPI.registerFakePlayer(duelsEntity);


        profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("NuttersSMP§r "));
        profile.getProperties().put("textures", new Property("textures", SMP_SKIN, SMP_SIGNATURE));
        smpEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        smpEntity.setLocation(-5.5, 70.0, -1.5, -145f, 0f);
        ServerAPI.registerFakePlayer(smpEntity);

        hologram = new Hologram(null, new Location(Bukkit.getWorld("world"), 3.5f, 72.3f, 41.5f), null);
        hologram.addLine(1, "&b" + gameTotals.getOrDefault("DUELS", 0) + " &fPlayers Online");
        hologram.spawn();
        gameHolos.put("DUELS", hologram);
    }

     public static void addGameServer(String serverName) {
        gameServers.put(serverName, AuroraMCAPI.getDbManager().getServerDetailsByName(serverName, AuroraMCAPI.getInfo().getNetwork().name()));
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

    public static EntityPlayer getLunaEntity() {
        return lunaEntity;
    }

    public static ServerInfo getGameServer(String server) {
        return gameServers.get(server);
    }

    public static Map<String, ServerInfo> getGameServers() {
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

    public static void loadParkours() {
        easy = new Parkour(1, "&a&lEasy Parkour", new Reward("&a+50 XP\n&6+50 Crowns\n&d+50 Tickets", 50, 50, 50, Collections.emptyMap(), Collections.emptyList()), new Reward("&a+1000 XP\n&6+1000 Crowns\n&d+1000 Tickets", 1000, 1000, 1000, Collections.emptyMap(), Collections.emptyList()));
        medium = new Parkour(2, "&6&lMedium Parkour", new Reward("&a+50 XP\n&6+50 Crowns\n&d+50 Tickets", 50, 50, 50, Collections.emptyMap(), Collections.emptyList()), new Reward("&a+3000 XP\n&6+15000 Crowns\n&d+15000 Tickets", 3000, 15000, 15000, Collections.emptyMap(), Collections.emptyList()));
        hard = new Parkour(3, "&c&lHard Parkour", new Reward("&A+50 XP\n&6+50 Crowns\n&d+50 Tickets", 50, 50, 50, Collections.emptyMap(), Collections.emptyList()), new Reward("&a+5000 XP\n&6+50000 Crowns\n&d+50000 Tickets", 5000, 50000, 50000, Collections.emptyMap(), Collections.emptyList()));
    }

    public static Parkour getEasy() {
        return easy;
    }

    public static Parkour getHard() {
        return hard;
    }

    public static Parkour getMedium() {
        return medium;
    }

    public static GUIItem getCancelItem() {
        return cancelItem;
    }

    public static GUIItem getCheckpointItem() {
        return checkpointItem;
    }

    public static GUIItem getRestartItem() {
        return restartItem;
    }

    public static void updateTotals() {
        AtomicInteger amount = new AtomicInteger();
        amount.set(0);
        LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getServerType().getString("game").equalsIgnoreCase("CRYSTAL_QUEST")).sorted((game1, game2) -> Integer.compare(game2.getCurrentPlayers(), game1.getCurrentPlayers())).forEach(info -> amount.addAndGet(info.getCurrentPlayers()));
        gameTotals.put("CRYSTAL_QUEST",amount.get());
        gameHolos.get("CRYSTAL_QUEST").getLines().get(1).setText("&b" + amount.get() + " &fPlayers Online");
        gameHolos.get("CRYSTAL_QUEST").update();
        amount.set(0);
        LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getServerType().getString("game").equalsIgnoreCase("DUELS")).sorted((game1, game2) -> Integer.compare(game2.getCurrentPlayers(), game1.getCurrentPlayers())).forEach(info -> amount.addAndGet(info.getCurrentPlayers()));
        gameTotals.put("DUELS",amount.get());
        gameHolos.get("DUELS").getLines().get(1).setText("&b" + amount.get() + " &fPlayers Online");
        gameHolos.get("DUELS").update();
        amount.set(0);
        LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getServerType().getString("game").equalsIgnoreCase("PAINTBALL")).sorted((game1, game2) -> Integer.compare(game2.getCurrentPlayers(), game1.getCurrentPlayers())).forEach(info -> amount.addAndGet(info.getCurrentPlayers()));
        gameTotals.put("PAINTBALL",amount.get());
        gameHolos.get("PAINTBALL").getLines().get(1).setText("&b" + amount.get() + " &fPlayers Online");
        gameHolos.get("PAINTBALL").update();
        amount.set(0);
        LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getServerType().getString("game").equalsIgnoreCase("ARCADE_MODE")).sorted((game1, game2) -> Integer.compare(game2.getCurrentPlayers(), game1.getCurrentPlayers())).forEach(info -> amount.addAndGet(info.getCurrentPlayers()));
        gameTotals.put("ARCADE_MODE",amount.get());
        gameHolos.get("ARCADE_MODE").getLines().get(1).setText("&b" + amount.get() + " &fPlayers Online");
        gameHolos.get("ARCADE_MODE").update();
    }

    public static Map<String, Integer> getGameTotals() {
        return gameTotals;
    }

    public static EntityPlayer getCometEntity() {
        return cometEntity;
    }

    public static EntityPlayer getSkyeEntity() {
        return skyeEntity;
    }

    public static EntityPlayer getSmpEntity() {
        return smpEntity;
    }
}

