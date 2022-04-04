/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.util;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.players.scoreboard.PlayerScoreboard;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateScoreboardRunnable extends BukkitRunnable {


    @Override
    public void run() {
        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
            PlayerScoreboard scoreboard = player.getScoreboard();
            scoreboard.setTitle("&3-= &b&lAURORAMC&r &3=-");
            scoreboard.setLine(14, " ");
            scoreboard.setLine(13, "&c&l«RANK»");
            scoreboard.setLine(12, player.getRank().getName());
            scoreboard.setLine(11, " ");
            scoreboard.setLine(10, "&6&l«CROWNS»");

            double crowns = player.getBank().getCrowns();
            String suffC = "";

            if (crowns >= 1000000) {
                crowns = crowns / 10000;
                crowns = Math.floor(crowns) / 100d;
                suffC = " million";
                if (crowns >= 1000) {
                    crowns = crowns / 10;
                    crowns = Math.floor(crowns) / 100d;
                    suffC = " billion";
                    if (crowns >= 1000) {
                        crowns = crowns / 10;
                        crowns = Math.floor(crowns) / 100d;
                        suffC = " trillion";
                    }
                }
            }

            double tickets = player.getBank().getTickets();
            String suffT = "";

            if (tickets >= 1000000) {
                if (tickets >= 1000000000) {
                    if (tickets >= 1000000000000d) {
                        tickets = tickets / 10000000000L;
                        tickets = Math.floor(tickets) / 100d;
                        suffT = " trillion";
                    } else {
                        tickets = tickets / 10000000;
                        tickets = Math.floor(tickets) / 100d;
                        suffT = " billion";
                    }
                } else {
                    tickets = tickets / 10000;
                    tickets = Math.floor(tickets) / 100d;
                    suffT = " million";
                }
            }

            scoreboard.setLine(9, ((suffC.equals(""))?String.format("%,d",player.getBank().getCrowns()):crowns + suffC));
            scoreboard.setLine(8, "  ");
            scoreboard.setLine(7, "&d&l«TICKETS»");
            scoreboard.setLine(6, ((suffT.equals(""))?String.format("%,d",player.getBank().getTickets()):tickets + suffT));
            scoreboard.setLine(5, "   ");
            scoreboard.setLine(4, "&a&l«SERVER»");
            if (player.getPreferences().isHideDisguiseNameEnabled() && player.isDisguised()) {
                scoreboard.setLine(3, "&oHidden");
            } else {
                scoreboard.setLine(3, AuroraMCAPI.getServerInfo().getName());
            }
            scoreboard.setLine(2, "    ");
            scoreboard.setLine(1, "&7auroramc.net");
        }
    }
}
