package com.github.Ramble21.commands.messageguessr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class MessageGuessr {

    public static String toDiscordFullTimestamp(long unixSeconds) {
        return "<t:" + unixSeconds + ":F>";
    }

    public static HashSet<Long> getBotIds(Guild guild) {
        HashSet<Long> botIds = new HashSet<>();
        for (Member member : guild.getMembers()) {
            if (member.getUser().isBot()) {
                botIds.add(member.getUser().getIdLong());
            }
        }
        return botIds;
    }

    private static void testDirectories() {
        try {
            for (String pathStr : new String[]{
                    "data",
                    "data/json",
                    "data/json/messageguessr"
            }) {
                Path path = Paths.get(pathStr);
                if (!Files.exists(path)) Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addAltToMap(long altId, long mainId) {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<Long, Long> altIdsToMainId = getAltMap();
        altIdsToMainId.put(altId, mainId);

        try (FileWriter writer = new FileWriter("data/json/messageguessr/altmap.json")){
            gson.toJson(altIdsToMainId,writer);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void addBlacklistedChannel(HashSet<Long> blacklistedChannels, long channelId) {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        blacklistedChannels.add(channelId);

        try (FileWriter writer = new FileWriter("data/json/messageguessr/blacklistedchannels.json")) {
            gson.toJson(blacklistedChannels, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashSet<Long> getBlacklistedChannels() {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashSet<Long> blacklistedChannels;

        try (FileReader reader = new FileReader("data/json/messageguessr/blacklistedchannels.json")) {
            Type listType = new TypeToken<HashSet<Long>>() {}.getType();
            blacklistedChannels = gson.fromJson(reader, listType);

            if (blacklistedChannels == null) {
                blacklistedChannels = new HashSet<>();
            }

        } catch (IOException e) {
            blacklistedChannels = new HashSet<>();
        }

        return blacklistedChannels;
    }

    public static HashMap<Long, Long> getAltMap() {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<Long, Long> altIdsToMainId;

        try (FileReader reader = new FileReader("data/json/messageguessr/altmap.json")) {
            Type listType = new TypeToken<HashMap<Long, Long>>() {}.getType();
            altIdsToMainId = gson.fromJson(reader, listType);

            if (altIdsToMainId == null) {
                altIdsToMainId = new HashMap<>();
            }

        } catch (IOException e) {
            altIdsToMainId = new HashMap<>();
        }

        return altIdsToMainId;
    }
}
