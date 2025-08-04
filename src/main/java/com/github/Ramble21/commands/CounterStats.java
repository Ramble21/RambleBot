package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.CounterMessage;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.TheCounter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class CounterStats implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        String path = "data/json/the-counter/" + Objects.requireNonNull(event.getGuild()).getId() + ".json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        CounterMessage counterMessage = null;
        try (FileReader reader = new FileReader(path)) {
            Type depType = new TypeToken<CounterMessage>() {}.getType();
            counterMessage = gson.fromJson(reader, depType);
        } catch (IOException e) {
            event.reply("This feature is not enabled in this server!").setEphemeral(true).queue();
        }
        assert counterMessage != null;
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(event.getGuild().getName() + " Counter Information");
        eb.setDescription(makeDescription(counterMessage));
        eb.setColor(RambleBot.scaryOrange);
        event.replyEmbeds(eb.build()).queue();
    }
    public String makeDescription(CounterMessage counterMessage) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, Integer> uT = counterMessage.userTriggers;
        HashMap<String, Double> nm = TheCounter.normalize(uT);
        ArrayList<String> topOffenders = new ArrayList<>(getTopOffenders(uT));
        // eventually make this customizable
        sb.append("**Counter trigger words:** [\"penis\", \"cock\", \"dick\", \"peanits\", \"pingas\"]\n");
        sb.append("**Counter trigger exceptions:** [\"cockroach\", \"dickhead\", \"cocktail\", \"peacock\", \"cockpit\", \"cockatoo\", \"dickinson\", \"dickens\", \"penistone\", \"dickwad\"]\n\n");
        sb.append("**Counter trigger most common culprits:**\n");
        for (int i = 0; i < Math.min(10, uT.size()); i++) {
            String id = topOffenders.get(i);
            String decimal = String.format("%.2f%%", nm.get(id));
            String toAppend = "**" + (i+1) + ":** <@" + id + "> — " + uT.get(id) + " trigger" + (uT.get(id) > 1 ? "s" : "") + " (" + decimal + ")\n";
            sb.append(toAppend);
        }
        return sb.toString();
    }
    public static List<String> getTopOffenders(HashMap<String, Integer> map) {
        return map.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey).toList();
    }
}
