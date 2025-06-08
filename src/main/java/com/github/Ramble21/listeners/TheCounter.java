package com.github.Ramble21.listeners;
import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.Ramble21;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class TheCounter extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String path = "data/json/the-counter/" + event.getGuild().getId() + ".json";
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw()).toLowerCase();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<CounterMessage>() {}.getType();

        if (message.startsWith("r!counter-setup") && Ramble21.isBotOwner(event.getAuthor())){
            try (FileWriter writer = new FileWriter(path)) {
                gson.toJson(new CounterMessage(event.getMessage()), writer);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (message.contains("penis") || message.contains("cock") || message.contains("dick")) {
            File guild = new File(path);
            if (guild.exists()) {
                try (FileReader reader = new FileReader(path)) {
                    CounterMessage previousMessage = gson.fromJson(reader, type);
                    CounterMessage currentMessage = new CounterMessage(event.getMessage());
                    OffsetDateTime old = OffsetDateTime.parse(previousMessage.dateTimeString);
                    OffsetDateTime curr = OffsetDateTime.parse(currentMessage.dateTimeString);

                    if (ChronoUnit.MINUTES.between(old, curr) >= 3) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Reset the counter!");
                        eb.setDescription(
                                "The server is now talking about penis. :pensive:" + "\n" +
                                "The last conversation about penis was **" + getTimeAgo(old, curr) + ".**\n" +
                                "The last person to start a conversation about penis was " + previousMessage.authorMention + ".\n" +
                                "Previous conversation link: " + previousMessage.jumpURL
                        );
                        eb.setColor(RambleBot.scaryOrange);
                        event.getMessage().replyEmbeds(eb.build()).queue();
                        try (FileWriter writer = new FileWriter(path)) {
                            gson.toJson(currentMessage, writer);
                        }
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static String getTimeAgo(OffsetDateTime old, OffsetDateTime curr) {
        long years = ChronoUnit.YEARS.between(old, curr);
        if (years > 0) return years + (years == 1 ? " year ago" : " years ago");

        long months = ChronoUnit.MONTHS.between(old, curr);
        if (months > 0) return months + (months == 1 ? " month ago" : " months ago");

        long weeks = ChronoUnit.WEEKS.between(old, curr);
        if (weeks > 0) return weeks + (weeks == 1 ? " week ago" : " weeks ago");

        long days = ChronoUnit.DAYS.between(old, curr);
        if (days > 0) return days + (days == 1 ? " day ago" : " days ago");

        long hours = ChronoUnit.HOURS.between(old, curr);
        if (hours > 0) return hours + (hours == 1 ? " hour ago" : " hours ago");

        long minutes = ChronoUnit.MINUTES.between(old, curr);
        if (minutes > 0) return minutes + (minutes == 1 ? " minute ago" : " minutes ago");

        return "less than a minute ago";
    }
}
class CounterMessage {
    public String authorMention;
    public String jumpURL;
    public String dateTimeString;
    public CounterMessage(Message m) {
        this.authorMention = m.getAuthor().getAsMention();
        this.jumpURL = m.getJumpUrl();
        this.dateTimeString = m.getTimeCreated().toString();
    }
}
