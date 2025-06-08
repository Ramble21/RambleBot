package com.github.Ramble21.commands;

import com.github.Ramble21.command.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

public class WordCensorRemove implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String word = Objects.requireNonNull(event.getOption("phrase")).getAsString();
        String path = "data/json/word-censor/" + Objects.requireNonNull(event.getGuild()).getId() + ".json";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<HashMap<String, Boolean>>() {}.getType();
        HashMap<String, Boolean> words;
        try (FileReader reader = new FileReader(path)) {
            words = gson.fromJson(reader, type);
        }
        catch (IOException e) {
            words = new HashMap<>();
        }
        if (!words.containsKey(word)) {
            event.reply("This word/phrase is not part of the censor list!").setEphemeral(true).queue();
            return;
        }
        words.remove(word);
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(words, writer);
            event.reply("✅ Successfully removed `" + word + "` from the censor list.").queue();
        }
    }
}
