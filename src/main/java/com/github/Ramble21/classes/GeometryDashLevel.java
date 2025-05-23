package com.github.Ramble21.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GeometryDashLevel {

    public String name;
    public long id;
    public int stars;
    public String author;
    public String difficulty;
    public int gddlTier;
    public boolean platformer;

    private boolean featured;
    private int epicValue; // 0 = rated/featured, 1 = epic, 2 = leg, 3 = mythic

    private static final Map<String, Integer> hardCodes = Map.of(
            "38306937", 18, // Buff This
            "104187415", 19, // How to Platformer
            "97543536", 18 // I wanna be the guy
    );

    private static final HashMap<Long, Integer> gddlTiers = new HashMap<>(); // key = ID, value = tier

    public GeometryDashLevel(int levelId){

        String jsonResponse = getApiResponse(levelId);
        if (gddlTiers.isEmpty()){
            initializeGddlMap();
        }
        if (!jsonResponse.equals("Error")){
            parseJson(jsonResponse);
        }
        else {
            this.id = -1;
        }
        gddlTier = GeometryDashLevel.gddlTiers.getOrDefault(id, 0);
    }
    public GeometryDashLevel(String robtopLevelName){
        if (gddlTiers.isEmpty()){
            initializeGddlMap();
        }

        gddlTier = switch (robtopLevelName.toLowerCase()) {
            case "clubstep, theory of everything 2" -> 3;
            case "deadlocked" -> 5;
            default -> throw new RuntimeException();
        };
        name = robtopLevelName;
        author = "RobTop";
        difficulty = "Easy Demon";
        platformer = false;
    }

    public String getRateType() {
        if (!featured) {
            return "";
        }
        return switch (epicValue) {
            case 0 -> "feature";
            case 1 -> "epic";
            case 2 -> "legendary";
            case 3 -> "mythic";
            default -> throw new RuntimeException();
        };
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

    public static void initializeGddlMap(){
        HashMap<String, String> strings = Refresh.fetchGDDLData();
        for (String key : hardCodes.keySet()){
            // Add hardcoded entries
            gddlTiers.put(Long.parseLong(key), hardCodes.get(key));
        }
        for (Map.Entry<String, String> entry : strings.entrySet()){
            // Skip already hardcoded entries
            if (hardCodes.containsKey(entry.getKey())) {
                continue;
            }
            long key = Long.parseLong(entry.getKey());
            int value = Integer.parseInt(entry.getValue());
            // Convert <Str, Str> to <Long, Int>
            gddlTiers.put(key, value);
        }
    }

    public String toString(){
        return "{\n \"name\": \"" + name +
                "\",\n \"id\": \"" + id +
                "\",\n \"author\": \"" + author +
                "\",\n \"difficulty\": \"" + difficulty +
                "\",\n \"platformer\": \"" + platformer +
                "\",\n \"gddlTier\": \"" + gddlTier +
                "\"\n}";
    }

    public static String getApiResponse(int levelId){
        try{

            URL url = new URL("https://gdbrowser.com/api/level/" + levelId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
            else {
                System.out.println("GET request failed. Response code: " + responseCode);
                return "Error";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void parseJson(String jsonResponse){
        Gson gson = new Gson();
        GeometryDashLevel data = gson.fromJson(jsonResponse, GeometryDashLevel.class);
        this.name = data.name;
        this.author = data.author;
        this.difficulty = data.difficulty;
        this.id = data.id;
        this.platformer = data.platformer;
        this.epicValue = data.epicValue;
        this.featured = data.featured;
        this.stars = data.stars;
    }
    public static ArrayList<GeometryDashLevel> getCachedLevels() {
        ArrayList<GeometryDashLevel> list = new ArrayList<>();
        try {
            String path = "data/json/gd-records/cache.json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(path);
            if (file.exists()) {
                Reader reader = new FileReader(file);
                Type listType = new TypeToken<List<GeometryDashLevel>>(){}.getType();
                list = gson.fromJson(reader, listType);
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public void cacheLevel() {
        try {
            String path = "data/json/gd-records/cache.json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(path);
            ArrayList<GeometryDashLevel> list = new ArrayList<>();
            if (file.exists()) {
                Reader reader = new FileReader(file);
                Type listType = new TypeToken<List<GeometryDashLevel>>(){}.getType();
                list = gson.fromJson(reader, listType);
                reader.close();
            }
            list.add(this);
            Writer writer = new FileWriter(file);
            gson.toJson(list, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GeometryDashLevel other = (GeometryDashLevel) obj;
        return id == other.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
