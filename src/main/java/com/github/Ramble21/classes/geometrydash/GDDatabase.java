package com.github.Ramble21.classes.geometrydash;

import com.github.Ramble21.RambleBot;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;
import java.util.ArrayList;

public class GDDatabase {
    private static final String local_url = Dotenv.configure().load().get("LOCAL_POSTGRES_URL");
    private static final String local_password = Dotenv.configure().load().get("LOCAL_POSTGRES_PW");
    private static final String user = "postgres";
    private static final String prod_url = Dotenv.configure().load().get("PROD_POSTGRES_URL");
    private static final String prod_password = Dotenv.configure().load().get("PROD_POSTGRES_PW");

    public static GDLevel getLevel(long id) {
        String queryTemp =
            """
            SELECT *
            FROM levels
            WHERE id = ?;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, id);
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

    public static ArrayList<GDRecord> getGuildRecords(long guildID, boolean platformer) {
        String queryTemp =
            """
            SELECT r.*, l.*
            FROM records r
            JOIN levels l ON r.level_id = l.id
            JOIN guild_members gm ON r.submitter_id = gm.user_id
            WHERE gm.guild_id = ?
                AND l.platformer = ?
                AND r.record_accepted = TRUE;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        ArrayList<GDRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, guildID);
            stmt.setBoolean(2, platformer);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GDRecord record = new GDRecord(
                        rs.getLong("submitter_id"),
                        rs.getInt("attempts"),
                        rs.getLong("level_id"),
                        rs.getBoolean("record_accepted"),
                        rs.getInt("bias_level")

                    );
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    public static ArrayList<GDRecord> getLevelRecords(long levelID) {
        String queryTemp =
            """
            SELECT r.*, m.username
            FROM records r
            JOIN members m ON r.submitter_id = m.user_id
            WHERE r.level_id = ?
                AND r.record_accepted = TRUE;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        ArrayList<GDRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, levelID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GDRecord record = new GDRecord(
                            rs.getLong("submitter_id"),
                            rs.getInt("attempts"),
                            rs.getLong("level_id"),
                            rs.getBoolean("record_accepted"),
                            rs.getInt("bias_level")

                    );
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    public static ArrayList<GDRecord> getMemberRecords(long submitterID, boolean platformer) {
        String queryTemp =
            """
            SELECT r.*, l.*
            FROM records r
            JOIN levels l ON r.level_id = l.id
            WHERE r.submitter_id = ?
                AND l.platformer = ?
                AND r.record_accepted = TRUE;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        ArrayList<GDRecord> records = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, submitterID);
            stmt.setBoolean(2, platformer);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GDRecord record = new GDRecord(
                            rs.getLong("submitter_id"),
                            rs.getInt("attempts"),
                            rs.getLong("level_id"),
                            rs.getBoolean("record_accepted"),
                            rs.getInt("bias_level")

                    );
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return records;
    }

    public static void addRecord(long submitterID, int attempts, int biasLevel, boolean recordAccepted, long levelID) {
        String queryTemp =
            """
            INSERT INTO records (submitter_id, attempts, bias_level, record_accepted, level_id)
            VALUES (?, ?, ?, ?, ?);
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, submitterID);
            stmt.setInt(2, attempts);
            stmt.setInt(3, biasLevel);
            stmt.setBoolean(4, recordAccepted);
            stmt.setLong(5, levelID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteRecord(long submitterID, long levelID) {
        String queryTemp =
            """
            DELETE FROM records
            WHERE submitter_id = ?;
                AND level_id = ?;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, submitterID);
            stmt.setLong(2, levelID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void editRecord(long submitterID, long levelID, int attempts, int biasLevel) {
        String queryTemp =
            """
            UPDATE records
            SET bias_level = ?, attempts = ?
            WHERE submitter_id = ?;
                AND level_id = ?;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setInt(1, biasLevel);
            stmt.setInt(2, attempts);
            stmt.setLong(3, submitterID);
            stmt.setLong(4, levelID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAllLevels() {
        String selectQuery = "SELECT id FROM levels;";
        String updateQuery = "UPDATE levels SET difficulty = ?, gddl_tier = ? WHERE id = ?;";
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;

        try (Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            ResultSet rs = selectStmt.executeQuery();
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            while (rs.next()) {
                long id = rs.getLong("id");
                GDDifficulty newGDD = GDLevel.fetchGDDLRating(id);
                String newDifficulty = rs.getString("difficulty");
                int newTier = rs.getInt("gddl_tier");
                if (newGDD != null) {
                    newDifficulty = newGDD.difficulty();
                    newTier = newGDD.gddlTier();
                }
                else {
                    System.out.println("API connection error; level " + id + " not updated");
                }

                updateStmt.setString(1, newDifficulty);
                updateStmt.setInt(2, newTier);
                updateStmt.setLong(3, id);
                updateStmt.addBatch(); // queue up updates
            }

            updateStmt.executeBatch(); // execute all updates efficiently

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void getUnverifiedRecords(long guildID) {
        String queryTemp =
            """
            SELECT r.*, l.*, m.username
            FROM records r
            JOIN levels l ON r.level_id = l.id
            JOIN members m ON r.submitter_id = m.user_id
            JOIN guild_members gm ON r.submitter_id = gm.user_id
            WHERE gm.guild_id = ?
              AND r.record_accepted = FALSE;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(queryTemp)) {
            stmt.setLong(1, guildID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDatabase() {
        String databaseCreator =
            """
            BEGIN;
            
            DROP SCHEMA public CASCADE;
            CREATE SCHEMA public;
            
            CREATE TABLE levels (
                id BIGINT PRIMARY KEY,
                name TEXT NOT NULL,
                stars INT,
                author TEXT NOT NULL,
                difficulty TEXT,
                gddl_tier INT,
                platformer BOOLEAN DEFAULT FALSE,
                rating TEXT,
                UNIQUE (name, author)
            );
            
            CREATE TABLE members (
                user_id BIGINT PRIMARY KEY,
                username TEXT NOT NULL
            );
            
            CREATE TABLE guilds (
                guild_id BIGINT PRIMARY KEY,
                name TEXT NOT NULL
            );
            
            CREATE TABLE records (
                record_id BIGSERIAL PRIMARY KEY,
                submitter_id BIGINT NOT NULL REFERENCES members(user_id) ON DELETE CASCADE,
                attempts INT DEFAULT 0,
                bias_level INT DEFAULT 0,
                record_accepted BOOLEAN DEFAULT FALSE,
                level_id BIGINT NOT NULL REFERENCES levels(id) ON DELETE CASCADE,
                UNIQUE (submitter_id, level_id)
            );
            CREATE INDEX records_submitter_index ON records(submitter_id);
            CREATE INDEX records_level_index ON records(level_id);
            
            CREATE TABLE guild_members (
                guild_id BIGINT NOT NULL REFERENCES guilds(guild_id) ON DELETE CASCADE,
                user_id BIGINT NOT NULL REFERENCES members(user_id) ON DELETE CASCADE,
                PRIMARY KEY (guild_id, user_id)
            );
            CREATE INDEX gm_user_index ON guild_members(user_id);
            CREATE INDEX gm_guild_index ON guild_members(guild_id);
            
            COMMIT;
            """;
        String url = RambleBot.isRunningLocally() ? local_url : prod_url;
        String password = RambleBot.isRunningLocally() ? local_password : prod_password;
        try (Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement()) {
            stmt.execute(databaseCreator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Database successfully constructed!");
    }
}