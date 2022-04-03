/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.players;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.util.CheckForcefieldRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.Date;

public class AuroraMCLobbyPlayer extends AuroraMCPlayer {

    private final long joinTimestamp;
    private long lastPunchTimestamp;
    private long lastMonkeClick;

    private long lastDailyBonus;
    private int dailyBonusClaimed;
    private long lastMonthlyBonus;
    private long lastPlusBonus;
    private boolean moved;

    private CheckForcefieldRunnable runnable;

    public AuroraMCLobbyPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.joinTimestamp = System.currentTimeMillis();
        moved = false;

        if (oldPlayer.getPreferences().isHubForcefieldEnabled()) {
            this.runnable = new CheckForcefieldRunnable(this);
            this.runnable.runTaskTimer(AuroraMCAPI.getCore(), 20, 20);
        } else {
            this.runnable = null;
        }

        if (System.currentTimeMillis() - oldPlayer.getStats().getFirstJoinTimestamp() > 31536000000L) {
            if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(50))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(50), 1, true);
            }
        }

        if (oldPlayer.hasPermission("elite")) {
            if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(2))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(2), 1, true);
            }
        }

        if (oldPlayer.hasPermission("master")) {
            if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(3))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(3), 1, true);
            }
        }

        if (oldPlayer.hasPermission("plus")) {
            if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(4))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(4), 1, true);
            }
        }

        if (oldPlayer.getLinkedDiscord() != null) {
            if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(15))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(15), 1, true);
            }
        }

        if (oldPlayer.getStats().getLobbyTimeMs() > 18000000) {
            if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(11))) {
                getStats().achievementGained(AuroraMCAPI.getAchievement(11), 1, true);
            }
        }

        //Achievement stuff
        for (AuroraMCPlayer player : AuroraMCAPI.getPlayers()) {
            if (player.isLoaded()) {
                if (player.hasPermission("social")) {
                    if (!player.isDisguised() && !player.isVanished() && !player.hasPermission("admin")) {
                        if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(17))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(17), 1, true);
                        }
                        break;
                    }
                }
                if (player.hasPermission("moderation")) {
                    if (!player.isVanished() && !player.isDisguised()) {
                        if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(24))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(24), 1, true);
                        }
                        break;
                    }
                }
                if (player.hasPermission("build")) {
                    if (!player.isVanished() && !player.isDisguised()) {
                        if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(25))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(25), 1, true);
                        }
                        break;
                    }
                }
                if (player.hasPermission("admin")) {
                    if (!player.isVanished() && !player.isDisguised()) {
                        if (getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(26))) {
                            getStats().achievementGained(AuroraMCAPI.getAchievement(26), 1, true);
                        }
                        break;
                    }
                }


                //To give it to other people
                if (oldPlayer.hasPermission("social")) {
                    if (!oldPlayer.isDisguised() && !oldPlayer.isVanished() && !oldPlayer.hasPermission("admin")) {
                        if (player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(17))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(17), 1, true);
                        }
                        break;
                    }
                }
                if (oldPlayer.hasPermission("moderation")) {
                    if (!oldPlayer.isVanished() && !oldPlayer.isDisguised()) {
                        if (player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(24))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(24), 1, true);
                        }
                        break;
                    }
                }
                if (oldPlayer.hasPermission("build")) {
                    if (!oldPlayer.isVanished() && !oldPlayer.isDisguised()) {
                        if (player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(25))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(25), 1, true);
                        }
                        break;
                    }
                }
                if (oldPlayer.hasPermission("admin")) {
                    if (!oldPlayer.isVanished() && !oldPlayer.isDisguised()) {
                        if (player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(26))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(26), 1, true);
                        }
                        break;
                    }
                }
                if (!player.isDisguised() && !player.isVanished()) {
                    if (oldPlayer.getFriendsList().getFriends().containsKey(player.getPlayer().getUniqueId())) {
                        if (player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(34))) {
                            player.getStats().achievementGained(AuroraMCAPI.getAchievement(34), 1, true);
                        }
                    }
                }
            }
        }

        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 3600000) {
            if (oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(43))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(43), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 18000000) {
            if (oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(44))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(44), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 36000000) {
            if (oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(45))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(45), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 86400000) {
            if (oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(46))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(46), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 172800000) {
            if (oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(47))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(47), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 604800000) {
            if (oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(48))) {
                oldPlayer.getStats().achievementGained(AuroraMCAPI.getAchievement(48), 1, true);
            }
        }
        if (oldPlayer.getStats().getLobbyTimeMs() + oldPlayer.getStats().getGameTimeMs() > 1209600000) {
            if (oldPlayer.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(49))) {
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

    public void monkeClick() {
        lastMonkeClick = System.currentTimeMillis();
    }

    public boolean canMonkeClick() {
        return System.currentTimeMillis() - lastMonkeClick >= 500;
    }

    public void claimDaily() {
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
        this.runnable.runTaskTimer(AuroraMCAPI.getCore(), 20, 20);
    }

    public void deactivateForcefield() {
        if (this.runnable != null) {
            this.runnable.cancel();
            this.runnable = null;
        }
    }

    public void claimMonthly() {
        lastMonthlyBonus = System.currentTimeMillis();
        this.getBank().addTickets(10000, true, true);
        this.getBank().addCrowns(10000, true, true);
        LobbyDatabaseManager.setLastMonthlyBonus(this.getId(), lastMonthlyBonus);
    }

    public void claimPlus() {
        lastPlusBonus = System.currentTimeMillis();
        this.getBank().addTickets(10000, true, true);
        this.getBank().addCrowns(10000, true, true);
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
        if (lastPlusBonus == -1) {
            return true;
        }
        return lastPlusBonus / 2592000000L < System.currentTimeMillis() / 2592000000L;
    }

    public boolean hasMoved() {
        return moved;
    }

    public void moved() {
        this.moved = true;
    }
}
