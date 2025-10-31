package com.github.Ramble21.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GeometryDashLevel {

    private String name;
    private int id;
    private int stars;
    private String author;
    private String difficulty;
    private final Integer gddlTier;
    private boolean platformer;
    private int attempts;
    private int biasLevel;

    private final String submitterId;

    private String rating = ""; // feature epic etc, just a star rate is ""

    private boolean featured;
    private boolean epic;
    private boolean legendary;
    private boolean mythic;

    private static final Map<String, Integer> hardCodes = Map.of(
            "38306937", 18, // Buff This
            "104187415", 19, // How to Platformer
            "97543536", 18 // I wanna be the guy
    );

    private static final HashMap<Integer, Integer> gddlTiers = new HashMap<>();
    private static ArrayList<GeometryDashLevel> moderatorQueue;

    public GeometryDashLevel(int levelId, int attempts, String submitterId, int biasLevel){
        this.attempts = attempts;
        this.submitterId = submitterId;
        this.biasLevel = biasLevel;

        String jsonResponse = getApiResponse(levelId);
        if (moderatorQueue == null){
            moderatorQueue = new ArrayList<>();
        }
        if (gddlTiers.isEmpty()){
            initializeGddlMap();
        }
        if (!jsonResponse.equals("Error")){
            parseJson(jsonResponse);
        }
        else {
            this.id = -1;
        }
        makeRating();
        gddlTier = GeometryDashLevel.gddlTiers.getOrDefault(id, 0);
    }
    public GeometryDashLevel(String robtopLevelName, int attempts, User submitter, int biasLevel){
        this.attempts = attempts;
        this.submitterId = submitter.getId();
        this.biasLevel = biasLevel;

        if (moderatorQueue == null){
            moderatorQueue = new ArrayList<>();
        }
        if (gddlTiers.isEmpty()){
            initializeGddlMap();
        }

        gddlTier = 0;
        name = robtopLevelName;
        stars = 10;
        author = "RobTop";
        difficulty = "Easy Demon";
        platformer = false;
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
    public int getAttempts(){
        return attempts;
    }
    public boolean isPlatformer(){
        return platformer;
    }

    public String getSubmitterId() {
        return submitterId;
    }
    public int getGddlTier(){
        return gddlTier;
    }
    public String getRating(){
        return rating;
    }
    public void setAttempts(int attempts){
        this.attempts = attempts;
    }
    public int getBiasLevel() {
        return biasLevel;
    }
    public void setBiasLevel(int biasLevel) {
        this.biasLevel = biasLevel;
    }

    public static void initializeGddlMap(){
        HashMap<String, String> strings = Refresh.fetchGDDLData();
        for (String key : hardCodes.keySet()){
            gddlTiers.put(Integer.parseInt(key), hardCodes.get(key));
        }
        for (Map.Entry<String, String> entry : strings.entrySet()){
            if (hardCodes.containsKey(entry.getKey())) continue;
            if (!canParseInt(entry.getKey())) {
                continue;
            }
            int key = Integer.parseInt(entry.getKey());
            int value = getValue(entry.getValue());
            gddlTiers.put(key, value);
        }
    }
    public static int getValue(String value){
        return canParseInt(value) ? Integer.parseInt(value) : 0;
    }
    public static boolean canParseInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static ArrayList<GeometryDashLevel> getModeratorQueue() {
        return moderatorQueue;
    }
    public void addToModeratorQueue(){
        moderatorQueue.add(this);
        updateModerateQueueJson(this, false);
    }
    @SuppressWarnings("ConstantConditions") // intellij keeps trying to fuck up my code and I don't accidentally listen to it
    public static GeometryDashLevel getFirstInGuild(Guild guild){
        if (moderatorQueue == null){
            moderatorQueue = initializeModeratorQueue();
            if (moderatorQueue == null){
                return null;
            }
        }
        for (GeometryDashLevel level : moderatorQueue){
            if (guild.getMember((guild.getJDA().getUserById(level.getSubmitterId()))) != null){
                return level;
            }
        }
        return null;
    }
    public static ArrayList<GeometryDashLevel> initializeModeratorQueue(){
        Gson gson = new Gson();
        String path = "data/json/completions/queue/queue.json";
        Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();

        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, type);
        }
        catch (IOException e) {
            return null;
        }
    }

    public String toString(){
        return "{\n \"name\": \"" + name +
                "\",\n \"id\": \"" + id +
                "\",\n \"author\": \"" + author +
                "\",\n \"difficulty\": \"" + difficulty +
                "\",\n \"platformer\": \"" + platformer +
                "\",\n \"attempts\": \"" + attempts +
                "\",\n \"gddlTier\": \"" + gddlTier +
                "\",\n \"biasLevel\": \"" + biasLevel +
                "\",\n \"submitterId\": \"" + submitterId +
                "\"\n}";
    }

    public static String getApiResponse(int levelId){
        try{
            String apiUrl = "https://gdbrowser.com/api/level/" + levelId;
            URL url = new URL(apiUrl);

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
        this.stars = data.stars;
        this.platformer = data.platformer;

        this.featured = data.featured;
        this.epic = data.epic;
        this.legendary = data.legendary;
        this.mythic = data.mythic;
    }

    public void makeRating(){
        if (featured){
            rating = "featured";
        }
        if (epic){
            rating = "epic";
        }
        if (legendary){
            rating = "legendary";
        }
        if (mythic){
            rating = "mythic";
        }
        if (!featured && !epic && !legendary && !mythic){
            rating = "";
        }
    }

    public void writeToPersonalJson(boolean isPlatformer){
        String type = "classic";
        if (isPlatformer){
            type = "platformer";
        }
        try {
            for (String pathStr : new String[]{
                    "data",
                    "data/json",
                    "data/json/completions",
                    "data/json/completions/" + type
            }) {
                Path path = Paths.get(pathStr);
                if (!Files.exists(path)) Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<GeometryDashLevel> geometryDashLevels;

        try (FileReader reader = new FileReader("data/json/completions/" + type + "/" + submitterId + ".json")) {
            Type listType = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
            geometryDashLevels = gson.fromJson(reader, listType);

            if (geometryDashLevels == null) {
                geometryDashLevels = new ArrayList<>();
            }

        } catch (IOException e) {
            geometryDashLevels = new ArrayList<>();
        }
        boolean skibidi = false;
        for (GeometryDashLevel level : geometryDashLevels){
            if (this.getName().equals(level.getName()) && this.getAuthor().equals(level.getAuthor())){
                skibidi = true;
                break;
            }
        }
        if (!skibidi){
            geometryDashLevels.add(this);
        }

        try (FileWriter writer = new FileWriter("data/json/completions/" + type + "/" + submitterId + ".json")){
            gson.toJson(geometryDashLevels,writer);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<GeometryDashLevel> getPersonalJsonList(User user, boolean isPlatformer){
        String typeAAA = "classic";
        if (isPlatformer){
            typeAAA = "platformer";
        }
        Gson gson = new Gson();
        String personalJson = "data/json/completions/" + typeAAA + "/" + user.getId() + ".json";
        Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();

        try (FileReader reader = new FileReader(personalJson)) {
            return gson.fromJson(reader, type);
        }
        catch (IOException e) {
            return null;
        }
    }
    public static ArrayList<GeometryDashLevel> getGuildJsonList(Guild guild, boolean isPlatformer){
        String typeAAA = "classic";
        if (isPlatformer){
            typeAAA = "platformer";
        }
        ArrayList<GeometryDashLevel> list = new ArrayList<>();
        Gson gson = new Gson();
        ArrayList<String> userIds = new ArrayList<>();
        for (Member member : guild.getMembers()){
            String path = "data/json/completions/" + typeAAA + "/" + member.getId() + ".json";
            File file = new File(path);
            if (file.exists()){
                userIds.add(path);
            }
        }

        Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
        for (String json : userIds){
            try (FileReader reader = new FileReader(json)) {
                list.addAll(gson.fromJson(reader, type));
            }
            catch (IOException e) {
                return null;
            }
        }
        // removes duplicates
        HashSet<Integer> seenIds = new HashSet<>();
        list.removeIf(obj -> !seenIds.add(obj.getId()));
        return list;
    }
    public static void updateModerateQueueJson(GeometryDashLevel level, Boolean removeLevel){
        try {
            for (String pathStr : new String[]{
                    "data",
                    "data/json",
                    "data/json/completions",
                    "data/json/completions/queue"
            }) {
                Path path = Paths.get(pathStr);
                if (!Files.exists(path)) Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        List<GeometryDashLevel> geometryDashLevels;

        try (FileReader reader = new FileReader("data/json/completions/queue/queue.json")) {
            Type listType = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
            geometryDashLevels = gson.fromJson(reader, listType);

            if (geometryDashLevels == null) {
                geometryDashLevels = new ArrayList<>();
            }

        } catch (IOException e) {
            geometryDashLevels = new ArrayList<>();
        }

        if (!removeLevel){
            geometryDashLevels.add(level);
        }
        else{
            int skibidi = -1;
            for (int i = 0; i < geometryDashLevels.size(); i++){
                if (geometryDashLevels.get(i).getName().equals(level.getName()) && geometryDashLevels.get(i).getAuthor().equals(level.getAuthor())){
                    skibidi = i;
                }
            }
            if (skibidi != -1){
                geometryDashLevels.remove(skibidi);
            }
        }
        moderatorQueue = (ArrayList<GeometryDashLevel>) geometryDashLevels;
        try (FileWriter writer = new FileWriter("data/json/completions/queue/queue.json")){
            gson.toJson(geometryDashLevels,writer);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
