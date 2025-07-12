package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Diacritics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WordCensorListener extends ListenerAdapter {
    private final HashMap<String, String> logChannels = new HashMap<>(Map.of
            ("931838136223412235", "1177506145053720676",
            "987325755937660978", "989286440812834906",
            "993983631007682620", "1279510405319753903")
    );
    // key -> server ID, value -> log channel ID
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            return;
        }
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw().toLowerCase());
        String path = "data/json/word-censor/" + event.getGuild().getId() + ".json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<HashMap<String, Boolean>>() {}.getType();
        HashMap<String, Boolean> words;
        try (FileReader reader = new FileReader(path)) {
            words = gson.fromJson(reader, type);
        }
        catch (IOException e) {
            words = new HashMap<>();
        }
        for (String censoredWord : words.keySet()) {
            boolean wordOnly = words.get(censoredWord);
            if (containsWord(wordOnly, message, censoredWord)) {
                String mention = event.getAuthor().getAsMention();
                event.getMessage().delete().queue();
                event.getChannel().sendMessage(mention + " **said a naughty word!** Watch " + RambleBot.your() + " mouth, kiddo.").queue();
                if (logChannels.containsKey(event.getGuild().getId())) {
                    EmbedBuilder logEmbed = new EmbedBuilder();
                    logEmbed.setColor(RambleBot.scaryOrange);
                    logEmbed.setAuthor(event.getAuthor().getName(), event.getAuthor().getAvatarUrl());
                    logEmbed.setDescription("**Message sent by " + mention + " Deleted in " + event.getChannel().getAsMention() + "\n" + "Message: \n**" + event.getMessage().getContentRaw() + "**\n" + "Offending phrase: **`" + censoredWord + "`");
                    logEmbed.setFooter(Objects.requireNonNull(event.getMessage().getTimeCreated()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    Objects.requireNonNull(event.getJDA().getTextChannelById(logChannels.get(event.getGuild().getId()))).sendMessageEmbeds(logEmbed.build()).queue();
                }
            }
        }
    }
    public static boolean containsWord(boolean wordOnly, String message, String censoredWord) {
        if (!wordOnly) {
            return message.contains(censoredWord);
        }
        String[] words = message.split("\\s+");
        for (String word : words) {
            if (word.equals(censoredWord)) {
                return true;
            }
        }
        return false;
    }
}
