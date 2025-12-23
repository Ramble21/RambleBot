package com.github.Ramble21.classes.geometrydash;

import com.google.gson.*;
import jdash.common.DemonDifficulty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Objects;

import static com.github.Ramble21.classes.geometrydash.JDashLevelParser.*;

public class GDLevel {
    private String name;
    private long id;
    private int stars;
    private String author;
    private String difficulty;
    private double gddlTier;
    private boolean platformer;
    private String rating; // feature epic etc., just a star rate is ""

    public static GDLevel fromID(long levelID) {
        GDLevel databaseLevel = GDDatabase.getLevel(levelID);
        if (databaseLevel != null) {
            return databaseLevel;
        }
        return new GDLevel(levelID);
    }
    public static GDLevel fromNameAndDiff(String name, String difficulty) {
        GDLevel databaseLevel = GDDatabase.getLevelFromNameDiff(name, difficulty);
        if (databaseLevel != null) {
            return databaseLevel;
        }
        return new GDLevel(name, difficulty);
    }

    public GDLevel(String name, String difficulty) {
        name = name.toLowerCase();
        if (name.equals("clubstep") || name.equals("theory of everything 2") || name.equals("deadlocked")) {
            id = switch (name) {
                case "clubstep" -> 1;
                case "theory of everything 2" -> 2;
                case "deadlocked" -> 3;
                default -> throw new RuntimeException(name + " is not a RobTop level");
            };
            stars = 10;
            author = "RobTop";
            platformer = false;
            rating = "featured";
        }
        else {
            DemonDifficulty demonDiff = switch (difficulty) {
                case "Easy Demon" -> DemonDifficulty.EASY;
                case "Medium Demon" -> DemonDifficulty.MEDIUM;
                case "Hard Demon" -> DemonDifficulty.HARD;
                case "Insane Demon" -> DemonDifficulty.INSANE;
                case "Extreme Demon" -> DemonDifficulty.EXTREME;
                default -> null;
            };
            jdash.common.entity.GDLevel tempLevel = fetchAPIResponseByName(name, demonDiff);
            if (tempLevel != null){
                parseTempLevel(tempLevel);
            }
            else {
                this.stars = -1;
            }
        }
        if (this.stars != -1) {
            GDDifficulty gdD = fetchGDDLRating(id);
            if (gdD == null) {
                this.difficulty = null;
                this.gddlTier = 0;
                this.name = name;
            }
            else {
                this.difficulty = gdD.difficulty();
                this.gddlTier = gdD.gddlTier();
                this.name = gdD.name();
            }
        }
    }

    public GDLevel(long levelID){
        if (levelID < 4) {
            name = switch ((int) levelID) {
                case 1 -> "Clubstep";
                case 2 -> "Theory of Everything 2";
                case 3 -> "Deadlocked";
                default -> throw new RuntimeException(levelID + " is not a RobTop level");
            };
            id = levelID;
            stars = 10;
            author = "RobTop";
            platformer = false;
            rating = "featured";
        }
        else {
            jdash.common.entity.GDLevel tempLevel = fetchAPIResponse(levelID);
            if (tempLevel != null){
                parseTempLevel(tempLevel);
            }
            else {
                this.stars = -1;
                return;
            }
        }
        GDDifficulty gdD = fetchGDDLRating(id);
        if (gdD == null) {
            this.difficulty = null;
            this.gddlTier = 0;
        }
        else {
            this.difficulty = gdD.difficulty();
            this.gddlTier = gdD.gddlTier();
        }
    }

    public GDLevel(String name, long id, int stars, String author,
                   String difficulty, double gddlTier, boolean platformer, String rating) {
        this.name = name;
        this.id = id;
        this.stars = stars;
        this.author = author;
        this.difficulty = difficulty;
        this.gddlTier = gddlTier;
        this.platformer = platformer;
        this.rating = rating;
    }


