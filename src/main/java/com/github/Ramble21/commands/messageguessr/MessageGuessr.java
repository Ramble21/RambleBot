package com.github.Ramble21.commands.messageguessr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MessageGuessr {

    public static String toDiscordFullTimestamp(long unixSeconds) {
        return "<t:" + unixSeconds + ":F>";
    }

    public static ArrayList<Long> getBadIds(Guild guild, boolean excludeOldMembers) {
        ArrayList<Long> badIds = new ArrayList<>();
        for (long userId : MessageGuessrDB.getUniqueUserIds(guild.getIdLong())) {
            User user = guild.getJDA().getUserById(userId);
            boolean isCurrentMember = guild.getMemberById(userId) != null;

            if (user == null || user.isBot() || (excludeOldMembers && !isCurrentMember)) {
                badIds.add(userId);
            }
        }
        return badIds;
    }

    public static long getMainAccount(long userId) {
        HashMap<Long, Long> altIdsToMainId = getAltMap();
        if (altIdsToMainId.containsKey(userId)) {
            return altIdsToMainId.get(userId);
        }
        altIdsToMainId.put(userId, userId);
        return userId;
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

    public static void addAltToMap(HashMap<Long, Long> altIdsToMainId, long altId, long mainId) {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

    public static MessageGuessrOptions getMiscOptions() {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MessageGuessrOptions options;

        try (FileReader reader = new FileReader("data/json/messageguessr/options.json")) {
            Type listType = new TypeToken<MessageGuessrOptions>() {}.getType();
            options = gson.fromJson(reader, listType);

            if (options == null) {
                options = new MessageGuessrOptions(-1, 3, false, false);
            }

        } catch (IOException e) {
            options = new MessageGuessrOptions(-1, 3, false, false);
        }

        return options;
    }

    public static void setMiscOptions(MessageGuessrOptions options) {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("data/json/messageguessr/options.json")) {
            gson.toJson(options, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
