/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.backend;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.ServerInfo;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.LobbyAPI;
import net.auroramc.lobby.api.util.Changelog;
import net.auroramc.lobby.api.util.CommunityPoll;
import org.apache.commons.io.FileUtils;
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

public class LobbyDatabaseManager {

    public static void downloadMap() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM maps WHERE parse_version = 'LIVE' AND map_id = 22");
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
                servers.add(new ServerInfo(set.getString(1), set.getString(2), set.getInt(3), ServerInfo.Network.valueOf(set.getString(4)), set.getBoolean(5), new JSONObject(set.getString(6)), set.getInt(7), set.getInt(8), set.getInt(9), set.getInt(10), set.getInt(11), set.getInt(12), set.getString(13)));
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


}
