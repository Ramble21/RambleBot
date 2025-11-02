package com.github.Ramble21.classes;

import com.github.Ramble21.RambleBot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class Ramble21 {

    public static ArrayList<String> getTrustedUserIDs() {
        String url = "trusted_users.txt";
        final var dictInputStream = RambleBot.class.getResourceAsStream(url);
        assert dictInputStream != null;
        ArrayList<String> temp = new ArrayList<>(new BufferedReader(new InputStreamReader(dictInputStream)).lines().toList());
        temp.replaceAll(s -> s.substring(0, s.indexOf("/") - 1));
        System.out.println(temp);
        return temp;
    }
    public static boolean isBrainrotServer(Guild guild){
        final Dotenv config = Dotenv.configure().load();
        String id = config.get("BRAINROT_ID");
        String otherId = "993983631007682620";
        return (guild.getId().equalsIgnoreCase(id) || guild.getId().equalsIgnoreCase(otherId));
    }
    public static int generateSeed(String userId) {
        return Integer.parseInt(userId.substring(14)) * LocalDate.now().getMonthValue();
    }

    public static String generateIp(int seed) {
        Random random1 = new Random(seed);
        String rand1 = Integer.toString(random1.nextInt(255)+1);
        String rand2 = Integer.toString(random1.nextInt(255)+1);
        String rand3 = Integer.toString(random1.nextInt(255)+1);
        String rand4 = Integer.toString(random1.nextInt(255)+1);
        return rand1 + "." + rand2 + "." + rand3 + "." + rand4;
    }

    public static double getMatchingPercentage(String str1, String str2) {
        if (str1.isEmpty()) {
            return 0.0;
        }
        int totalChars = str1.length();
        int matchingChars = 0;
        boolean[] charPresent = new boolean[256];
        for (char c : str2.toCharArray()) {
            charPresent[c] = true;
        }
        for (char c : str1.toCharArray()) {
            if (charPresent[c]) {
                matchingChars++;
            }
        }
        return (double) matchingChars / totalChars * 100;
    }
    public static String getEmojiName(String difficulty){
        return switch (difficulty){
            case "Easy Demon" -> "<:icon_demon_easy:1307789634415104000>";
            case "Medium Demon" -> "<:icon_demon_medium:1307789652010467465>";
            case "Hard Demon" -> "<:icon_demon_hard:1307789610230874112>";
            case "Insane Demon" -> "<:icon_demon_insane:1307789727251955783>";
            case "Extreme Demon" -> "<:icon_demon_extreme:1307789584331178045>";
            default -> throw new IllegalStateException("Unexpected value: " + difficulty);
        };
    }

    public static boolean memberNotTrustedUser(Member member){
        return !getTrustedUserIDs().contains(member.getId());
    }
    public static boolean isBotOwner(User u) {
        return u.getId().equals("739978476651544607") || u.getId().equals("786006212415979570");
    }
    public static String getBrainrotterID() {
        return Dotenv.configure().load().get("BRAINROTTER_ID");
    }
    public static boolean isRambleBot(User u) {
        return u.getId().equals("1295872060341616640");
    }
    public static boolean isRepuestaServer(Guild g) {
        try (FileReader reader = new FileReader("data/json/misc/repuestas.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> list = gson.fromJson(reader, type);
            return list != null && list.contains(g.getId());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean modifyRepuestaServers(Guild g, boolean remove) {
        // returns true if worked as intended, false if nothing occurs, throws error if there is a missing file
        String guildID = g.getId();
        String filePath = "data/json/misc/repuestas.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> ids;
        try (FileReader reader = new FileReader(filePath)) {
            ids = gson.fromJson(reader, type);
            if (ids == null) {
                ids = new ArrayList<>();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if ((ids.contains(guildID) && !remove) || (!ids.contains(guildID) && remove)) {
            return false;
        }
        else {
            if (!remove) {
                ids.add(guildID);
            }
            else {
                ids.remove(guildID);
            }
            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(ids, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }
}
