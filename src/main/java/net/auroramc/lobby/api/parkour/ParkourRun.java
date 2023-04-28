/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.parkour;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.lobby.api.backend.LobbyDatabaseManager;
import net.auroramc.lobby.api.parkour.plates.Checkpoint;
import net.auroramc.lobby.api.player.AuroraMCLobbyPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParkourRun {

    private final AuroraMCLobbyPlayer player;
    private final Parkour parkour;
    private final List<Checkpoint> checkpoints = new ArrayList<>();
    private long currentSplit;
    private Map<Integer, Long> splitTimes;
    private int lastReached = 0;
    private long startTime;
    private long previous = -2;
    private List<Checkpoint> previouslyReachedCheckpoints;
    private int jumps;
    private int checkpointsHit;
    private double totalDistanceTravelled;
    private boolean touchedGround;
    private BukkitTask actionBarTask;

    public ParkourRun(AuroraMCLobbyPlayer player, Parkour parkour) {
        this.player = player;
        this.parkour = parkour;
        jumps = 0;
        checkpointsHit = 0;
        totalDistanceTravelled = 0.0d;
        startTime = System.currentTimeMillis();
        touchedGround = true;
        currentSplit = startTime;
        new BukkitRunnable() {
            @Override
            public void run() {
                previouslyReachedCheckpoints = LobbyDatabaseManager.getReachedCheckpoints(player, parkour);
                previous = LobbyDatabaseManager.getTime(player, parkour);
                splitTimes = LobbyDatabaseManager.getSplitTimes(player, parkour);
            }
        }.runTaskAsynchronously(ServerAPI.getCore());
        actionBarTask = new BukkitRunnable(){
            @Override
            public void run() {
                BaseComponent message = new TextComponent(TextFormatter.convert("&b&lCurrent Time: &r" + formatTime(System.currentTimeMillis() - startTime) + " - &b&lParkour: &r" + parkour.getName() + "&r - &b&lCurrent Checkpoint: &r#" + lastReached));
                player.sendHotBar(message);
            }
        }.runTaskTimerAsynchronously(ServerAPI.getCore(), 0, 2);
    }

    public void checkpoint(Checkpoint checkpoint) {
        if (lastReached == checkpoint.getCheckpointNo()) {
            currentSplit = System.currentTimeMillis();
            return;
        }
        if (!checkpoints.contains(checkpoint)) {
            lastReached = checkpoint.getCheckpointNo();
            checkpointsHit++;
            long ms = System.currentTimeMillis() - currentSplit;
            if (splitTimes.containsKey(checkpoint.getCheckpointNo())) {
                if (splitTimes.get(checkpoint.getCheckpointNo()) > ms) {
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have reached checkpoint **#" + checkpoint.getCheckpointNo() + "** in **" + formatTime(ms) + "** and beat your personal best of **" + formatTime(splitTimes.get(checkpoint.getCheckpointNo())) + "**!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            LobbyDatabaseManager.setSplitTime(player, parkour, checkpoint.getCheckpointNo(), ms, true);
                        }
                    }.runTaskAsynchronously(ServerAPI.getCore());
                    splitTimes.put(checkpoint.getCheckpointNo(), ms);
                } else {
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have reached checkpoint **#" + checkpoint.getCheckpointNo() + "** in **" + formatTime(ms) + "** (personal best: **" + formatTime(splitTimes.get(checkpoint.getCheckpointNo())) + "**)!"));
                }
            } else {
                player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have reached checkpoint **#" + checkpoint.getCheckpointNo() + "** in **" + formatTime(ms) + "**!"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        LobbyDatabaseManager.setSplitTime(player, parkour, checkpoint.getCheckpointNo(), ms, false);
                    }
                }.runTaskAsynchronously(ServerAPI.getCore());
                splitTimes.put(checkpoint.getCheckpointNo(), ms);
            }
            checkpoints.add(checkpoint);

            //Give checkpoint reward if not already reached.
            if (!previouslyReachedCheckpoints.contains(checkpoint)) {
                if (parkour.getCheckpointCommand() != null) {
                    parkour.getCheckpointCommand().apply(player);
                    player.sendMessage(new TextComponent(TextFormatter.convert(parkour.getCheckpointCommand().getRewardString())));
                }
            }
            previouslyReachedCheckpoints.add(checkpoint);

            new BukkitRunnable() {
                @Override
                public void run() {
                   LobbyDatabaseManager.reachedCheckpoint(player, parkour, checkpoint);
                }
            }.runTaskAsynchronously(ServerAPI.getCore());

        }
    }

    public void end(FailCause cause) {
        if (cause != null) {
            if (actionBarTask != null) {
                actionBarTask.cancel();
                actionBarTask = null;
            }
            switch (cause) {
                case FLY:
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You are not allowed to fly while doing the parkour. Parkour failed!"));
                    break;
                case TELEPORTATION:
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You are not allowed to teleport while doing the parkour. Parkour failed!"));
                    break;
                case NEW_PARKOUR:
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have started another parkour, parkour failed!"));
                    break;
            }
            long time = System.currentTimeMillis() - startTime;
            player.getStats().incrementStatistic(0, "pktime", time, true);
            player.getStats().incrementStatistic(0, "pkcheckpoints", checkpointsHit, true);
            player.getStats().incrementStatistic(0, "pkjumps", jumps, true);
            player.getStats().addProgress(AuroraMCAPI.getAchievement(203), jumps, player.getStats().getAchievementsGained().getOrDefault(AuroraMCAPI.getAchievement(203),0),true);
            player.getStats().incrementStatistic(0, "pkdistance", Math.round(totalDistanceTravelled * 10), true);
            if (player.getStats().getStatistic(0, "pktime") > 18000000 && !player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(208))) {
                player.getStats().achievementGained(AuroraMCAPI.getAchievement(208), 1, true);
            }
        } else {
            if (checkpoints.size() != parkour.getNoCheckpoints()) {
                if (actionBarTask != null) {
                    actionBarTask.cancel();
                    actionBarTask = null;
                }

                long time = System.currentTimeMillis() - startTime;
                player.getStats().incrementStatistic(0, "pktime", time, true);
                player.getStats().incrementStatistic(0, "pkcheckpoints", checkpointsHit, true);
                player.getStats().incrementStatistic(0, "pkjumps", jumps, true);
                player.getStats().addProgress(AuroraMCAPI.getAchievement(203), jumps, player.getStats().getAchievementsGained().getOrDefault(AuroraMCAPI.getAchievement(203),0),true);
                player.getStats().incrementStatistic(0, "pkdistance", Math.round(totalDistanceTravelled * 10), true);
                if (player.getStats().getStatistic(0, "pktime") > 18000000 && !player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(208))) {
                    player.getStats().achievementGained(AuroraMCAPI.getAchievement(208), 1, true);
                }

                player.sendMessage(TextFormatter.pluginMessage("Parkour", "You did not reach enough checkpoints, parkour failed!"));
                parkour.playerEnd(player);
                player.parkourEnd();
                return;
            }
            long finishMili = System.currentTimeMillis() - startTime;
            long splitMs = System.currentTimeMillis() - currentSplit;
            if (actionBarTask != null) {
                actionBarTask.cancel();
                actionBarTask = null;
            }

            long time = System.currentTimeMillis() - startTime;
            player.getStats().incrementStatistic(0, "pktime", time, true);
            player.getStats().incrementStatistic(0, "pkcheckpoints", checkpointsHit, true);
            player.getStats().incrementStatistic(0, "pkjumps", jumps, true);
            player.getStats().addProgress(AuroraMCAPI.getAchievement(203), jumps, player.getStats().getAchievementsGained().getOrDefault(AuroraMCAPI.getAchievement(203),0),true);
            player.getStats().incrementStatistic(0, "pkdistance", Math.round(totalDistanceTravelled * 10), true);
            if (player.getStats().getStatistic(0, "pktime") > 18000000 && !player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(208))) {
                player.getStats().achievementGained(AuroraMCAPI.getAchievement(208), 1, true);
            }


            int check = 0;

            if (checkpoints.size() > 0) {
                check = checkpoints.get(checkpoints.size() - 1).getCheckpointNo() + 1;
            }

            if (splitTimes.containsKey(check)) {
                long oldSplit = splitTimes.get(check);
                if (oldSplit > splitMs) {
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have reached the finish point (split-time) in **" + formatTime(splitMs) + "** and beat your personal best of **" + formatTime(oldSplit) + "**!"));
                    int finalCheck = check;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            LobbyDatabaseManager.setSplitTime(player, parkour, finalCheck, splitMs, true);
                        }
                    }.runTaskAsynchronously(ServerAPI.getCore());
                } else {
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have reached the finish point (split-time) in **" + formatTime(splitMs) + "** (personal best: **" + formatTime(oldSplit) + "**)!"));
                }
            } else {
                player.sendMessage(TextFormatter.pluginMessage("Parkour", "You have reached the finish point (split-time) in **" + formatTime(splitMs) + "**!"));
                int finalCheck = check;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        LobbyDatabaseManager.setSplitTime(player, parkour, finalCheck, splitMs, false);
                    }
                }.runTaskAsynchronously(ServerAPI.getCore());
            }

            if (previous > 0) {
                if (finishMili < previous) {
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You beat your previous record and you managed to complete the **" + TextFormatter.convert(parkour.getName()) + "** in **" + formatTime(finishMili) + "**!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            LobbyDatabaseManager.newTime(player, finishMili, true, parkour);
                            int position = LobbyDatabaseManager.leaderboardPosition(player, parkour);

                            player.sendMessage(TextFormatter.pluginMessage("Parkour", "You are in **" + position + ((position % 10 == 1) ? "st" : ((position % 10 == 2) ? "nd" : ((position % 10 == 3) ? ((position == 13) ? "th" : "rd") : "th"))) + " place** for the **" + TextFormatter.convert(parkour.getName()) + "**!"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    parkour.getLeaderboard().refresh();
                                }
                            }.runTask(ServerAPI.getCore());
                        }
                    }.runTaskAsynchronously(ServerAPI.getCore());
                    switch (parkour.getId()) {
                        case 1: {
                            if (finishMili <= 30000) {
                                if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(204))) {
                                    player.getStats().achievementGained(AuroraMCAPI.getAchievement(204), 1, true);
                                }
                            }
                            break;
                        }
                        case 2: {
                            if (finishMili <= 150000) {
                                if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(205))) {
                                    player.getStats().achievementGained(AuroraMCAPI.getAchievement(205), 1, true);
                                }
                            }
                            break;
                        }
                        case 3: {
                            if (finishMili <= 330000) {
                                if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(206))) {
                                    player.getStats().achievementGained(AuroraMCAPI.getAchievement(206), 1, true);
                                }
                            }
                            break;
                        }
                    }
                } else {
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You didn't beat your previous record, but you managed to complete the **" + TextFormatter.convert(parkour.getName()) + "** in **" + formatTime(finishMili) + "**!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int position = LobbyDatabaseManager.leaderboardPosition(player, parkour);
                            player.sendMessage(TextFormatter.pluginMessage("Parkour", "You are in **" + position + ((position % 10 == 1) ? "st" : ((position % 10 == 2) ? "nd" : ((position % 10 == 3) ? ((position == 13) ? "th" : "rd") : "th"))) + " place** for the **" + TextFormatter.convert(parkour.getName()) + "**!"));
                        }
                    }.runTaskAsynchronously(ServerAPI.getCore());
                }
                switch (parkour.getId()) {
                    case 1: {
                        if (finishMili <= 30000) {
                            if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(204))) {
                                player.getStats().achievementGained(AuroraMCAPI.getAchievement(204), 1, true);
                            }
                        }
                        break;
                    }
                    case 2: {
                        if (finishMili <= 150000) {
                            if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(205))) {
                                player.getStats().achievementGained(AuroraMCAPI.getAchievement(205), 1, true);
                            }
                        }
                        break;
                    }
                    case 3: {
                        if (finishMili <= 240000) {
                            if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(206))) {
                                player.getStats().achievementGained(AuroraMCAPI.getAchievement(206), 1, true);
                            }
                        }
                        break;
                    }
                }
            } else {
                if (previous == -1) {
                    player.getStats().incrementStatistic(0, "pkpks", 1, true);
                    if (parkour.getEndCommand() != null) {
                        parkour.getEndCommand().apply(player);
                        player.sendMessage(new TextComponent(TextFormatter.convert(parkour.getEndCommand().getRewardString())));
                    }

                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "Well done! You completed the **" + TextFormatter.convert(parkour.getName()) + "** in **" + formatTime(finishMili) + "**! Your reward will be applied shortly!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            LobbyDatabaseManager.newTime(player, finishMili, false, parkour);
                            int position = LobbyDatabaseManager.leaderboardPosition(player, parkour);
                            player.sendMessage(TextFormatter.pluginMessage("Parkour", "You are in **" + position + ((position % 10 == 1) ? "st" : ((position % 10 == 2) ? "nd" : ((position % 10 == 3) ? ((position == 13) ? "th" : "rd") : "th"))) + " place** for the **" + TextFormatter.convert(parkour.getName()) + "**!"));
                        }
                    }.runTaskAsynchronously(ServerAPI.getCore());
                    switch (parkour.getId()) {
                        case 1: {
                            if (finishMili <= 30000) {
                                if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(204))) {
                                    player.getStats().achievementGained(AuroraMCAPI.getAchievement(204), 1, true);
                                }
                            }
                            break;
                        }
                        case 2: {
                            if (finishMili <= 150000) {
                                if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(205))) {
                                    player.getStats().achievementGained(AuroraMCAPI.getAchievement(205), 1, true);
                                }
                            }
                            break;
                        }
                        case 3: {
                            if (finishMili <= 330000) {
                                if (!player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(206))) {
                                    player.getStats().achievementGained(AuroraMCAPI.getAchievement(206), 1, true);
                                }
                            }
                            break;
                        }
                    }
                } else {
                    player.sendMessage(TextFormatter.pluginMessage("Parkour", "You completed the parkour too quickly, parkour failed!"));
                }
            }
        }
        if (player.getStats().getStatistic(0, "pkpks") >= 3 && !player.getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(19))) {
            player.getStats().achievementGained(AuroraMCAPI.getAchievement(19), 1, true);
        }
        parkour.playerEnd(player);
        player.parkourEnd();
    }

    private static String formatTime(long ms) {
        long minutes, seconds;
        minutes = ms / 60000;
        ms -= (minutes * 60000);

        seconds = ms / 1000;
        ms -= (seconds * 1000);

        return ((minutes > 0)?minutes + "m ":"") + seconds + "." + ((ms < 100)?((ms < 10)?"00":"0"):"") + ms + "s";
    }

    public Parkour getParkour() {
        return parkour;
    }

    /**
     * Get the total distance travelled this run.
     * @return the total distance travelled this run.
     */
    public double getTotalDistanceTravelled() {
        return totalDistanceTravelled;
    }

    /**
     * Get the player who is doing this parkour run.
     * @return the player doing this parkour run.
     */
    public AuroraMCLobbyPlayer getPlayer() {
        return player;
    }

    /**
     * Get the amount of checkpoints the player has hit this run.
     * @return the amount of checkpoints.
     */
    public int getCheckpointsHit() {
        return checkpointsHit;
    }

    /**
     * Get the amount of times the player has jumped in this run.
     * @return the amount of jumps.
     */
    public int getJumps() {
        return jumps;
    }

    /**
     * Executed when a checkpoint is hit.
     */
    public void checkpointHit() {
        this.checkpointsHit++;
    }

    /**
     * Executed when the player jumps.
     */
    public void jumped() {
        this.jumps++;
    }

    public int getLastReached() {
        return lastReached;
    }

    public void restart() {
        startTime = System.currentTimeMillis();
        checkpoints.clear();
        lastReached = 0;
        currentSplit = startTime;
    }

    /**
     * Executed when the player travels when doing the parkour.
     * @param distance the distance travelled.
     */
    public void addTravel(double distance) {
        this.totalDistanceTravelled += distance;
    }

    public enum FailCause {FLY, TELEPORTATION, NOT_ENOUGH_CHECKPOINTS, NEW_PARKOUR, LEAVE}

    public boolean hasTouchedGround() {
        return touchedGround;
    }

    public void touchedGround() {
        touchedGround = true;
    }

    public void leftGround() {
        touchedGround = false;
    }

}
