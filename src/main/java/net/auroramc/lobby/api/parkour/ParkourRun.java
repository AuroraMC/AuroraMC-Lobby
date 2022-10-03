/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.parkour;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.lobby.api.parkour.plates.Checkpoint;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Calendar;
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
                previouslyReachedCheckpoints = AuroraMCAPI.getDbManager().getReachedCheckpoints(player, parkour);
                previous = AuroraMCAPI.getDbManager().getTime(player, parkour);
                splitTimes = AuroraMCAPI.getDbManager().getSplitTimes(player, parkour);
            }
        }.runTaskAsynchronously(AuroraMCAPI.getCore());
        actionBarTask = new BukkitRunnable(){
            @Override
            public void run() {
                String message = AuroraMCAPI.getFormatter().pluginMessage(null, "&b&lCurrent Time: &r" + formatTime(System.currentTimeMillis() - startTime) + " - &b&lParkour: &r" + parkour.getName() + "&r - &b&lCurrent Checkpoint: &r#" + lastReached);
                player.sendHotBar(message, ChatColor.WHITE, false);
            }
        }.runTaskTimerAsynchronously(AuroraMCAPI.getCore(), 0, 2);
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
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have reached checkpoint **#" + checkpoint.getCheckpointNo() + "** in **" + formatTime(ms) + "** and beat your personal best of **" + formatTime(splitTimes.get(checkpoint.getCheckpointNo())) + "**!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            AuroraMCAPI.getDbManager().setSplitTime(player, parkour, checkpoint.getCheckpointNo(), ms, true);
                        }
                    }.runTaskAsynchronously(AuroraMCAPI.getCore());
                    splitTimes.put(checkpoint.getCheckpointNo(), ms);
                } else {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have reached checkpoint **#" + checkpoint.getCheckpointNo() + "** in **" + formatTime(ms) + "** (personal best: **" + formatTime(splitTimes.get(checkpoint.getCheckpointNo())) + "**)!"));
                }
            } else {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have reached checkpoint **#" + checkpoint.getCheckpointNo() + "** in **" + formatTime(ms) + "**!"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AuroraMCAPI.getDbManager().setSplitTime(player, parkour, checkpoint.getCheckpointNo(), ms, false);
                    }
                }.runTaskAsynchronously(AuroraMCAPI.getCore());
                splitTimes.put(checkpoint.getCheckpointNo(), ms);
            }
            checkpoints.add(checkpoint);

            //Give checkpoint reward if not already reached.
            if (!previouslyReachedCheckpoints.contains(checkpoint)) {
                if (parkour.getCheckpointCommand() != null) {
                    parkour.getCheckpointCommand().apply(player);
                }
            }
            previouslyReachedCheckpoints.add(checkpoint);

            new BukkitRunnable() {
                @Override
                public void run() {
                   AuroraMCAPI.getDbManager().reachedCheckpoint(player, parkour, checkpoint);
                }
            }.runTaskAsynchronously(AuroraMCAPI.getCore());

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
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You are not allowed to fly while doing the parkour. Parkour failed!"));
                    break;
                case TELEPORTATION:
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You are not allowed to teleport while doing the parkour. Parkour failed!"));
                    break;
                case NEW_PARKOUR:
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have started another parkour, parkour failed!"));
                    break;
            }
            long time = System.currentTimeMillis() - startTime;
            new BukkitRunnable(){
                @Override
                public void run() {
                    AuroraMCAPI.getDbManager().addAttempt(player, parkour, time);
                }
            }.runTaskAsynchronously(AuroraMCAPI.getCore());
        } else {
            if (checkpoints.size() != parkour.getNoCheckpoints()) {
                if (actionBarTask != null) {
                    actionBarTask.cancel();
                    actionBarTask = null;
                }

                long time = System.currentTimeMillis() - startTime;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AuroraMCAPI.getDbManager().addAttempt(player, parkour, time);
                    }
                }.runTaskAsynchronously(AuroraMCAPI.getCore());

                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You did not reach enough checkpoints, parkour failed!"));
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


            int check = 0;

            if (checkpoints.size() > 0) {
                check = checkpoints.get(checkpoints.size() - 1).getCheckpointNo() + 1;
            }

            if (splitTimes.containsKey(check)) {
                long oldSplit = splitTimes.get(check);
                if (oldSplit > splitMs) {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have reached the finish point in **" + formatTime(splitMs) + "** and beat your personal best of **" + formatTime(oldSplit) + "**!"));
                    int finalCheck = check;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            AuroraMCAPI.getDbManager().setSplitTime(player, parkour, finalCheck, splitMs, true);
                        }
                    }.runTaskAsynchronously(AuroraMCAPI.getCore());
                } else {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have reached the finish point in **" + formatTime(splitMs) + "** (personal best: **" + formatTime(oldSplit) + "**)!"));
                }
            } else {
                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You have reached the finish point in **" + formatTime(splitMs) + "**!"));
                int finalCheck = check;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        AuroraMCAPI.getDbManager().setSplitTime(player, parkour, finalCheck, splitMs, false);
                    }
                }.runTaskAsynchronously(AuroraMCAPI.getCore());
            }

            if (previous > 0) {
                if (finishMili < previous) {

                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You beat your previous record and you managed to complete the **" + parkour.getName() + " **parkour in **" + formatTime(finishMili) + "**!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            AuroraMCAPI.getDbManager().newTime(player, finishMili, true, parkour);
                            int position = AuroraMCAPI.getDbManager().leaderboardPosition(player, parkour);

                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You are in &a" + position + ((position % 10 == 1) ? "st" : ((position % 10 == 2) ? "nd" : ((position % 10 == 3) ? ((position == 13) ? "th" : "rd") : "th"))) + " place** for the **" + parkour.getName() + "** parkour!"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    parkour.getLeaderboard().refresh();
                                }
                            }.runTask(AuroraMCAPI.getCore());
                        }
                    }.runTaskAsynchronously(AuroraMCAPI.getCore());
                } else {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You didn't beat your previous record, but you managed to complete the **" + parkour.getName() + " **parkour in **{time} **!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            int position = AuroraMCAPI.getDbManager().leaderboardPosition(player, parkour);
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You are in &a" + position + ((position % 10 == 1) ? "st" : ((position % 10 == 2) ? "nd" : ((position % 10 == 3) ? ((position == 13) ? "th" : "rd") : "th"))) + " place** for the **" + parkour.getName() + "** parkour!"));
                        }
                    }.runTaskAsynchronously(AuroraMCAPI.getCore());
                }
            } else {
                if (previous == -1) {
                    if (parkour.getEndCommand() != null) {
                        long timestamp = System.currentTimeMillis();
                        parkour.getEndCommand().apply(player);
                    }

                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "Well done! You completed the **" + parkour.getName() + " **parkour in **" + formatTime(finishMili) + "**! Your reward will be applied shortly!"));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            AuroraMCAPI.getDbManager().newTime(player, finishMili, false, parkour);
                            int position = AuroraMCAPI.getDbManager().leaderboardPosition(player, parkour);
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You are in &a" + position + ((position % 10 == 1) ? "st" : ((position % 10 == 2) ? "nd" : ((position % 10 == 3) ? ((position == 13) ? "th" : "rd") : "th"))) + " place** for the **" + parkour.getName() + "** parkour!"));
                        }
                    }.runTaskAsynchronously(AuroraMCAPI.getCore());
                } else {
                    player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Parkour", "You completed the parkour too quickly, parkour failed!"));
                }
            }
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

    /**
     * Executed when the player travels when doing the parkour.
     * @param distance the distance travelled.
     */
    public void addTravel(double distance) {
        this.totalDistanceTravelled += distance;
    }

    public enum FailCause {FLY, TELEPORTATION, NOT_ENOUGH_CHECKPOINTS, NEW_PARKOUR, LEAVE}

}