    public void parseTempLevel(jdash.common.entity.GDLevel jdashLevel) {
        this.name = jdashLevel.name();
        this.author = jdashLevel.creatorName().isPresent() ? jdashLevel.creatorName().get() : "-";
        this.difficulty = jdashLevel.demonDifficulty().name();
        this.id = jdashLevel.id();
        this.stars = jdashLevel.isDemon() ? 10 : 0;
        this.platformer = jdashLevel.isPlatformer();

        String qr = jdashLevel.qualityRating().name().toLowerCase();
        boolean featured = qr.equals("featured");
        boolean epic = qr.equals("epic");
        boolean legendary = qr.equals("legendary");
        boolean mythic = qr.equals("mythic");
        this.rating = makeRating(featured, epic, legendary, mythic);
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrupted, continuing with retry logic");
        }
    }

    public String getName() {
        return name;
    }
    public long getId() {
        return id;
    }
    public int getStars() {
        return stars;
    }
    public String getAuthor() {
        return author;
    }
    public String getDifficulty() {
        return difficulty;
    }
    public int getDifficultyAsInt(){
        return switch (difficulty){
            case "Non-Demon" -> -1;
            case "Easy Demon" -> 6;
            case "Medium Demon" -> 7;
            case "Hard Demon" -> 8;
            case "Insane Demon" -> 9;
            case "Extreme Demon" -> 10;
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        };
    }

    public boolean isPlatformer(){
        return platformer;
    }
    public double getGddlTier(){
        return gddlTier;
    }
    public String getRating(){
        return rating;
    }

    public static String makeRating(boolean featured, boolean epic, boolean legendary, boolean mythic){
        if (featured){
            return "featured";
        }
        if (epic){
            return "epic";
        }
        if (legendary){
            return "legendary";
        }
        if (mythic){
            return "mythic";
        }
        return "";
    }

    public String getDifficultyPngName(){
        String[] parts = difficulty.toLowerCase().split(" ", 2);
        String name = parts[0];
        if (rating.equals("featured")){
            name += "_feature";
        }
        else if (!rating.isEmpty()){
            name += "_" + rating;
        }
        return "images/diff_faces/" + name + ".png";
    }

    public static GDDifficulty fetchGDDLRating(long levelId) {
        int maxRetries = 7;
        int retryDelayMs = 800;
        int connectionTimeoutMs = 5000;
        int readTimeoutMs = 10000;

        if (levelId == 12107595L) {
            return new GDDifficulty("Generation Retro", null, 67);
        }

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try {
                String apiUrl = "https://gdladder.com/api/level/" + levelId;
                URL url = new URL(apiUrl);

                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(connectionTimeoutMs);
                conn.setReadTimeout(readTimeoutMs);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    try {
                        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                        if (json.has("Rating") && json.has("Meta")) {
                            double gddlTier = 0;
                            JsonElement ratingElement = json.get("Rating");
                            if (ratingElement != null && !ratingElement.isJsonNull()) {
                                gddlTier = json.get("Rating").getAsDouble();
                            }
                            String name = json.getAsJsonObject("Meta").get("Name").getAsString();
                            String difficulty = json.getAsJsonObject("Meta").get("Difficulty").getAsString();
                            difficulty = switch (difficulty) {
                                case "Official", "Easy" -> "Easy Demon";
                                case "Medium" -> "Medium Demon";
                                case "Hard" -> "Hard Demon";
                                case "Insane" -> "Insane Demon";
                                case "Extreme" -> "Extreme Demon";
                                default -> "Non-Demon";
                            };
                            return new GDDifficulty(name, difficulty, gddlTier);
                        }
                        else {
                            System.out.println("No Rating and/or Difficulty field found in response");
                            return null;
                        }
                    } catch (JsonSyntaxException e) {
                        System.out.println("Invalid JSON response: " + e.getMessage());
                        return null;
                    } catch (NumberFormatException e) {
                        System.out.println("Rating value is not a valid number: " + e.getMessage());
                        return null;
                    }
                }
                else if (responseCode == 429) {
                    System.out.println("Rate limited on gdladder.com. Retrying in " + retryDelayMs + "ms... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    sleep(retryDelayMs);
                    retryDelayMs *= 2;
                }
                else if (responseCode >= 500 && responseCode < 600) {
                    System.out.println("GDLadder server error (" + responseCode + "). Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    sleep(retryDelayMs);
                }
                else if (responseCode == 404) {
                    System.out.println("Level rating not found (404) for level ID: " + levelId);
                }
                else {
                    System.out.println("Failed to fetch rating. Response code: " + responseCode);
                    return null;
                }
            }
            catch (SocketTimeoutException e) {
                System.out.println("Request timed out fetching rating. Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                sleep(retryDelayMs);
            }
            catch (IOException e) {
                System.out.println("Network error fetching rating: " + e.getMessage() + ". Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                sleep(retryDelayMs);
            }
            catch (Exception e) {
                System.out.println("Unexpected error fetching rating: " + e.getMessage());
                return null;
            }
            finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException ignored) {
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }

        System.out.println("Max retries exceeded while fetching rating for level " + levelId);
        return null;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GDLevel other = (GDLevel) obj;
        return id == other.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return "{\n" +
            "  \"id\": " + id + ",\n" +
            "  \"name\": \"" + name + "\",\n" +
            "  \"stars\": " + stars + ",\n" +
            "  \"author\": \"" + author + "\",\n" +
            "  \"difficulty\": \"" + difficulty + "\",\n" +
            "  \"gddlTier\": " + gddlTier + ",\n" +
            "  \"platformer\": " + platformer + ",\n" +
            "  \"rating\": \"" + rating + "\"\n" +
            "}";
    }

}
