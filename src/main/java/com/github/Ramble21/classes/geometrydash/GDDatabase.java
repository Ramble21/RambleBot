package com.github.Ramble21.classes.geometrydash;

import com.github.Ramble21.RambleBot;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class GDDatabase {
    private static final String local_url = Dotenv.configure().load().get("LOCAL_POSTGRES_URL");
    private static final String local_password = Dotenv.configure().load().get("LOCAL_POSTGRES_PW");
    private static final String user = "postgres";
    private static final String prod_url = Dotenv.configure().load().get("PROD_POSTGRES_URL");
    private static final String prod_password = Dotenv.configure().load().get("PROD_POSTGRES_PW");

    public static GDLevel getLevel(int id) {
        String queryTemp = "SELECT * FROM levels WHERE id = ?";
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GDLevel(
                            rs.getString("name"),
                            rs.getInt("id"),
                            rs.getInt("stars"),
                            rs.getString("author"),
                            rs.getString("difficulty"),
                            rs.getInt("gddl_tier"),
                            rs.getBoolean("platformer"),
                            rs.getString("rating")
                    );
                }
                else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static GDRecord getRecord(int levelID, String submitterID) {
        String queryTemp = "SELECT * FROM levels WHERE level_id = ? AND submitter_id = ?";
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setInt(1, levelID);
            stmt.setString(2, submitterID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GDRecord(
                            rs.getString("submitter_id"),
                            rs.getInt("id"),
                            rs.getInt("stars"),
                            rs.getBoolean("record_accepted"),
                            rs.getInt("bias_level")
                    );
                }
                else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetDatabase() {
        String levelTable =
        """
        CREATE TABLE levels (
            id INT PRIMARY KEY,
            name TEXT,
            stars INT,
            author TEXT,
            difficulty TEXT,
            gddl_tier INT,
            platformer BOOLEAN,
            rating TEXT
        );
       """;
        String recordsTable =
        """
        CREATE TABLE records (
            submitter_id TEXT,
            attempts INT,
            bias_level INT,
            record_accepted BOOLEAN,
            level_id INT REFERENCES levels(id)
        );
        """;
        String delete = "DROP TABLE IF EXISTS levels, records CASCADE;";
        executeQuickSQL(delete);
        executeQuickSQL(levelTable);
        executeQuickSQL(recordsTable);
    }
    public static void executeQuickSQL(String query) {
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement()) {
            stmt.execute(query);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
