/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.events.player.PlayerFakePlayerInteractEvent;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.GameServerListing;
import net.auroramc.lobby.gui.LieutenantLuna;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakePlayerListener implements Listener {

    private static final List<String> lunaPhrases;
    private static final List<String> cometPhrases;
    private static final List<String> skyePhrases;

    static {
        lunaPhrases = new ArrayList<>();
        cometPhrases = new ArrayList<>();
        skyePhrases = new ArrayList<>();

        lunaPhrases.add("I got the call telling me that my crew had crashed.. I had to bring resupplies immediately to make sure they can get back to the mission A.S.A.P!");

        cometPhrases.add("What happened? Where are we? Are the rest of the crew okay????");

        skyePhrases.add("This isn't good... Lieutenant isn't going to be happy...");

    }

    @EventHandler
    public void onFakePlayerInteract(PlayerFakePlayerInteractEvent e) {
        AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) e.getPlayer();
        if (player.canClick()) {
            player.click();
            if (e.getFakePlayer().equals(LobbyAPI.getLunaEntity())) {
                LieutenantLuna luna = new LieutenantLuna((AuroraMCLobbyPlayer) e.getPlayer());
                luna.open(e.getPlayer());
                player.sendMessage(new TextComponent(TextFormatter.convert("&6&lLieutenant &e&lLuna&r &6&l»&r " + lunaPhrases.get(new Random().nextInt(lunaPhrases.size())))));
            } else if (e.getFakePlayer().equals(LobbyAPI.getArcadeEntity())) {
                GameServerListing listing = new GameServerListing(player, "ARCADE_MODE", "Arcade Mode", "Arcade");
                listing.open(player);
             } else if (e.getFakePlayer().equals(LobbyAPI.getDuelsEntity())) {
                GameServerListing listing = new GameServerListing(player, "DUELS", "Duels", "Duels");
                listing.open(player);
            } else if (e.getFakePlayer().equals(LobbyAPI.getCqEntity())) {
                GameServerListing listing = new GameServerListing(player, "CRYSTAL_QUEST", "Crystal Quest", "CQ");
                listing.open(player);
            } else if (e.getFakePlayer().equals(LobbyAPI.getPaintballEntity())) {
                GameServerListing listing = new GameServerListing(player, "PAINTBALL", "Paintball", "PB");
                listing.open(player);
            } else if (e.getFakePlayer().equals(LobbyAPI.getCometEntity())) {
                player.sendMessage(new TextComponent(TextFormatter.convert("&5&lColonel &d&lComet&r &5&l»&r " + cometPhrases.get(new Random().nextInt(cometPhrases.size())))));
            } else if (e.getFakePlayer().equals(LobbyAPI.getSkyeEntity())) {
                player.sendMessage(new TextComponent(TextFormatter.convert("&3&lSergeant &b&lSkye&r &3&l»&r " + skyePhrases.get(new Random().nextInt(skyePhrases.size())))));
            }
        }
    }
}
