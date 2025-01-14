package com.github.Ramble21.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Refresh {
    public static HashMap<String, String> fetchGDDLData(){
        long timeI = System.currentTimeMillis();
        System.out.println("Fetching GDDL data");
        HashMap<String, String> dataMap = new HashMap<>();
        try {
            String csvUrl = "https://docs.google.com/spreadsheets/d/1xaMERl70vzr8q9MqElr4YethnV15EOe8oL1UV9LLljc/export?format=csv&gid=0";
            URL url = new URL(csvUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            int rowIndex = 0;

            while ((line = in.readLine()) != null) {
                if (rowIndex++ == 0) continue;
                String[] columns = line.split(",");
                if (columns.length >= 6) {
                    String key = columns[4].trim(); // column E (IDs)
                    String value = columns[5].trim(); // column F (ratings)
                    dataMap.put(key, value);
                }
            }

            in.close();
            conn.disconnect();
        } catch (Exception e) {
            System.out.println("Failed to fetch data: " + e);
        }
        System.out.println("Finished fetching after " + (System.currentTimeMillis()-timeI) + " ms");
        return dataMap;
    }
    public static GeometryDashLevel refreshLevel(GeometryDashLevel old){
        return new GeometryDashLevel(old.getId(), old.getAttempts(), old.getSubmitterId(), old.getBiasLevel());
    }
    public static void refreshAllLevels(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        for (String filePath : getFilePaths("data/json/completions/classic")){
            filePath = "data/json/completions/classic/" + filePath;
            Reader reader;
            try {
                reader = new FileReader(filePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type listType = new TypeToken<List<GeometryDashLevel>>() {}.getType();
            List<GeometryDashLevel> levels = gson.fromJson(reader, listType);
            levels.replaceAll(Refresh::refreshLevel);
            try (Writer writer = new FileWriter(filePath)) {
                gson.toJson(levels, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(filePath.substring(21) + " updated successfully");
        }
        for (String filePath : getFilePaths("data/json/completions/platformer")){
            filePath = "data/json/completions/platformer/" + filePath;
            Reader reader;
            try {
                reader = new FileReader(filePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Type listType = new TypeToken<List<GeometryDashLevel>>() {}.getType();
            List<GeometryDashLevel> levels = gson.fromJson(reader, listType);
            levels.replaceAll(Refresh::refreshLevel);
            try (Writer writer = new FileWriter(filePath)) {
                gson.toJson(levels, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(filePath.substring(21) + " updated successfully");
        }
    }
    public static ArrayList<String> getFilePaths(String folderPath) {
        ArrayList<String> relativePaths = new ArrayList<>();
        try {
            Files.walk(Paths.get(folderPath))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        Path relativePath = Paths.get(folderPath).relativize(path);
                        relativePaths.add(relativePath.toString());
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return relativePaths;
    }
}
