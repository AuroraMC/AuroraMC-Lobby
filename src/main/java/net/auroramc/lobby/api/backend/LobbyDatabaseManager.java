/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.backend;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.ServerInfo;
import net.auroramc.core.api.cosmetics.Crate;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.parkour.Parkour;
import net.auroramc.lobby.api.parkour.plates.Checkpoint;
import net.auroramc.lobby.api.players.AuroraMCLobbyPlayer;
import net.auroramc.lobby.api.util.Changelog;
import net.auroramc.lobby.api.util.CommunityPoll;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class LobbyDatabaseManager {

    public static void downloadMap() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM maps WHERE parse_version = '" + ((AuroraMCAPI.isTestServer())?"TEST":"LIVE") + "' AND map_id = 54");
            ResultSet set = statement.executeQuery();
            File file = new File(LobbyAPI.getLobby().getDataFolder(), "zip");
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
            file.mkdirs();
            if (set.next()) {
                File zipFile = new File(file, set.getInt(2) + ".zip");
                FileOutputStream output = new FileOutputStream(zipFile);

                System.out.println("Writing to file " + zipFile.getAbsolutePath());
                InputStream input = set.getBinaryStream(7);
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
                output.flush();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getVersionNumbers() {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            return connection.hgetAll("versionnumbers");
        }
    }

    public static Map<String, List<Changelog>> getChangelogs() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            Map<String, List<Changelog>> changelogs = new HashMap<>();

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM changelogs ORDER BY timestamp desc");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                if (changelogs.containsKey(set.getString(2))) {
                    changelogs.get(set.getString(2)).add(new Changelog(set.getInt(1), set.getString(2), set.getString(3), set.getLong(4), set.getString(5), set.getString(6)));
                } else {
                    List<Changelog> logs = new ArrayList<>();
                    logs.add(new Changelog(set.getInt(1), set.getString(2), set.getString(3), set.getLong(4), set.getString(5), set.getString(6)));
                    changelogs.put(set.getString(2), logs);
                }
            }

            return changelogs;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static Changelog getLatestChangelog() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {

            PreparedStatement statement = connection.prepareStatement("SELECT * FROM changelogs ORDER BY timestamp desc LIMIT 1");
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                return new Changelog(set.getInt(1), set.getString(2), set.getString(3), set.getLong(4), set.getString(5), set.getString(6));
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setVersionNumber(String gameKey, String version) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.hset("versionnumbers", gameKey, version);
        }
    }

    public static String fetchData(ServerInfo info) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            if (connection.exists("serverdata." + info.getNetwork().name() + "." + info.getName())) {
                return connection.get("serverdata." + info.getNetwork() + "." + info.getName());
            } else {
                return "IDLE;0/0;N/A;N/A";
            }
        }
    }

    public static List<ServerInfo> getServers() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM servers WHERE network = ?");
            statement.setString(1, AuroraMCAPI.getServerInfo().getNetwork().name());
            ResultSet set = statement.executeQuery();

            ArrayList<ServerInfo> servers = new ArrayList<>();
            while (set.next()) {
                servers.add(new ServerInfo(set.getString(1), set.getString(2), set.getInt(3), ServerInfo.Network.valueOf(set.getString(4)), set.getBoolean(5), new JSONObject(set.getString(6)), set.getInt(7), set.getInt(8), set.getInt(9), set.getInt(10), set.getInt(11), set.getInt(12), set.getInt(13), set.getString(14)));
            }

            return servers;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateServerData() {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.set("serverdata." + AuroraMCAPI.getServerInfo().getNetwork().name() + "." + AuroraMCAPI.getServerInfo().getName(), "IDLE;" + AuroraMCAPI.getPlayers().stream().filter(player -> !player.isVanished()).count() + "/" + AuroraMCAPI.getServerInfo().getServerType().getInt("max_players") + ";Lobby;Lobby");
            connection.expire("serverdata." + AuroraMCAPI.getServerInfo().getNetwork().name() + "." + AuroraMCAPI.getServerInfo().getName(), 15);
        }
    }

    public static long getLastDailyBonus(int id) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            if (connection.hexists("bonuses." + id, "daily")) {
                return Long.parseLong(connection.hget("bonuses." + id, "daily"));
            } else {
                return -1;
            }
        }
    }
    public static void setLastDailyBonus(int id, long time) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.hset("bonuses." + id, "daily", time + "");
        }
    }

    public static int getLastDailyBonusTotal(int id) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            if (connection.hexists("bonuses." + id, "dailytotal")) {
                return Integer.parseInt(connection.hget("bonuses." + id, "dailytotal"));
            } else {
                return 0;
            }
        }
    }

    public static void setLastDailyBonusTotal(int id, int total) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.hset("bonuses." + id, "dailytotal", total + "");
        }
    }

    public static long getLastMonthlyBonus(int id) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            if (connection.hexists("bonuses." + id, "monthly")) {
                return Long.parseLong(connection.hget("bonuses." + id, "monthly"));
            } else {
                return -1;
            }
        }
    }

    public static void setLastMonthlyBonus(int id, long time) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.hset("bonuses." + id, "monthly", time + "");
        }
    }

    public static long getLastPlusBonus(int id) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            if (connection.hexists("bonuses." + id, "plus")) {
                return Long.parseLong(connection.hget("bonuses." + id, "plus"));
            } else {
                return -1;
            }
        }
    }

    public static void setLastPlusBonus(int id, long time) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.hset("bonuses." + id, "plus", time + "");
        }
    }

    public static CommunityPoll getPoll() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM polls");
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                if (set.getLong(3) > System.currentTimeMillis()) {
                    statement = connection.prepareStatement("SELECT * FROM poll_answers WHERE poll_id = ?");
                    statement.setInt(1, set.getInt(1));

                    ResultSet set1 = statement.executeQuery();

                    Map<Integer, CommunityPoll.PollAnswer> answers = new HashMap<>();

                    while (set1.next()) {
                        answers.put(set1.getInt(1), new CommunityPoll.PollAnswer(set1.getInt(1), set1.getString(3)));
                    }

                    Map<Integer, Long> responses = new HashMap<>();
                    try (Jedis jedis = AuroraMCAPI.getDbManager().getRedisConnection()) {
                        Map<String, String> response = jedis.hgetAll("responses." + set.getString(1));
                        for (Map.Entry<String, String> entry : response.entrySet()) {
                            responses.put(Integer.parseInt(entry.getKey()), Long.parseLong(entry.getValue()));
                        }
                    }

                    return new CommunityPoll(set.getInt(1), set.getString(2), answers, responses, set.getLong(3));
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean hasVoted(int pollId, int playerId) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            return connection.exists("poll." + pollId + "." + playerId);
        }
    }

    public static void setVote(int pollId, int playerId, int responseId) {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.set("poll." + pollId + "." + playerId, responseId + "");
            connection.hincrBy("responses." + pollId, responseId + "", 1);
        }
    }

    public static void newCrate(Crate crate) {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO crates(uuid, type, amc_id) VALUES (?,?,?)");
            statement.setString(1, crate.getUuid().toString());
            statement.setString(2, crate.getType());
            statement.setInt(3, crate.getOwner());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Checkpoint> getReachedCheckpoints(AuroraMCLobbyPlayer player, Parkour parkour) {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM pk_reachedcheckpoints WHERE parkour_id = ? AND id = ?");
            statement.setInt(2, player.getId());
            statement.setInt(1, parkour.getId());

            ResultSet resultSet = statement.executeQuery();

            List<Checkpoint> checkpoints = new ArrayList<>();
            while (resultSet.next()) {
                checkpoints.add(parkour.getCheckpoint(resultSet.getInt(3)));
            }
            return checkpoints;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void reachedCheckpoint(AuroraMCLobbyPlayer player, Parkour parkour, Checkpoint checkpoint) {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO pk_reachedcheckpoints VALUES (?, ?, ?)");
            statement.setInt(1, player.getId());
            statement.setInt(2, parkour.getId());
            statement.setInt(3, checkpoint.getCheckpointNo());

            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getTime(AuroraMCLobbyPlayer player, Parkour parkour) {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM pk_playertimes WHERE parkour_id = ? AND id = ?");
            statement.setInt(2, player.getId());
            statement.setInt(1, parkour.getId());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getLong(3);
            } else {
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @SuppressWarnings("unused")
    public static void newTime(AuroraMCLobbyPlayer player, long time, boolean beatBefore, Parkour parkour) {
        if (beatBefore) {
            try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
                PreparedStatement statement = connection.prepareStatement("UPDATE pk_playertimes SET time = ? WHERE id = ? AND parkour_id = ?");

                statement.setLong(1, time);
                statement.setInt(2, player.getId());
                statement.setInt(3, parkour.getId());

                boolean result = statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO pk_playertimes(id, parkour_id, time, name) values (?,?,?,?)");

                statement.setInt(1, player.getId());
                statement.setInt(2, parkour.getId());
                statement.setLong(3, time);
                statement.setString(4, player.getName());

                boolean result = statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<Integer, Long> getSplitTimes(AuroraMCLobbyPlayer player, Parkour parkour) {
        HashMap<Integer, Long> splitTimes = new HashMap<>();
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM pk_splittimes WHERE id = ? AND parkour_id = ?");
            statement.setInt(1, player.getId());
            statement.setInt(2, parkour.getId());

            ResultSet set = statement.executeQuery();
            while (set.next()) {
                splitTimes.put(set.getInt(3), set.getLong(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return splitTimes;
    }

    public static void setSplitTime(AuroraMCLobbyPlayer player, Parkour parkour, int checkpoint, long time, boolean reachedBefore) {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement;
            if (reachedBefore) {
                statement = connection.prepareStatement("UPDATE pk_splittimes SET time = ? WHERE id = ? AND parkour_id = ? AND checkpoint = ?");
                statement.setLong(1, time);
                statement.setInt(2, parkour.getId());
                statement.setInt(3, parkour.getId());
                statement.setInt(4, checkpoint);
            } else {
                statement = connection.prepareStatement("INSERT INTO pk_splittimes VALUES(?, ?, ?, ?, ?)");
                statement.setString(1, player.getPlayer().getUniqueId().toString());
                statement.setInt(2, parkour.getId());
                statement.setInt(3, checkpoint);
                statement.setLong(4, time);
                statement.setString(5, player.getName());
            }

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addAttempt(AuroraMCLobbyPlayer player, Parkour parkour, long time) {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM pk_stats WHERE parkour_id = ? AND id = ?");
            statement.setInt(1, parkour.getId());
            statement.setInt(2, player.getId());
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                statement = connection.prepareStatement("UPDATE pk_stats SET attempts = attempts + 1, total_time = (total_time + ?), checkpoints = checkpoints + ?, jumps = jumps + ?, distance = distance + ? WHERE parkour_id = ? AND id = ?");
                statement.setLong(1, time);
                statement.setInt(2, player.getActiveParkourRun().getCheckpointsHit());
                statement.setInt(3, player.getActiveParkourRun().getJumps());
                statement.setDouble(4, player.getActiveParkourRun().getTotalDistanceTravelled());
                statement.setInt(5, parkour.getId());
                statement.setInt(6, player.getId());
                statement.execute();
            } else {
                statement = connection.prepareStatement("INSERT INTO pk_stats VALUES (?, ?, 0, 1, ?, ?, ?, ?)");
                statement.setInt(1, player.getId());
                statement.setInt(2, parkour.getId());
                statement.setLong(6, time);
                statement.setInt(4, player.getActiveParkourRun().getCheckpointsHit());
                statement.setInt(3, player.getActiveParkourRun().getJumps());
                statement.setDouble(5, player.getActiveParkourRun().getTotalDistanceTravelled());
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int leaderboardPosition(AuroraMCLobbyPlayer player, Parkour parkour) {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM pk_playertimes WHERE parkour_id = ? ORDER BY `time` ASC ");
            statement.setInt(1, parkour.getId());

            ResultSet results = statement.executeQuery();
            int counter = 1;
            while (results.next()) {
                if (results.getInt(1) == player.getId()) {
                    return counter;
                } else {
                    counter++;
                }
            }
        } catch (SQLException e) {
            return -1;
        }
        return -1;
    }

    public static HashMap<Integer, List<String>> getLeaderboard(Parkour parkour) {
        HashMap<Integer, List<String>> leaderboard = new HashMap<>();

        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM pk_playertimes WHERE parkour_id = ? ORDER BY `time` ASC LIMIT 10");
            statement.setInt(1, parkour.getId());

            ResultSet results = statement.executeQuery();
            int counter = 1;
            while (results.next()) {
                List<String> record = new ArrayList<>();
                record.add(results.getString(4));
                record.add(results.getString(3));
                record.add(results.getString(1));
                leaderboard.put(counter, record);
                counter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return leaderboard;
    }

}
