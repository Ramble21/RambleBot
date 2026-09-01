package com.github.Ramble21.commands.messageguessr;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;

public class MessageGuessrDB {

    private static final String url = Dotenv.configure().load().get("MESSAGEDB_POSTGRES_URL");
    private static final String password = Dotenv.configure().load().get("PROD_POSTGRES_PW");
    private static final String user = Dotenv.configure().load().get("PROD_POSTGRES_USER");
    private static final long serverId = Long.parseLong(Dotenv.configure().load().get("MESSAGEDB_SERVER_ID"));

    public static Message getMessage(long serverId, ArrayList<Long> goodUserIds, ArrayList<Long> badChannelIds) {
        String queryTemp =
                """
                SELECT * FROM "public"."MESSAGES"
                WHERE "USER_ID" = ANY (?)
                AND "CHANNEL_ID" != ALL (?)
                AND "SERVER_ID" = ?
                AND "CONTENT" IS NOT NULL
                AND length("CONTENT") >= 1
                ORDER BY random()
                LIMIT 1;
                """;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            Array goodUserIdArray = conn.createArrayOf("bigint", goodUserIds.toArray());
            Array badChannelIdArray = conn.createArrayOf("bigint", badChannelIds.toArray());
            stmt.setArray(1, goodUserIdArray);
            stmt.setArray(2, badChannelIdArray);
            stmt.setLong(3, serverId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getString("CONTENT"),
                            rs.getString("JUMP_URL"),
                            rs.getLong("TIMESTAMP"),
                            rs.getLong("USER_ID"),
                            rs.getLong("ID"),
                            rs.getLong("CHANNEL_ID"),
                            rs.getLong("SERVER_ID"),
                            rs.getLong("REPLYING_TO_ID")
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Long> getUniqueUserIds(long serverId, int minMessageCount) {
        String queryTemp =
                """
                SELECT "USER_ID" FROM "public"."MESSAGES"
                WHERE "SERVER_ID" = ?
                GROUP BY "USER_ID"
                HAVING COUNT(*) >= ?
                """;
        ArrayList<Long> userIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, serverId);
            stmt.setInt(2, minMessageCount);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getLong("USER_ID"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userIds;
    }

    public static Message getMessageById(long messageId) {
        String queryTemp =
                """
                SELECT * FROM "public"."MESSAGES"
                WHERE "ID" = ?
                LIMIT 1;
                """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(queryTemp)) {

            stmt.setLong(1, messageId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(
                            rs.getString("CONTENT"),
                            rs.getString("JUMP_URL"),
                            rs.getLong("TIMESTAMP"),
                            rs.getLong("USER_ID"),
                            rs.getLong("ID"),
                            rs.getLong("CHANNEL_ID"),
                            rs.getLong("SERVER_ID"),
                            rs.getLong("REPLYING_TO_ID")
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
