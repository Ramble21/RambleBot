package com.github.Ramble21.classes.geometrydash;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class GDLevel {
    private String name;
    private int id;
    private int stars;
    private String author;
    private String difficulty;
    private final int gddlTier;
    private boolean platformer;
    private String rating; // feature epic etc, just a star rate is ""

    public static GDLevel getLevelFromID(int levelID) {
        GDLevel databaseLevel = GDDatabase.getLevel(levelID);
        if (databaseLevel != null) {
            return databaseLevel;
        }
        return new GDLevel(levelID);
    }

    public GDLevel(int levelID){
        if (levelID < 4) {
            name = switch (levelID) {
                case 1 -> "clubstep";
                case 2 -> "theory of everything 2";
                case 3 -> "deadlocked";
                default -> throw new RuntimeException(levelID + " is not a RobTop level");
            };
            gddlTier = fetchGDDLRating(id);
            stars = 10;
            author = "RobTop";
            difficulty = "Easy Demon";
            platformer = false;
            rating = "featured";
        }
        else {
            String jsonResponse = fetchAPIResponse(levelID);
            if (jsonResponse != null){
                parseJson(jsonResponse);
            }
            else {
                this.stars = -1;
            }
            gddlTier = fetchGDDLRating(id);
        }
    }

    public GDLevel(String name, int id, int stars, String author,
                   String difficulty, int gddlTier, boolean platformer, String rating) {
        this.name = name;
        this.id = id;
        this.stars = stars;
        this.author = author;
        this.difficulty = difficulty;
        this.gddlTier = gddlTier;
        this.platformer = platformer;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }
    public int getId() {
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
        if (difficulty == null)
            throw new NullPointerException(toString());
        return switch (difficulty){
            case "Easy Demon" -> 5;
            case "Medium Demon" -> 4;
            case "Hard Demon" -> 3;
            case "Insane Demon" -> 2;
            case "Extreme Demon" -> 1;
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        };
    }
    public boolean isPlatformer(){
        return platformer;
    }
    public int getGddlTier(){
        return gddlTier;
    }
    public String getRating(){
        return rating;
    }

    public void parseJson(String jsonResponse){
        JsonObject data = JsonParser.parseString(jsonResponse).getAsJsonObject();

        this.name = data.get("name").getAsString();
        this.author = data.get("author").getAsString();
        this.difficulty = data.get("difficulty").getAsString();
        this.id = data.get("id").getAsInt();
        this.stars = data.get("stars").getAsInt();
        this.platformer = data.get("platformer").getAsBoolean();

        boolean featured = data.get("featured").getAsBoolean();
        boolean epic = data.get("epic").getAsBoolean();
        boolean legendary = data.get("legendary").getAsBoolean();
        boolean mythic = data.get("mythic").getAsBoolean();
        this.rating = makeRating(featured, epic, legendary, mythic);
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

    public static int fetchGDDLRating(int levelId) {
        int maxRetries = 3;
        int retryDelayMs = 1000;
        int connectionTimeoutMs = 5000;
        int readTimeoutMs = 10000;

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

                        if (json.has("Rating")) {
                            String rating = json.get("Rating").getAsString();
                            return (int) Math.round(Double.parseDouble(rating));
                        } else {
                            System.out.println("No Rating field found in response");
                            return -1;
                        }
                    } catch (JsonSyntaxException e) {
                        System.out.println("Invalid JSON response: " + e.getMessage());
                        return -1;
                    } catch (NumberFormatException e) {
                        System.out.println("Rating value is not a valid number: " + e.getMessage());
                        return -1;
                    }
                }
                else if (responseCode == 429) { // Rate limited
                    System.out.println("Rate limited on gdladder.com. Retrying in " + retryDelayMs + "ms... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    Thread.sleep(retryDelayMs);
                    retryDelayMs *= 2; // Exponential backoff
                    continue;
                }
                else if (responseCode >= 500 && responseCode < 600) { // Server error
                    System.out.println("GDLadder server error (" + responseCode + "). Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    Thread.sleep(retryDelayMs);
                    continue;
                }
                else if (responseCode == 404) {
                    System.out.println("Level rating not found (404) for level ID: " + levelId);
                    return -1;
                }
                else {
                    System.out.println("Failed to fetch rating. Response code: " + responseCode);
                    return -1;
                }
            }
            catch (SocketTimeoutException e) {
                System.out.println("Request timed out fetching rating. Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            }
            catch (IOException e) {
                System.out.println("Network error fetching rating: " + e.getMessage() + ". Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Request interrupted while fetching rating");
                return -1;
            }
            catch (Exception e) {
                System.out.println("Unexpected error fetching rating: " + e.getMessage());
                return -1;
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
        return -1;
    }

    public static String fetchAPIResponse(int levelId) {
        int maxRetries = 3;
        int retryDelayMs = 1000;
        int connectionTimeoutMs = 5000;
        int readTimeoutMs = 10000;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            HttpURLConnection connection = null;
            try {
                String apiUrl = "https://gdbrowser.com/api/level/" + levelId;
                URL url = new URL(apiUrl);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(connectionTimeoutMs);
                connection.setReadTimeout(readTimeoutMs);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                }
                else if (responseCode == 429) { // Rate limited
                    System.out.println("Rate limited on gdbrowser.com. Retrying in " + retryDelayMs + "ms... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    Thread.sleep(retryDelayMs);
                    retryDelayMs *= 2;
                }
                else if (responseCode >= 500 && responseCode < 600) { // Server error
                    System.out.println("Server error (" + responseCode + "). Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                    Thread.sleep(retryDelayMs);
                }
                else if (responseCode == 404) { // 404
                    System.out.println("Level " + levelId + " not found (404)");
                    return null;
                }
                else {
                    System.out.println("GET request failed for level " + levelId + ". Response code: " + responseCode);
                    return null;
                }
            }
            catch (SocketTimeoutException e) {
                System.out.println("Request timed out. Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            catch (IOException e) {
                System.out.println("Network error: " + e.getMessage() + ". Retrying... (Attempt " + (attempt + 1) + "/" + maxRetries + ")");
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Request interrupted");
                return null;
            }
            catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                return null;
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        System.out.println("Max retries exceeded");
        return null;
    }
}
