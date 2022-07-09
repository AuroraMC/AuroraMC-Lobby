/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.gui.crates.buy.confirmation;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.utils.gui.GUI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.core.cosmetics.crates.IronCrate;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.gui.crates.buy.BuyDiamondCrate;
import net.auroramc.lobby.gui.crates.buy.BuyIronCrate;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ConfirmIron extends GUI {

    private final AuroraMCLobbyPlayer player;
    private final int amount;

    public ConfirmIron(AuroraMCLobbyPlayer player, int amount) {
        super("&7&lBuy " + amount + " Iron Crate" + ((amount > 1)?"s?":"?"), 4, true);
        border("&7&lBuy " + amount + " Iron Crate" + ((amount > 1)?"s?":"?"), null);


        this.player = player;
        this.amount = amount;

        this.setItem(2, 2, new GUIItem(Material.STAINED_CLAY, "&c&lCancel", 1, ";&rGo back to the previous menu.", (short)14));
        this.setItem(2, 6, new GUIItem(Material.STAINED_CLAY, "&a&lConfirm", 1, ";&d" + (CrateUtil.IRON_CRATE_PRICE * amount) + " Tickets&r will be taken from your account.", (short)5));
    }

    @Override
    public void onClick(int row, int column, ItemStack itemStack, ClickType clickType) {
        if (column == 2) {
            BuyIronCrate crate = new BuyIronCrate(player);
            crate.open(player);
            AuroraMCAPI.openGUI(player, crate);
        } else if (column == 6) {
            player.getPlayer().closeInventory();
            if (player.getBank().getTickets() >= (amount * CrateUtil.IRON_CRATE_PRICE)) {
                player.getBank().withdrawTickets((amount * CrateUtil.IRON_CRATE_PRICE), false, true);
                for (int i = 0;i < amount;i++) {
                    IronCrate crate = CrateUtil.generateIronCrate(player.getId());
                    player.getCrates().add(crate);
                }
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "You purchased &7" + amount + " Iron Crates&r and &d" + (amount * CrateUtil.IRON_CRATE_PRICE) + " Tickets&r were withdrawn from your account."));
            } else {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Crates", "You have insufficient funds to purchase that crate. You need &d" + ((amount * CrateUtil.IRON_CRATE_PRICE) - player.getBank().getTickets()) + "&r additional Tickets."));
            }
        } else {
            player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ITEM_BREAK, 100, 0);
        }
    }
}