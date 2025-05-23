package com.github.Ramble21.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class GeometryDashRecord {

    public final GeometryDashLevel level;
    public final int attempts;
    public final String submitterID;

    public final static Queue<GeometryDashLevel> moderatorQueue = new LinkedList<>();

    public GeometryDashRecord(GeometryDashLevel level, int attempts, String submitterID) {
        this.level = level;
        this.attempts = attempts;
        this.submitterID = submitterID;
    }

    public void writeToPersonalJSON() {
        try {
            String path = "data/json/gd-records/" + (level.platformer ? "platformer" : "classic") + "/" + submitterID + ".json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(path);
            ArrayList<GeometryDashRecord> list = new ArrayList<>();
            if (file.exists()) {
                Reader reader = new FileReader(file);
                Type listType = new TypeToken<List<GeometryDashRecord>>(){}.getType();
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
    public static ArrayList<GeometryDashRecord> getPersonalJSON(String userID, boolean platformer) {
        ArrayList<GeometryDashRecord> list = new ArrayList<>();
        try {
            String path = "data/json/gd-records/" + (platformer ? "platformer" : "classic") + "/" + userID + ".json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(path);
            if (file.exists()) {
                Reader reader = new FileReader(file);
                Type listType = new TypeToken<List<GeometryDashRecord>>(){}.getType();
                list = gson.fromJson(reader, listType);
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public static ArrayList<GeometryDashLevel> getModeratorQueue() {
        ArrayList<GeometryDashLevel> list = new ArrayList<>();
        try {
            String path = "data/json/gd-records/queue.json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(path);
            if (file.exists()) {
                Reader reader = new FileReader(file);
                Type listType = new TypeToken<List<GeometryDashRecord>>(){}.getType();
                list = gson.fromJson(reader, listType);
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    public void addToModeratorQueue() {
        try {
            String path = "data/json/gd-records/queue.json";
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(path);
            ArrayList<GeometryDashRecord> list = new ArrayList<>();
            if (file.exists()) {
                Reader reader = new FileReader(file);
                Type listType = new TypeToken<List<GeometryDashRecord>>(){}.getType();
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
    public static HashSet<GeometryDashLevel> getGuildLevels(Guild guild, boolean platformer) {
        HashSet<GeometryDashLevel> levels = new HashSet<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<ArrayList<GeometryDashRecord>>(){}.getType();

        for (Member member : guild.getMembers()) {
            String path = "data/json/gd-records/" + (platformer ? "platformer" : "classic") + "/" + member.getId() + ".json";
            File file = new File(path);
            if (file.exists()) {
                try (FileReader reader = new FileReader(path)) {
                    ArrayList<GeometryDashRecord> records = gson.fromJson(reader, type);
                    for (GeometryDashRecord record : records) {
                        levels.add(record.level);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return levels;
    }
}
