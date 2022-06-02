/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.events.FakePlayerInteractEvent;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.GameServerListing;
import net.auroramc.lobby.gui.TheMonke;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FakePlayerListener implements Listener {

    @EventHandler
    public void onFakePlayerInteract(FakePlayerInteractEvent e) {
        AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) e.getPlayer();
        if (player.canClick()) {
            player.click();
            if (e.getFakePlayer().equals(LobbyAPI.getMonkeyEntity())) {
                TheMonke monke = new TheMonke((AuroraMCLobbyPlayer) e.getPlayer());
                monke.open(e.getPlayer());
                AuroraMCAPI.openGUI(e.getPlayer(), monke);
            } else if (e.getFakePlayer().equals(LobbyAPI.getArcadeEntity())) {
                GameServerListing listing = new GameServerListing(player, "ARCADE_MODE", "Arcade Mode", "Arcade");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
             } else if (e.getFakePlayer().equals(LobbyAPI.getDuelsEntity())) {
                GameServerListing listing = new GameServerListing(player, "DUELS", "Duels", "Duels");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
            } else if (e.getFakePlayer().equals(LobbyAPI.getCqEntity())) {
                GameServerListing listing = new GameServerListing(player, "CRYSTAL_QUEST", "Crystal Quest", "CQ");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
            } else if (e.getFakePlayer().equals(LobbyAPI.getPaintballEntity())) {
                GameServerListing listing = new GameServerListing(player, "PAINTBALL", "Paintball", "PB");
                listing.open(player);
                AuroraMCAPI.openGUI(player, listing);
            }
        }
    }
}
