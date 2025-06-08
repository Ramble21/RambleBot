package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.command.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class WordCensorList implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
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
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(RambleBot.killbotEnjoyer);
        eb.setTitle("Word censor list for " + event.getGuild().getName());
        eb.setDescription(createDescription(words));
        eb.setThumbnail(event.getGuild().getIconUrl());
        event.replyEmbeds(eb.build()).queue();
    }
    public String createDescription(HashMap<String, Boolean> words) {
        ArrayList<String> amogus = new ArrayList<>(words.keySet());
        if (amogus.isEmpty()) {
            return "There are no censored words/phrases in this server!";
        }
        StringBuilder result = new StringBuilder().append("```\n").append(amogus.get(0));
        for (int i = 1; i < amogus.size(); i++) {
            result.append("\n").append(amogus.get(i));
            if (words.get(amogus.get(i))) {
                result.append(" (word only)");
            }
        }
        result.append("```");
        return result.toString();
    }
}
