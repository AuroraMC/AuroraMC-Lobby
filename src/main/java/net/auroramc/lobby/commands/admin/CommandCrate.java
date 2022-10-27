/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.commands.admin;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.store.Payment;
import net.auroramc.core.api.command.Command;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.permissions.Permission;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.gui.support.PaymentHistory;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandCrate extends Command {


    public CommandCrate() {
        super("crate", Collections.singletonList("crates"), Arrays.asList(Permission.ADMIN, Permission.SUPPORT), false, null);
    }

    @Override
    public void execute(AuroraMCPlayer player, String aliasUsed, List<String> args) {
        if (args.size() == 3) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    UUID uuid = AuroraMCAPI.getDbManager().getUUID(args.get(0));
                    if (uuid == null) {
                        player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", String.format("No player found named **%s**.", args.get(0))));
                        return;
                    }

                    int id = AuroraMCAPI.getDbManager().getAuroraMCID(uuid);

                    int amount;

                    try {
                        amount = Integer.parseInt(args.get(2));
                    } catch (NumberFormatException e) {
                        player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "That is not a valid amount. You must choose a number between 1 and 50."));
                        return;
                    }

                    if (amount < 1 || amount > 50) {
                        player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "That is not a valid amount. You must choose a number between 1 and 50."));
                        return;
                    }

                    switch (args.get(1).toLowerCase()) {
                        case "iron": {
                            for (int i = 0;i < amount;i++) {
                                Crate crate = CrateUtil.generateIronCrate(id);
                                if (AuroraMCAPI.getPlayer(args.get(0)) != null) {
                                    ((AuroraMCLobbyPlayer)AuroraMCAPI.getPlayer(args.get(0))).getCrates().add(crate);
                                }
                            }
                            break;
                        }
                        case "gold": {
                            for (int i = 0;i < amount;i++) {
                                Crate crate = CrateUtil.generateGoldCrate(id);
                                if (AuroraMCAPI.getPlayer(args.get(0)) != null) {
                                    ((AuroraMCLobbyPlayer)AuroraMCAPI.getPlayer(args.get(0))).getCrates().add(crate);
                                }
                            }
                            break;
                        }
                        case "diamond": {
                            for (int i = 0;i < amount;i++) {
                                Crate crate = CrateUtil.generateDiamondCrate(id);
                                if (AuroraMCAPI.getPlayer(args.get(0)) != null) {
                                    ((AuroraMCLobbyPlayer)AuroraMCAPI.getPlayer(args.get(0))).getCrates().add(crate);
                                }
                            }
                            break;
                        }
                        case "emerald": {
                            for (int i = 0;i < amount;i++) {
                                Crate crate = CrateUtil.generateEmeraldCrate(id);
                                if (AuroraMCAPI.getPlayer(args.get(0)) != null) {
                                    ((AuroraMCLobbyPlayer)AuroraMCAPI.getPlayer(args.get(0))).getCrates().add(crate);
                                }
                            }
                            break;
                        }
                    }
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crate", "**" + args.get(0) + "** has been given **" + amount + " " + args.get(1).toUpperCase() + " Crates**"));
                }
            }.runTaskAsynchronously(AuroraMCAPI.getCore());

        } else {
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "Invalid syntax. Correct syntax: **/crate [player] [type] [amount]**"));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCPlayer auroraMCPlayer, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
