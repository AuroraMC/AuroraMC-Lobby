/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.GameServerInfo;
import net.auroramc.lobby.api.util.ServerState;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LobbySwitcher extends GUI {

    private final AuroraMCPlayer player;

    public LobbySwitcher(AuroraMCPlayer player) {
        super("&3&lSelect a server!", 5, true);
        this.player = player;
        this.border("&3&lSelect a server!", null);

        List<GameServerInfo> infos = LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getInfo().getServerType().getString("game").equalsIgnoreCase("lobby")).sorted(Comparator.comparingInt(game -> Integer.parseInt(game.getInfo().getName().split("-")[1]))).collect(Collectors.toList());
        int row = 1;
        int column = 1;
        for (GameServerInfo info : infos) {
            this.setItem(row, column, new GUIItem(Material.STAINED_GLASS, "&3&lLobby Server " + info.getInfo().getName().split("-")[1], info.getCurrentPlayers(), ";&r&fPlayers: **" + info.getCurrentPlayers() + "**&f/**" + info.getMaxPlayers() + ";;" + ((info.getCurrentPlayers() == info.getMaxPlayers())?"&cThis lobby is currently full! Purchase a rank;&cat store.auroramc.net to bypass this!":"&aClick to join this lobby!"), (short)((info.getCurrentPlayers() == info.getMaxPlayers())?14:0)));
            column++;
            if (column == 8) {
                row++;
                column = 2;
                if (row == 5) {
                    break;
                }
            }
        }
    }

    @Override
    public void onClick(int row, int column, ItemStack item, ClickType clickType) {
        if (item.getType() != Material.STAINED_GLASS) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
            return;
        }
        String[] name = item.getItemMeta().getDisplayName().split(" ");

        int server = Integer.parseInt(name[name.length - 1]);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("JoinGame");
        out.writeUTF(player.getName());
        out.writeUTF("Lobby-" + server);
        player.getPlayer().sendPluginMessage(AuroraMCAPI.getCore(), "BungeeCord", out.toByteArray());
        player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 0);
    }

    public void update() {
        List<GameServerInfo> infos = LobbyAPI.getGameServers().values().stream().filter(gameServerInfo -> gameServerInfo.getInfo().getServerType().getString("game").equalsIgnoreCase("lobby")).sorted(Comparator.comparingInt(game -> Integer.parseInt(game.getInfo().getName().split("-")[1]))).collect(Collectors.toList());
        int row = 1;
        int column = 1;
        for (int i = 0;i < 10;i++) {
            if (infos.size() <= i) {
                this.updateItem(row, column, null);
            } else {
                GameServerInfo info = infos.get(i);
                this.updateItem(row, column, new GUIItem(Material.STAINED_GLASS, "&3&lLobby Server " + info.getInfo().getName().split("-")[1], info.getCurrentPlayers(), ";&r&fPlayers: **" + info.getCurrentPlayers() + "**&f/**" + info.getMaxPlayers() + ";;" + ((info.getCurrentPlayers() == info.getMaxPlayers())?"&cThis lobby is currently full! Purchase a rank;&cat store.auroramc.net to bypass this!":"&aClick to join this lobby!"), (short)((info.getCurrentPlayers() == info.getMaxPlayers())?14:0)));
            }
            column++;
            if (column == 8) {
                row++;
                column = 2;
                if (row == 5) {
                    break;
                }
            }
        }
    }
}
