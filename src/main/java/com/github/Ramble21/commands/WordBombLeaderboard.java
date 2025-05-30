package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.command.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.checkerframework.checker.units.qual.A;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class WordBombLeaderboard implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        Guild guild = event.getGuild();
        assert guild != null;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(guild.getName() + " WordBomb Leaderboard");
        final var trophy = RambleBot.class.getResourceAsStream("images/trophy.png");
        embed.setThumbnail("attachment://trophy.png");
        embed.setColor(RambleBot.killbotEnjoyer);

        HashMap<String, Integer> scores = getServerScores(guild.getId());
        if (scores == null || scores.isEmpty()){
            embed.setDescription("No WordBomb games have been played yet in this server!");
            event.getInteraction().replyEmbeds(embed.build()).queue();
            return;
        }
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(scores.entrySet());
        entries.sort(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue()).reversed());

        addDescriptionToEmbed(entries, embed);
        assert trophy != null;
        event.getInteraction().replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(trophy, "trophy.png")
                ).queue();
    }
    public static HashMap<String, Integer> getServerScores(String guildID) {
        String path = "data/json/wordbomb/" + guildID + ".json";
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, type);
        }
        catch (IOException e) {
            return null;
        }
    }
    public static void addServerScore(String guildID, String userID, int score) {
        String path = "data/json/wordbomb/" + guildID + ".json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<String, Integer> scores = getServerScores(guildID);
        if (scores == null) {
            scores = new HashMap<>();
        }
        scores.put(userID, scores.getOrDefault(userID, 0) + score);
        try (FileWriter writer = new FileWriter(path)){
            gson.toJson(scores, writer);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public void addDescriptionToEmbed(ArrayList<Map.Entry<String, Integer>> entries, EmbedBuilder embed) {
        StringJoiner joiner = new StringJoiner("\n");
        for (Map.Entry<String, Integer> entry : entries) {
            joiner.add("1. <@" + entry.getKey() + ">: **" + entry.getValue() + " points**");
        }
        embed.setDescription(joiner.toString());
    }
}
