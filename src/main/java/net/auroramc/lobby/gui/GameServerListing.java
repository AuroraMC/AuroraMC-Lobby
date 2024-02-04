/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.api.backend.info.ServerInfo;
import net.auroramc.api.backend.info.ServerState;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class GameServerListing extends GUI {

    private final AuroraMCServerPlayer player;
    private final String gameCode;
    private final String gameName;
    private final String serverCode;

    public GameServerListing(AuroraMCServerPlayer player, String gameCode, String gameName, String serverCode) {
        super("&3&lSelect a server!", 5, true);
        this.player = player;
        this.gameCode = gameCode;
        this.gameName = gameName;
        this.serverCode = serverCode;
        this.border("&3&lSelect a server!", null);

        this.setItem(5, 4, new GUIItem(Material.WOOL, "&7&lSpectate In-Progress Games", 1, ";&r&aClick here to view games in progress!"));

        List<ServerInfo> infos = LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getServerType().getString("game").equalsIgnoreCase(gameCode) && (gameServerInfo.getServerState() == ServerState.STARTING || gameServerInfo.getServerState() == ServerState.WAITING_FOR_PLAYERS || gameServerInfo.getServerState() == ServerState.ACTIVE)).sorted((game1, game2) -> Integer.compare(game2.getCurrentPlayers(), game1.getCurrentPlayers())).collect(Collectors.toList());
        int row = 2;
        int column = 2;
        for (ServerInfo info : infos) {
            this.setItem(row, column, new GUIItem(Material.STAINED_GLASS, "&3&l" + gameName + " Server " + info.getName().split("-")[1], info.getCurrentPlayers(), ";&r&fPlayers: **" + info.getCurrentPlayers() + "**&f/**" + info.getMaxPlayers() + "**;&r&fGame: **" + info.getActiveGame() + "**" + ((!info.getActiveMap().equalsIgnoreCase("n/a"))?";&r&fMap: **" + info.getActiveMap() + "**":"") + ";&r&fStatus: **" + info.getServerState().getName() + "**;;" + ((info.getCurrentPlayers() == info.getMaxPlayers())?"&cThis server is currently full!":"&aClick to join the server!"), (short)((info.getCurrentPlayers() == info.getMaxPlayers() || info.getServerState() == ServerState.INACTIVE)?14:((info.getServerState() == ServerState.STARTING)?4:0))));
            column++;
            if (column == 7) {
                row++;
                column = 2;
                if (row == 4) {
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() != Material.STAINED_GLASS) {
            if (item.getType() == Material.WOOL) {
                GameInProgressServerListing sl = new GameInProgressServerListing(player, gameCode, gameName, serverCode);
                sl.open(player);
                return;
            }
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }
        String[] name = item.getItemMeta().getDisplayName().split(" ");

        int server = Integer.parseInt(name[name.length - 1]);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("JoinGame");
        out.writeUTF(player.getName());
        out.writeUTF(serverCode + "-" + server);
        player.sendPluginMessage(out.toByteArray());
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 0);
    }

    public void update() {
        List<ServerInfo> infos = LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getServerType().getString("game").equalsIgnoreCase(gameCode) && (gameServerInfo.getServerState() == ServerState.STARTING || gameServerInfo.getServerState() == ServerState.WAITING_FOR_PLAYERS || gameServerInfo.getServerState() == ServerState.ACTIVE)).sorted((game1, game2) -> Integer.compare(game2.getCurrentPlayers(), game1.getCurrentPlayers())).collect(Collectors.toList());
        int row = 2;
        int column = 2;
        for (int i = 0;i < 10;i++) {
            if (infos.size() <= i) {
                this.updateItem(row, column, null);
            } else {
                ServerInfo info = infos.get(i);
                this.updateItem(row, column, new GUIItem(Material.STAINED_GLASS, "&3&l" + gameName + " Server " + info.getName().split("-")[1], info.getCurrentPlayers(), ";&r&fPlayers: **" + info.getCurrentPlayers() + "**&f/**" + info.getMaxPlayers() + "**;&r&fGame: **" + info.getActiveGame() + "**" + ((!info.getActiveMap().equalsIgnoreCase("n/a"))?";&r&fMap: **" + info.getActiveMap() + "**":"") + ";&r&fStatus: **" + info.getServerState().getName() + "**;;" + ((info.getCurrentPlayers() == info.getMaxPlayers())?"&cThis server is currently full!":"&aClick to join the server!"), (short)((info.getCurrentPlayers() == info.getMaxPlayers() || info.getServerState() == ServerState.INACTIVE)?14:((info.getServerState() == ServerState.STARTING)?4:0))));
            }
            column++;
            if (column == 7) {
                row++;
                column = 2;
                if (row == 4) {
                    break;
                }
            }
        }
    }
}
