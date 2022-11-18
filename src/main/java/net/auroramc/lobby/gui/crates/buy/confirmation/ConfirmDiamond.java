/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates.buy.confirmation;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.crates.buy.BuyDiamondCrate;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfirmDiamond extends GUI {

    private final AuroraMCLobbyPlayer player;
    private final int amount;

    public ConfirmDiamond(AuroraMCLobbyPlayer player, int amount) {
        super("&b&lBuy " + amount + " Diamond Crate" + ((amount > 1)?"s?":"?"), 4, true);
        border("&b&lBuy " + amount + " Diamond Crate" + ((amount > 1)?"s?":"?"), null);

        this.player = player;
        this.amount = amount;

        this.setItem(2, 2, new GUIItem(Material.STAINED_CLAY, "&c&lCancel", 1, ";&r&fGo back to the previous menu.", (short)14));
        this.setItem(2, 6, new GUIItem(Material.STAINED_CLAY, "&a&lConfirm", 1, ";&d" + (CrateUtil.DIAMOND_CRATE_PRICE * amount) + " Tickets&r&f will be taken from your account.", (short)5));
    }

    @Override
    public void onClick(int row, int column, ItemStack itemStack, ClickType clickType) {
        if (column == 2) {
            BuyDiamondCrate crate = new BuyDiamondCrate(player);
            crate.open(player);
            AuroraMCAPI.openGUI(player, crate);
        } else if (column == 6) {
            player.getPlayer().closeInventory();
            if (player.getBank().getTickets() >= (amount * CrateUtil.DIAMOND_CRATE_PRICE)) {
                player.getBank().withdrawTickets((amount * CrateUtil.DIAMOND_CRATE_PRICE), false, true);
                for (int i = 0;i < amount;i++) {
                    DiamondCrate crate = CrateUtil.generateDiamondCrate(player.getId());
                    player.getCrates().add(crate);
                }
                long amountOfCrates = player.getCrates().stream().filter(crate2 -> crate2.getOpened() <= 0).count();
                if (player.getHolograms().get("crates").getLines().size() == 1) {
                    player.getHolograms().get("crates").addLine(2, "&fYou have &b" + amountOfCrates + " &fcrates to open!");
                } else {
                    player.getHolograms().get("crates").getLines().get(2).setText("&fYou have &b" + amountOfCrates + " &fcrates to open!");
                }
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "You purchased &b" + amount + " Diamond Crates&r&f and &d" + (amount * CrateUtil.DIAMOND_CRATE_PRICE) + " Tickets&r&f were withdrawn from your account."));
            } else {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "You have insufficient funds to purchase that crate. You need &d" + ((amount * CrateUtil.DIAMOND_CRATE_PRICE) - player.getBank().getTickets()) + "&r&f additional Tickets."));
            }
        } else {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
        }
    }
}
