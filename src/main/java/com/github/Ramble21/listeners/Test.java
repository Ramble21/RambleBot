package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("r!append_jsons")){
            String[] filePaths = {
                    "data/json/completions/classic/364135729737105409.json",
                    "data/json/completions/classic/470010465486110730.json",
                    "data/json/completions/classic/597173421671186433.json",
                    "data/json/completions/classic/615425385231810560.json",
                    "data/json/completions/classic/674819147963564054.json",
                    "data/json/completions/classic/681666746511523851.json",
                    "data/json/completions/classic/689616447017975881.json",
                    "data/json/completions/classic/730407701057896521.json",
                    "data/json/completions/classic/739978476651544607.json",
                    "data/json/completions/classic/759086512586358794.json",
                    "data/json/completions/classic/786006212415979570.json",
                    "data/json/completions/classic/840216337119969301.json",
                    "data/json/completions/classic/917819764682915912.json",
                    "data/json/completions/classic/941951025236037632.json",
                    "data/json/completions/classic/987132003331764284.json",
                    "data/json/completions/classic/1030383982774321162.json",
                    "data/json/completions/classic/1105270481294209075.json",
                    "data/json/completions/classic/1206894200831090700.json",
                    "data/json/completions/platformer/597173421671186433.json",
                    "data/json/completions/platformer/615425385231810560.json",
                    "data/json/completions/platformer/681666746511523851.json",
                    "data/json/completions/platformer/739978476651544607.json",
                    "data/json/completions/platformer/987132003331764284.json",
                    "data/json/completions/platformer/1105270481294209075.json"
            };

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            for (String filePath : filePaths){
                System.out.println("Attempting to parse json: " + filePath.substring(21));
                Reader reader = null;
                try {
                    reader = new FileReader(filePath);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Type listType = new TypeToken<List<GeometryDashLevel>>() {}.getType();
                List<GeometryDashLevel> levels = gson.fromJson(reader, listType);

                for (GeometryDashLevel level : levels) {
                    GeometryDashLevel.initializeRating(level);
                }

                try (Writer writer = new FileWriter(filePath)) {
                    gson.toJson(levels, writer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(filePath.substring(21) + " updated successfully");
            }
        }
    }
}
