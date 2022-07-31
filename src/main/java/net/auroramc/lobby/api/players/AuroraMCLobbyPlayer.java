/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.players;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.core.api.cosmetics.Gadget;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.cosmetics.crates.DiamondCrate;
import net.auroramc.core.cosmetics.crates.EmeraldCrate;
import net.auroramc.core.cosmetics.crates.GoldCrate;
import net.auroramc.core.cosmetics.crates.IronCrate;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.util.CheckForcefieldRunnable;
import net.auroramc.lobby.utils.CrateUtil;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuroraMCLobbyPlayer extends AuroraMCPlayer {

    private final long joinTimestamp;
    private long lastPunchTimestamp;
    private long lastClick;

    private long lastDailyBonus;
    private int dailyBonusClaimed;
    private long lastMonthlyBonus;
    private long lastPlusBonus;
    private boolean moved;

    private Map<Gadget, Long> lastUsed;

    private CheckForcefieldRunnable runnable;

    private List<Crate> crates;

    public AuroraMCLobbyPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.joinTimestamp = System.currentTimeMillis();
        lastUsed = new HashMap<>();
        moved = false;
        crates = AuroraMCAPI.getDbManager().getCrates(this.getId());

        if (oldPlayer.getPreferences().isHubForcefieldEnabled() && (oldPlayer.hasPermission("social") || oldPlayer.hasPermission("admin"))) {
            this.runnable = new CheckForcefieldRunnable(this);
            this.runnable.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
        } else {
            this.runnable = null;
        }

        if (System.currentTimeMillis() - oldPlayer.getStats().getFirstJoinTimestamp() > 31536000000L) {
            if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(50))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(50), 1, true);
            }
        }

        if (oldPlayer.hasPermission("elite")) {
            if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(2))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(2), 1, true);
            }
        }

        if (oldPlayer.hasPermission("master")) {
            if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(3))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(3), 1, true);
            }
        }

        if (oldPlayer.hasPermission("plus")) {
            if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(4))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(4), 1, true);
            }
        }

        if (oldPlayer.getLinkedDiscord() != null) {
            if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(15))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(15), 1, true);
            }
        }

        if (oldPlayer.getStats().getLobbyTimeMs() > 18000000) {
            if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(11))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(11), 1, true);
            }
        }

        //Achievement stuff
        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
            if (player.isLoaded()) {
                if (player.hasPermission("social")) {
                    if (!player.isDisguised() && !player.isVanished() && !player.hasPermission("admin")) {
                        if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(17))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(17), 1, true);
                        }
                    }
                }
                if (player.hasPermission("moderation")) {
                    if (!player.isVanished() && !player.isDisguised()) {
                        if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(24))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(24), 1, true);
                        }
                    }
                }
                if (player.hasPermission("build")) {
                    if (!player.isVanished() && !player.isDisguised()) {
                        if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(25))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(25), 1, true);
                        }
                    }
                }
                if (player.hasPermission("admin")) {
                    if (!player.isVanished() && !player.isDisguised()) {
                        if (!getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(26))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(26), 1, true);
                        }
                    }
                }


                //To give it to other people
                if (oldPlayer.hasPermission("social")) {
                    if (!oldPlayer.isDisguised() && !oldPlayer.isVanished() && !oldPlayer.hasPermission("admin")) {
                        if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(17))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(17), 1, true);
                        }
                    }
                }
                if (oldPlayer.hasPermission("moderation")) {
                    if (!oldPlayer.isVanished() && !oldPlayer.isDisguised()) {
                        if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(24))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(24), 1, true);
                        }
                    }
                }
                if (oldPlayer.hasPermission("build")) {
                    if (!oldPlayer.isVanished() && !oldPlayer.isDisguised()) {
                        if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(25))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(25), 1, true);
                        }
                    }
                }
                if (oldPlayer.hasPermission("admin")) {
                    if (!oldPlayer.isVanished() && !oldPlayer.isDisguised()) {
                        if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(26))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(26), 1, true);
                        }
                    }
                }
                if (!player.isDisguised() && !player.isVanished()) {
                    if (oldPlayer.getFriendsList().getFriends().containsKey(player.getPlayer().getUniqueId())) {
                        if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(34))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(34), 1, true);
                        }
                    }
                }
            }
        }

        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 3600000) {
            if (!oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(43))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(43), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 18000000) {
            if (!oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(44))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(44), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 36000000) {
            if (!oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(45))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(45), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 86400000) {
            if (!oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(46))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(46), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 172800000) {
            if (!oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(47))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(47), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 604800000) {
            if (!oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(48))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(48), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 1209600000) {
            if (!oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(49))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(49), 1, true);
            }
        }

        lastDailyBonus = LobbyDatabaseManager.getLastDailyBonus(this.getId());
        dailyBonusClaimed = LobbyDatabaseManager.getLastDailyBonusTotal(this.getId());
        lastMonthlyBonus = LobbyDatabaseManager.getLastMonthlyBonus(this.getId());
        lastPlusBonus = LobbyDatabaseManager.getLastPlusBonus(this.getId());

        new BukkitRunnable(){
            @Override
            public void run() {
                if (getPreferences().isHubSpeedEnabled()) {
                    getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000000, 1, true, false));
                } else {
                    getPlayer().removePotionEffect(PotionEffectType.SPEED);
                }
            }
        }.runTask(AuroraMCAPI.getCore());
    }

    public long getJoinTimestamp() {
        return joinTimestamp;
    }

    public void punched() {
        lastPunchTimestamp = System.currentTimeMillis();
    }

    public boolean canBePunched() {
        return System.currentTimeMillis() - lastPunchTimestamp >= 30000;
    }

    public void click() {
        lastClick = System.currentTimeMillis();
    }

    public boolean canClick() {
        return System.currentTimeMillis() - lastClick >= 500;
    }

    public void claimDaily() {
        getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("The Monke", "You claimed your daily bonus! You got:\n" +
                "&6+100 Crowns\n" +
                "&d+100 Tickets\n" +
                "&a+100 XP"));
        lastDailyBonus = System.currentTimeMillis();
        dailyBonusClaimed++;
        this.getBank().addTickets(100, true, true);
        this.getBank().addCrowns(100, true, true);
        this.getStats().addXp(100, true);
        LobbyDatabaseManager.setLastDailyBonus(this.getId(), lastDailyBonus);
        LobbyDatabaseManager.setLastDailyBonusTotal(this.getId(), dailyBonusClaimed);
    }

    public CheckForcefieldRunnable getRunnable() {
        return runnable;
    }

    public void activateForcefield() {
        if (this.runnable != null) {
            return;
        }
        this.runnable = new CheckForcefieldRunnable(this);
        this.runnable.runTaskTimer(AuroraMCAPI.getCore(), 10, 10);
    }

    public void deactivateForcefield() {
        if (this.runnable != null) {
            this.runnable.cancel();
            this.runnable = null;
        }
    }

    public void claimMonthly() {
        int amount;
        String crates;

        switch (getRank()) {
            case PLAYER: {
                amount = 1000;
                crates = "&7+1 Iron Crate";
                IronCrate crate = CrateUtil.generateIronCrate(getId());
                this.crates.add(crate);
                break;
            }
            case ELITE: {
                amount = 2500;
                crates = "&7+2 Iron Crates\n" +
                        "&6+1 Gold Crate";
                IronCrate crate = CrateUtil.generateIronCrate(getId());
                GoldCrate crate2 = CrateUtil.generateGoldCrate(getId());
                this.crates.add(crate);
                this.crates.add(crate2);
                break;
            }
            default: {
                amount = 5000;
                crates = "&6+1 Gold Crate\n" +
                        "&b+2 Diamond Crate";
                DiamondCrate crate = CrateUtil.generateDiamondCrate(getId());
                this.crates.add(crate);
                crate = CrateUtil.generateDiamondCrate(getId());
                this.crates.add(crate);
                GoldCrate crate2 = CrateUtil.generateGoldCrate(getId());
                this.crates.add(crate2);
                break;
            }
        }

        getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("The Monke", "You claimed your monthly bonus! You got:\n" +
                crates + "\n" +
                "&6+" + String.format("%,d", amount) + "  Crowns\n" +
                "&d+" + String.format("%,d", amount) + " Tickets"));
        lastMonthlyBonus = System.currentTimeMillis();
        this.getBank().addTickets(amount, true, true);
        this.getBank().addCrowns(amount, true, true);
        LobbyDatabaseManager.setLastMonthlyBonus(this.getId(), lastMonthlyBonus);
    }

    public void claimPlus() {
        getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("The Monke", "You claimed your monthly Plus bonus! You got:\n" +
                "&a+1 Emerald Crate\n" +
                "&d+1 Diamond Crate\n" +
                "&6+5,000 Crowns\n" +
                "&d+5,000 Tickets"));
        lastPlusBonus = System.currentTimeMillis();
        EmeraldCrate crate = CrateUtil.generateEmeraldCrate(getId());
        DiamondCrate crate2 = CrateUtil.generateDiamondCrate(getId());
        crates.add(crate);
        crates.add(crate2);
        this.getBank().addTickets(5000, true, true);
        this.getBank().addCrowns(5000, true, true);
        LobbyDatabaseManager.setLastPlusBonus(this.getId(), lastPlusBonus);
    }

    public int getDailyBonusClaimed() {
        return dailyBonusClaimed;
    }

    public long getLastDailyBonus() {
        return lastDailyBonus;
    }

    public long getLastMonthlyBonus() {
        return lastMonthlyBonus;
    }

    public long getLastPlusBonus() {
        return lastPlusBonus;
    }

    public boolean canClaimDaily() {
        if (lastDailyBonus == -1) {
            return true;
        }
        Calendar last = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        last.setTimeInMillis(lastDailyBonus);
        today.setTime(new Date());

        return last.get(Calendar.DATE) != today.get(Calendar.DATE);
    }

    public boolean canClaimMonthly() {
        if (lastMonthlyBonus == -1) {
            return true;
        }
        Calendar last = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        last.setTimeInMillis(lastDailyBonus);
        today.setTime(new Date());

        return last.get(Calendar.YEAR) > today.get(Calendar.YEAR) || (last.get(Calendar.YEAR) == today.get(Calendar.YEAR) && last.get(Calendar.MONTH) != today.get(Calendar.MONTH));
    }

    public boolean canClaimPlus() {
        if (lastPlusBonus == -1 && this.hasPermission("plus")) {
            return true;
        }
        return lastPlusBonus / 2592000000L < System.currentTimeMillis() / 2592000000L  && this.hasPermission("plus");
    }

    public boolean hasMoved() {
        return moved;
    }

    public void moved() {
        this.moved = true;
    }

    public Map<Gadget, Long> getLastUsed() {
        return lastUsed;
    }

    public List<Crate> getCrates() {
        return crates;
    }
}
