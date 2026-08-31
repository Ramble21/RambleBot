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

    public static Message getMessage(long serverId, ArrayList<Long> badUserIds, ArrayList<Long> badChannelIds) {
        String queryTemp =
                """
                SELECT * FROM "public"."MESSAGES"
                WHERE "USER_ID" != ALL (?)
                AND "CHANNEL_ID" != ALL (?)
                AND "SERVER_ID" = ?
                ORDER BY random()
                LIMIT 1;
                """;

        if (RambleBot.isRunningLocally()) {
            throw new RuntimeException("Message database connection does not work while running locally!");
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(queryTemp)) {

            Array badUserIdArray = conn.createArrayOf("bigint", badUserIds.toArray());
            Array badChannelIdArray = conn.createArrayOf("bigint", badChannelIds.toArray());

            stmt.setArray(1, badUserIdArray);
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

    public static Message getMessageById(long messageId) {
        String queryTemp =
                """
                SELECT * FROM "public"."MESSAGES"
                WHERE "ID" = ?
                LIMIT 1;
                """;

        if (RambleBot.isRunningLocally()) {
            throw new RuntimeException("Message database connection does not work while running locally!");
        }

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
