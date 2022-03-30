/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.players;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;

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

    public AuroraMCLobbyPlayer(AuroraMCPlayer oldPlayer) {
        super(oldPlayer);
        this.joinTimestamp = System.currentTimeMillis();

        lastDailyBonus = LobbyDatabaseManager.getLastDailyBonus(this.getId());
        dailyBonusClaimed = LobbyDatabaseManager.getLastDailyBonusTotal(this.getId());
        lastMonthlyBonus = LobbyDatabaseManager.getLastMonthlyBonus(this.getId());
        lastPlusBonus = LobbyDatabaseManager.getLastPlusBonus(this.getId());
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

        return last.get(Calendar.YEAR) > today.get(Calendar.YEAR) || (last.get(Calendar.YEAR) == today.get(Calendar.YEAR) && (last.get(Calendar.MONTH) > today.get(Calendar.MONTH) || (last.get(Calendar.MONTH) == today.get(Calendar.MONTH) && last.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))));
    }

    public boolean canClaimMonthly() {
        if (lastMonthlyBonus == -1) {
            return true;
        }
        Calendar last = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        last.setTimeInMillis(lastDailyBonus);
        today.setTime(new Date());

        return last.get(Calendar.YEAR) > today.get(Calendar.YEAR) || (last.get(Calendar.YEAR) == today.get(Calendar.YEAR) && last.get(Calendar.MONTH) > today.get(Calendar.MONTH));
    }

    public boolean canClaimPlus() {
        if (lastPlusBonus == -1) {
            return true;
        }
        return lastPlusBonus / 2592000000L < System.currentTimeMillis() / 2592000000L;
    }
}
