package com.github.Ramble21.listeners;
import com.github.Ramble21.classes.CounterMessage;
import com.github.Ramble21.classes.Ramble21;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;

public class Test extends ListenerAdapter {

    private final HashMap<String, Integer> userTriggers = new HashMap<>();
    private int numScans = 100;
    OffsetDateTime cutoff = OffsetDateTime.of(
            2025, 6, 6, 0, 0, 0, 0, ZoneOffset.ofHours(-4)
    );

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("r!test") && Ramble21.isBotOwner(event.getAuthor())){
            System.out.println("Hello world!");
        }
        if (event.getMessage().getContentRaw().equals("r!scan") && Ramble21.isBotOwner(event.getAuthor())) {
            try {
                scanMessages(event.getGuild(), "The server is now talking about penis.");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (event.getMessage().getContentRaw().equals("r!grab") && Ramble21.isBotOwner(event.getAuthor())) {
            System.out.println("Finished scanning all channels!");
            String path = "data/json/the-counter/" + event.getGuild().getId() + ".json";
            try (FileReader reader = new FileReader(path)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type depType = new TypeToken<JsonObject>() {}.getType();
                JsonObject dep = gson.fromJson(reader, depType);
                CounterMessage curr = new CounterMessage(
                        dep.get("authorMention").getAsString(),
                        dep.get("jumpURL").getAsString(),
                        dep.get("startDateTimeString").getAsString(),
                        userTriggers
                );
                try (FileWriter writer = new FileWriter(path)) {
                    gson.toJson(curr, writer);
                    event.getMessage().reply("Messages successfully scanned!").queue();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void scanMessages(Guild guild, String targetString) throws FileNotFoundException {
        for (TextChannel channel : guild.getTextChannels()) {
            if (channel.getName().equals("ramblebot-testing")) {
                System.out.println("Nope!");
                continue;
            }
            scanChannel(channel, targetString, guild.getSelfMember());
        }
    }

    public void scanChannel(TextChannel channel, String targetString, Member rambleBot) {
        if (!rambleBot.hasPermission(channel, Permission.VIEW_CHANNEL)) {
            System.out.println("Bot does not have view channel permissions in channel " + channel);
            return;
        }
        channel.getHistory().retrievePast(100).queue(messages -> {
            for (Message message : messages) {
                StringBuilder content = new StringBuilder();
                content.append(message.getContentRaw());
                for (MessageEmbed embed : message.getEmbeds()) {
                    if (embed.getTitle() != null)
                        content.append("\n").append(embed.getTitle());
                    if (embed.getDescription() != null)
                        content.append("\n").append(embed.getDescription());
                    for (MessageEmbed.Field field : embed.getFields()) {
                        content.append("\n").append(field.getName()).append(": ").append(field.getValue());
                    }
                }
                String contentRaw = content.toString();
                if (message.getAuthor().isBot() && contentRaw.contains(targetString)) {
                    Message referenced = message.getReferencedMessage();
                    if (referenced != null) {
                        String ID = referenced.getAuthor().getId();
                        userTriggers.put(ID, userTriggers.getOrDefault(ID, 0) + 1);
                        System.out.println(ID + " from " + referenced.getContentRaw() + " at " + referenced.getTimeCreated());
                    }
                }
            }
            if (messages.isEmpty()) {
                return;
            }
            OffsetDateTime messageTime = messages.get(messages.size() - 1).getTimeCreated();
            if (!messageTime.isBefore(cutoff)) {
                Message oldest = messages.get(messages.size() - 1);
                channel.getHistoryBefore(oldest, 100).queue(nextBatch -> {
                    scanChannelFromHistory(channel, nextBatch.getRetrievedHistory(), targetString, 100);
                });
            }
        });
    }

    private void scanChannelFromHistory(TextChannel channel, List<Message> messages, String targetString, int numScans) {
        this.numScans = numScans;
        for (Message message : messages) {
            StringBuilder content = new StringBuilder();
            content.append(message.getContentRaw());
            for (MessageEmbed embed : message.getEmbeds()) {
                if (embed.getTitle() != null)
                    content.append("\n").append(embed.getTitle());
                if (embed.getDescription() != null)
                    content.append("\n").append(embed.getDescription());
                for (MessageEmbed.Field field : embed.getFields()) {
                    content.append("\n").append(field.getName()).append(": ").append(field.getValue());
                }
            }
            String contentRaw = content.toString();
            if (message.getAuthor().isBot() && contentRaw.contains(targetString)) {
                Message referenced = message.getReferencedMessage();
                if (referenced != null) {
                    String ID = referenced.getAuthor().getId();
                    userTriggers.put(ID, userTriggers.getOrDefault(ID, 0) + 1);
                    System.out.println(ID + " from " + referenced.getContentRaw() + " at " + referenced.getTimeCreated());
                }
            }
        }
        if (messages.isEmpty()) {
            System.out.println("Finished scanning " + channel.getName());
            return;
        }
        OffsetDateTime messageTime = messages.get(messages.size() - 1).getTimeCreated();
        if (!messageTime.isBefore(cutoff)) {
            Message oldest = messages.get(messages.size() - 1);
            channel.getHistoryBefore(oldest, 100).queue(nextBatch -> {
                scanChannelFromHistory(channel, nextBatch.getRetrievedHistory(), targetString, 100);
            });
        }
        else {
            System.out.println("Finished scanning " + channel.getName());
        }
    }
}
