/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api;

import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.AuroraMCLobby;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LobbyAPI {

    private static LobbyMap map;
    private static AuroraMCLobby lobby;

    private static final GUIItem prefsItem;
    private static final GUIItem cosmeticsItem;
    private static final GUIItem gamesItem;
    private static final GUIItem lobbyItem;


    static {
        lobbyItem = new GUIItem(Material.NETHER_STAR, "&a&lSwitch Lobbies");
        gamesItem = new GUIItem(Material.COMPASS, "&a&lBrowse Games");
        prefsItem = new GUIItem(Material.REDSTONE_COMPARATOR, "&a&lView Preferences");
        cosmeticsItem = new GUIItem(Material.EMERALD, "&a&lView Cosmetics");
    }


    public static void setLobbyMap(LobbyMap map) {
        LobbyAPI.map = map;
    }

    public static void init(AuroraMCLobby lobby) {
        LobbyAPI.lobby = lobby;
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


}
