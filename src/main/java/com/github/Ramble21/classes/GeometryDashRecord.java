package com.github.Ramble21.classes;

import com.github.Ramble21.listeners.PaginatorListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GeometryDashRecord {

    public final GeometryDashLevel level;
    public final int attempts;
    public final String submitterID;

    public GeometryDashRecord(GeometryDashLevel level, int attempts, String submitterID) {
        this.level = level;
        this.attempts = attempts;
        this.submitterID = submitterID;
    }

    public void writeToPersonalJSON() {
        try {
            String path = "data/json/completions/" + (level.platformer ? "platformer" : "classic") + "/" + submitterID + ".json";
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
            String path = "data/json/completions/" + (platformer ? "platformer" : "classic") + "/" + userID + ".json";
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
}
