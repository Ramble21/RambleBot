package com.github.Ramble21.listeners;
import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.Ramble21;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

    private boolean triggersCounter(String message) {
        String[] triggerWords = {"penis", "cock", "dick"};
        for (String trigger : triggerWords) {
            if (message.contains(trigger) && !message.equals(trigger)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String path = "data/json/the-counter/" + event.getGuild().getId() + ".json";
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw()).toLowerCase();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<CounterMessage>() {}.getType();

        if (message.startsWith("r!counter-setup") && Ramble21.isBotOwner(event.getAuthor())){
            try (FileWriter writer = new FileWriter(path)) {
                gson.toJson(new CounterMessage(event.getMessage()), writer);
                event.getMessage().reply("Counter successfully setup for guild " + event.getGuild().getName()).queue();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (message.startsWith("r!counter-update") && Ramble21.isBotOwner(event.getAuthor())) {
            try (FileReader reader = new FileReader(path)) {
                Type depType = new TypeToken<JsonObject>() {}.getType();
                JsonObject dep = gson.fromJson(reader, depType);
                CounterMessage curr = new CounterMessage(event.getMessage());
                curr.authorMention = dep.get("authorMention").getAsString();
                curr.endDateTimeString = dep.get("dateTimeString").getAsString();
                curr.startDateTimeString = dep.get("dateTimeString").getAsString();
                curr.jumpURL = dep.get("jumpURL").getAsString();
                try (FileWriter writer = new FileWriter(path)) {
                    gson.toJson(curr, writer);
                    event.getMessage().reply("Deprecated JSON successfully replaced for guild " + event.getGuild().getName()).queue();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if (triggersCounter(message)) {
            File guild = new File(path);
            if (guild.exists()) {
                try (FileReader reader = new FileReader(path)) {
                    CounterMessage previousMessage = gson.fromJson(reader, type);
                    CounterMessage currentMessage = new CounterMessage(event.getMessage());
                    OffsetDateTime old = OffsetDateTime.parse(previousMessage.endDateTimeString);
                    OffsetDateTime curr = OffsetDateTime.parse(currentMessage.startDateTimeString);
                    long minutes = ChronoUnit.MINUTES.between(old, curr);

                    if (minutes >= 5) {
                        EmbedBuilder eb = new EmbedBuilder();
                        currentMessage.maxMinutes = Math.max(previousMessage.maxMinutes, ChronoUnit.MINUTES.between(old, curr));
                        eb.setTitle("Reset the counter!");
                        eb.setDescription(
                                "The server is now talking about penis. :pensive:" + "\n" +
                                "The last conversation about penis was **" + getTimeAgo(old, curr) + ".**\n" +
                                "The longest this server has gone without a penis conversation is **" + minutesToBetterUnit(currentMessage.maxMinutes) + ".**\n" +
                                "The last person to start a conversation about penis was " + previousMessage.authorMention + ".\n" +
                                "Previous conversation link: " + previousMessage.jumpURL
                        );
                        eb.setColor(RambleBot.scaryOrange);
                        event.getMessage().replyEmbeds(eb.build()).queue();
                        try (FileWriter writer = new FileWriter(path)) {
                            gson.toJson(currentMessage, writer);
                        }
                    }
                    else {
                        previousMessage.endDateTimeString = currentMessage.startDateTimeString;
                        try (FileWriter writer = new FileWriter(path)) {
                            gson.toJson(previousMessage, writer);
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

        long days = ChronoUnit.DAYS.between(old, curr);
        if (days > 2) return days + " days ago";

        long hours = ChronoUnit.HOURS.between(old, curr);
        if (hours > 0) return hours + (hours == 1 ? " hour ago" : " hours ago");

        long minutes = ChronoUnit.MINUTES.between(old, curr);
        if (minutes > 0) return minutes + (minutes == 1 ? " minute ago" : " minutes ago");

        return "less than a minute ago";
    }
    public static String minutesToBetterUnit(long minutes) {
        if (minutes < 60) {
            return minutes + " minutes";
        }
        long hours = minutes / 60;
        if (hours < 48) {
            return hours + " hours";
        }
        long days = hours / 24;
        if (days < 365) {
            return days + " days";
        }
        long years = days / 365;
        return years + " years";
    }
}
class CounterMessage {
    public String authorMention;
    public String jumpURL;
    public String startDateTimeString;
    public String endDateTimeString;
    public long maxMinutes;
    public CounterMessage(Message m) {
        this.authorMention = m.getAuthor().getAsMention();
        this.jumpURL = m.getJumpUrl();
        this.startDateTimeString = m.getTimeCreated().toString();
        this.endDateTimeString = startDateTimeString;
        this.maxMinutes = 0;
    }
}
