package com.github.Ramble21.listeners;
import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.GeometryDashRecord;
import com.github.Ramble21.classes.Ramble21;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase();
        if (message.equals("r!test") && Ramble21.isBotOwner(event.getAuthor())){
            System.out.println("Hello world!");
        }
        if (message.equals("r!make_new_data") && Ramble21.isBotOwner(event.getAuthor())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type type = new TypeToken<List<GeometryDashRecord>>(){}.getType();

            String[] subDirects = new String[]{"classic", "platformer"};
            for (String subDirectory : subDirects) {
                File inputDir = new File("data/json/completions/" + subDirectory);
                File outputDir = new File("data/json/gd-records/" + subDirectory);

                if (!inputDir.exists()) {
                    continue;
                }
                if (!outputDir.exists()) {
                    if (!outputDir.mkdirs()) {
                        throw new RuntimeException("Failed to create directory: " + outputDir.getAbsolutePath());
                    }
                }

                for (File file : Objects.requireNonNull(inputDir.listFiles())) {
                    try {
                        Reader reader = new FileReader("data/json/completions/" + subDirectory + "/" + file.getName());
                        Reader newReader = new FileReader("data/json/gd-records/" + subDirectory + "/" + file.getName());
                        Writer writer = new FileWriter("data/json/gd-records/" + subDirectory + "/" + file.getName());

                        JsonElement root = JsonParser.parseReader(reader);
                        if (root.isJsonArray()) {
                            JsonArray array = root.getAsJsonArray();
                            for (JsonElement elem : array) {
                                JsonObject obj = elem.getAsJsonObject();
                                int id = obj.get("id").getAsInt();
                                String submitterID = obj.get("submitterID").getAsString();
                                int attempts = obj.get("attempts").getAsInt();

                                ArrayList<GeometryDashRecord> records = gson.fromJson(newReader, type);
                                if (records == null) {
                                    records = new ArrayList<>();
                                }
                                records.add(new GeometryDashRecord(new GeometryDashLevel(id), attempts, submitterID));
                                gson.toJson(writer, type);
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }
}
