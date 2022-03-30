/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.events.FakePlayerInteractEvent;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FakePlayerListener implements Listener {

    @EventHandler
    public void onFakePlayerInteract(FakePlayerInteractEvent e) {
        if (e.getFakePlayer().equals(LobbyAPI.getMonkeyEntity())) {
            AuroraMCLobbyPlayer player = (AuroraMCLobbyPlayer) e.getPlayer();
            if (player.canMonkeClick()) {
                player.monkeClick();
                e.getPlayer().getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("The Monke", "Hello! I am the Monke, your local delivery ape! Visit me to collect your monthly bonuses, rewards and more!"));
            }
        }
    }
}
