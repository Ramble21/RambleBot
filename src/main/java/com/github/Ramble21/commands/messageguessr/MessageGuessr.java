package com.github.Ramble21.commands.messageguessr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class MessageGuessr {

    public static String toDiscordFullTimestamp(long unixSeconds) {
        return "<t:" + unixSeconds + ":F>";
    }

    public record ClassificationCache(HashSet<Long> invalidIds) {}

    public static void saveClassificationCache(long serverId, ClassificationCache cache) {
        testDirectories();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream("data/json/messageguessr/classification_" + serverId + ".json"),
                StandardCharsets.UTF_8)) {
            gson.toJson(cache, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassificationCache loadClassificationCache(long serverId) {
        testDirectories();
        Gson gson = new GsonBuilder().create();
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("data/json/messageguessr/classification_" + serverId + ".json"),
                StandardCharsets.UTF_8)) {
            ClassificationCache cache = gson.fromJson(reader, ClassificationCache.class);
            return cache != null ? cache : new ClassificationCache(new HashSet<>());
        } catch (IOException e) {
            return new ClassificationCache(new HashSet<>());
        }
    }

    public static HashMap<Long, Long> getMainAccounts(Collection<Long> userIds) {
        HashMap<Long, Long> altIdsToMainId = getAltMap();
        HashMap<Long, Long> result = new HashMap<>();
        for (long id : userIds) {
            result.put(id, altIdsToMainId.getOrDefault(id, id));
        }
        return result;
    }

    public static ArrayList<Long> getBadIds(Guild guild, boolean excludeOldMembers) {
        ArrayList<Long> badIds = new ArrayList<>();
        for (long userId : MessageGuessrDB.getUniqueUserIds(guild.getIdLong(), 1000)) {
            boolean isCurrentMember = guild.getMemberById(userId) != null;

            if (excludeOldMembers && !isCurrentMember) {
                badIds.add(userId);
                continue;
            }

            User user = guild.getJDA().getUserById(userId);
            if (user != null && user.isBot()) {
                badIds.add(userId);
            }
        }
        return badIds;
    }

    public static void addManualDeletedUser(ArrayList<MGUser> deletedUsers, MGUser user) {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        deletedUsers.add(user);

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream("data/json/messageguessr/deletedusers.json"),
                StandardCharsets.UTF_8)) {
            gson.toJson(deletedUsers, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<MGUser> getManualDeletedUsers() {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<MGUser> deletedUsers;

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("data/json/messageguessr/deletedusers.json"),
                StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<ArrayList<MGUser>>() {}.getType();
            deletedUsers = gson.fromJson(reader, listType);

            if (deletedUsers == null) {
                deletedUsers = new ArrayList<>();
            }

        } catch (IOException e) {
            deletedUsers = new ArrayList<>();
        }

        return deletedUsers;
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

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream("data/json/messageguessr/altmap.json"),
                StandardCharsets.UTF_8)) {
            gson.toJson(altIdsToMainId, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addBlacklistedChannel(HashSet<Long> blacklistedChannels, long channelId) {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        blacklistedChannels.add(channelId);

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream("data/json/messageguessr/blacklistedchannels.json"),
                StandardCharsets.UTF_8)) {
            gson.toJson(blacklistedChannels, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashSet<Long> getBlacklistedChannels() {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashSet<Long> blacklistedChannels;

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("data/json/messageguessr/blacklistedchannels.json"),
                StandardCharsets.UTF_8)) {
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

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("data/json/messageguessr/options.json"),
                StandardCharsets.UTF_8)) {
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
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream("data/json/messageguessr/options.json"),
                StandardCharsets.UTF_8)) {
            gson.toJson(options, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<Long, Long> getAltMap() {
        testDirectories();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HashMap<Long, Long> altIdsToMainId;

        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream("data/json/messageguessr/altmap.json"),
                StandardCharsets.UTF_8)) {
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