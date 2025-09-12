package com.github.Ramble21.listeners;
import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.CounterMessage;
import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.Ramble21;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
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
import java.util.HashMap;
import java.util.Map;

public class TheCounter extends ListenerAdapter {

    private boolean triggersCounter(String message) {
        String[] triggerWords = {
                "penis", "cock", "dick", "peanits", "pingas"
        };
        String[] exceptions = {
                "cockroach", "dickhead", "cocktail", "peacock", "cockpit",
                "cockatoo", "dickinson", "dickens", "penistone", "dickwad"
        };
        for (String trigger : triggerWords) {
            if (message.contains(trigger) && !message.equals(trigger)) {
                for (String exception : exceptions) {
                    if (message.contains(exception) && exception.contains(trigger)) {
                        for (int i = 0; i < message.length(); i++) {
                            if (message.startsWith(trigger, i) && !message.startsWith(exception, i)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String path = "data/json/the-counter/" + event.getGuild().getId() + ".json";
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw()).toLowerCase();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<CounterMessage>() {}.getType();

        if (message.startsWith("r!counter-setup") && Ramble21.isBotOwner(event.getAuthor())){
            try (FileWriter writer = new FileWriter(path)) {
                gson.toJson(new CounterMessage(event.getMessage(), new HashMap<>()), writer);
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
                CounterMessage curr = new CounterMessage(event.getMessage(), new HashMap<>());
                curr.authorMention = dep.get("authorMention").getAsString();
                curr.endDateTimeString = dep.get("endDateTimeString").getAsString();
                curr.startDateTimeString = dep.get("startDateTimeString").getAsString();
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
                    CounterMessage currentMessage = new CounterMessage(event.getMessage(), previousMessage.userTriggers);
                    OffsetDateTime old = OffsetDateTime.parse(previousMessage.endDateTimeString);
                    OffsetDateTime curr = OffsetDateTime.parse(currentMessage.startDateTimeString);
                    long minutes = ChronoUnit.MINUTES.between(old, curr);

                    if (minutes >= 5) {
                        currentMessage.addUserTrigger(event.getMessage());
                        currentMessage.maxMinutes = Math.max(previousMessage.maxMinutes, ChronoUnit.MINUTES.between(old, curr));
                        HashMap<String, Double> percentages = normalize(currentMessage.userTriggers);
                        Map.Entry<String, Double> allTimeHigh = getAllTimeHigh(percentages);
                        String userPercent = String.format("%.2f%%", percentages.get(currentMessage.authorMention.substring(2, currentMessage.authorMention.length() - 1)));
                        String allTimePercent = String.format("%.2f%%", allTimeHigh.getValue());

                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Reset the counter!");
                        eb.setDescription(
                                "The server is now talking about penis. :pensive:" + "\n" +
                                "The last conversation about penis was **" + getTimeAgo(old, curr) + ".**\n" +
                                "The longest this server has gone without a penis conversation is **" + minutesToBetterUnit(currentMessage.maxMinutes) + ".**\n" +
                                currentMessage.authorMention + " has started **" + userPercent + "** of penis conversations in this server.\n" +
                                "<@" + allTimeHigh.getKey() + "> has started the most penis conversations in this server, at **" + allTimePercent + "**.\n" +
                                "The last person to start a conversation about penis was " + previousMessage.authorMention + ": " + previousMessage.jumpURL + "."
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
    public Map.Entry<String, Double> getAllTimeHigh(HashMap<String, Double> percentages) {
        Map.Entry<String, Double> allTimeHigh = null;
        double max = 0.0;
        for (Map.Entry<String, Double> entry : percentages.entrySet()) {
            if (entry.getValue() > max) {
                allTimeHigh = entry;
                max = entry.getValue();
            }
        }
        return allTimeHigh;
    }
    public static HashMap<String, Double> normalize(HashMap<String, Integer> raw) {
        HashMap<String, Double> normalized = new HashMap<>();
        int total = raw.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) return normalized;
        for (Map.Entry<String, Integer> entry : raw.entrySet()) {
            double percent = (entry.getValue() * 100.0) / total;
            normalized.put(entry.getKey(), percent);
        }
        return normalized;
    }
    public static String getTimeAgo(OffsetDateTime old, OffsetDateTime curr) {
        long minutes = ChronoUnit.MINUTES.between(old, curr);
        long hours = minutes / 60;
        long days = hours / 24;
        long years = days / 365;

        if (years > 0) return years + (years == 1 ? " year ago" : " years ago");
        if (hours >= 100) return days + " days ago";
        if (hours > 0) return hours + (hours == 1 ? " hour ago" :  " hours ago");
        if (minutes > 0) return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        return "less than a minute ago";
    }
    public static String minutesToBetterUnit(long minutes) {
        if (minutes < 60) {
            return minutes + " minutes";
        }
        long hours = minutes / 60;
        if (hours < 100) {
            return hours + " hours";
        }
        long days = hours / 24;
        if (days < 365) {
            return days + " days";
        }
        long years = days / 365;
        return years + (years == 1 ? " year" : " years");
    }
}
