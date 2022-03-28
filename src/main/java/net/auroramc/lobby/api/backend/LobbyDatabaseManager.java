/*
 * Copyright (c) 2022 AuroraMC Ltd. All Rights Reserved.
 */

package net.auroramc.lobby.api.backend;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.ServerInfo;
import net.auroramc.lobby.AuroraMCLobby;
import net.auroramc.lobby.api.LobbyAPI;
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
                File zipFile = new File(file, set.getInt(1) + ".zip");
                FileOutputStream output = new FileOutputStream(zipFile);

                System.out.println("Writing to file " + zipFile.getAbsolutePath());
                InputStream input = set.getBinaryStream(6);
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
            connection.set("serverdata." + AuroraMCAPI.getServerInfo().getNetwork().name() + "." + AuroraMCAPI.getServerInfo().getName(), ";" + AuroraMCAPI.getPlayers().stream().filter(player -> !player.isVanished()).count() + "/" + AuroraMCAPI.getServerInfo().getServerType().getInt("max_players") + ";Lobby;Lobby");
            connection.expire("serverdata." + AuroraMCAPI.getServerInfo().getNetwork().name() + "." + AuroraMCAPI.getServerInfo().getName(), 15);
        }
    }

}
