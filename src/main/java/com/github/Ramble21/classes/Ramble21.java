package com.github.Ramble21.classes;

import com.github.Ramble21.RambleBot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class Ramble21 {
    public static String rateRizz(int rizz) {
        return switch (rizz) {
            case 1 -> "rizz count is 1/10. Ain't no party like a Diddy party!";
            case 2 -> "rizz count is 2/10. Maybe they should stop watching so much anime and touch some grass";
            case 3 ->
                    "rizz count is 3/10. It would've been higher if they didn't piss their pants every time they see their crush";
            case 4 -> "rizz count is 4/10. I would check that out if I were them";
            case 5 -> "rizz count is 5/10. Booooooooring";
            case 6 -> "rizz count is 6/10. I'm honestly shocked that its even this high";
            case 7 ->
                    "rizz count is 7/10. They only know how to rizz up dudes though, maybe they should try to vary it up";
            case 8 ->
                    "rizz count is 8/10. If only they didn't scare all of their dates away by yapping endlessly about Balatro";
            case 9 -> "rizz count is 9/10. Maybe this is a result of them finally not playing geometry dash anymore";
            case 10 ->
                    "rizz count is 10/10. Holy shit. We have found the skibidi sigma rizzler themself. Maybe even the future CEO of Ohio";
            default -> "rizz count is -1/12 because there's a bug in this bot's stupid code";
        };
    }
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

    public static int generateRizz(int seed) {
        return new Random(seed).nextInt(10) + 1;
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

    public static void sortByEstimatedDiff (ArrayList<GeometryDashLevel> list, boolean isObjective){
        if (isObjective){
            list.sort(
                    Comparator.comparingInt(GeometryDashLevel::getDifficultyAsInt).reversed()
                            .thenComparingInt(GeometryDashLevel::getGddlTier)
                            .thenComparingInt(GeometryDashLevel::getAttempts).reversed()
            );
        }
        else{
            list.sort(
                    Comparator.comparingInt(GeometryDashLevel::getDifficultyAsInt).reversed()
                            .thenComparingInt(GeometryDashLevel::getGddlTier)
                            .thenComparingInt(GeometryDashLevel::getBiasLevel)
                            .thenComparingInt(GeometryDashLevel::getAttempts).reversed()
            );
        }
    }


    public static String getVictorsAsMention (GeometryDashLevel level, Guild guild, boolean isPlatformer){
        ArrayList<String> toReturn = getVictors(level, guild, isPlatformer);
        toReturn.replaceAll(string -> "<@" + string + ">");
        StringBuilder amongUsPotionAtThreeAM = new StringBuilder(toReturn.get(0));
        for (int i = 1; i < toReturn.size(); i++){
            amongUsPotionAtThreeAM.append(", ").append(toReturn.get(i));
        }
        return amongUsPotionAtThreeAM.toString();
    }
    public static int getAverageAttempts (GeometryDashLevel level, Guild guild, boolean isPlatformer){
        ArrayList<String> toReturn = getVictors(level, guild, isPlatformer);
        int total = 0;
        int iterations = 0;
        for (String memberId : toReturn){
            String rizz;
            if (isPlatformer){
                rizz = "platformer";
            }
            else{
                rizz = "classic";
            }
            String path = "data/json/completions/" + rizz + "/" + memberId + ".json";
            try (FileReader reader = new FileReader(path)){
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
                ArrayList<GeometryDashLevel> completions = gson.fromJson(reader, type);
                for (GeometryDashLevel level2 : completions){
                    if (level2.getId() == level.getId()){
                        total += level2.getAttempts();
                        iterations++;
                    }
                }
            } catch (IOException ignored){
            }
        }
        return total/iterations;
    }
    public static ArrayList<String> getVictors(GeometryDashLevel level, Guild guild, boolean isPlatformer) {
        ArrayList<String> toReturn = new ArrayList<>();
        for (Member member : guild.getMembers()){
            String rizz;
            if (isPlatformer){
                rizz = "platformer";
            }
            else{
                rizz = "classic";
            }
            String path = "data/json/completions/" + rizz + "/" + member.getId() + ".json";
            try (FileReader reader = new FileReader(path)){
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
                ArrayList<GeometryDashLevel> completions = gson.fromJson(reader, type);
                for (GeometryDashLevel level2 : completions){
                    if (level2.getId() == level.getId()){
                        toReturn.add(member.getId());
                    }
                }
            } catch (IOException ignored){
            }
        }
        return toReturn;
    }
    public static String getHardestsAsString(GeometryDashLevel level, Guild guild, boolean isPlatformer){
        ArrayList<String> victorIds = new ArrayList<>();
        String rizz;
        if (isPlatformer){
            rizz = "platformer";
        }
        else{
            rizz = "classic";
        }
        for (Member member : guild.getMembers()){
            String path = "data/json/completions/" + rizz + "/" + member.getId() + ".json";
            try (FileReader reader = new FileReader(path)){
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
                ArrayList<GeometryDashLevel> completions = gson.fromJson(reader, type);
                for (GeometryDashLevel level2 : completions){
                    if (level2.getId() == level.getId()){
                        victorIds.add(member.getId());
                    }
                }
            } catch (IOException ignored){
            }
        }
        ArrayList<String> hardestIdsAsMention = new ArrayList<>();
        for (String id : victorIds){
            String path = "data/json/completions/" + rizz + "/" + id + ".json";
            try (FileReader reader = new FileReader(path)){
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
                ArrayList<GeometryDashLevel> completions = gson.fromJson(reader, type);
                sortByEstimatedDiff(completions, false);
                if (completions.get(0).getName().equals(level.getName()) && completions.get(0).getAuthor().equals(level.getAuthor())){
                    hardestIdsAsMention.add("<@" + id + ">");
                }
            } catch (IOException ignored){
            }
        }
        if (hardestIdsAsMention.isEmpty()){
            return "";
        }
        else if (hardestIdsAsMention.size() == 1){
            return "This is the hardest " + rizz + " level beaten by " + hardestIdsAsMention.get(0) + "!";
        }
        StringBuilder finalString = new StringBuilder();
        for (int i = 0; i < hardestIdsAsMention.size()-1; i++){
            if (i == hardestIdsAsMention.size()-2){
                finalString.append(hardestIdsAsMention.get(i)).append(" and ").append(hardestIdsAsMention.get(i + 1));
            }
            else{
                finalString.append(hardestIdsAsMention.get(i)).append(", ");
            }
        }
        return "This is the hardest " + rizz + " level beaten by " + finalString + "!";
    }
    public static String getDifficultyPngName(GeometryDashLevel level){
        String[] parts = level.getDifficulty().toLowerCase().split(" ", 2);
        String name = parts[0];
        if (level.getRating().equals("featured")){
            name += "_feature";
        }
        else if (!level.getRating().isEmpty()){
            name += "_" + level.getRating();
        }
        return "images/diff_faces/" + name + ".png";
    }
    public static boolean memberIsModerator(Member member){
        return getTrustedUserIDs().contains(member.getId());
    }
    public static GeometryDashLevel getHardest(User user, boolean isPlatformer) throws IOException {
        String rizz = "platformer";
        if (!isPlatformer) rizz = "classic";
        String path =  "data/json/completions/" + rizz + "/" + user.getId() + ".json";
        try (FileReader reader = new FileReader(path)){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
            ArrayList<GeometryDashLevel> completions = gson.fromJson(reader, type);
            sortByEstimatedDiff(completions, false);
            return completions.get(0);
        } catch (IOException e){
            throw new IOException(e);
        }
    }
    public static GeometryDashLevel getAttemptExtrema(User user, String difficulty, boolean high) {
        try (FileReader reader = new FileReader("data/json/completions/classic/" + user.getId() + ".json")){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<GeometryDashLevel>>() {}.getType();
            ArrayList<GeometryDashLevel> completions = gson.fromJson(reader, type);
            int max = 0;
            int min = Integer.MAX_VALUE;
            GeometryDashLevel maxLevel = null;
            GeometryDashLevel minLevel = null;
            for (GeometryDashLevel level : completions){
                if (!level.getDifficulty().equals(difficulty)) continue;
                if (level.getAttempts() < min){
                    min = level.getAttempts();
                    minLevel = level;
                }
                if (level.getAttempts() > max){
                    max = level.getAttempts();
                    maxLevel = level;
                }
            }
            if (high){
                return maxLevel;
            }
            return minLevel;
        } catch (IOException e){
            return null;
        }
    }
    public static String makeExtremaString(GeometryDashLevel level){
        if (level == null){
            return "N/A\n";
        }
        else{
            return "**" + level.getName() + "** (" + level.getAttempts() + " atts)\n";
        }
    }
    public static int getLeaderboardPosition(GeometryDashLevel level, Guild guild, boolean isPlatformer){
        ArrayList<GeometryDashLevel> levels = GeometryDashLevel.getGuildJsonList(guild, isPlatformer);
        sortByEstimatedDiff(levels, true);
        for (int i = 0; i < Objects.requireNonNull(levels).size(); i++){
            if (Objects.equals(levels.get(i).getName(), level.getName()) && Objects.equals(levels.get(i).getAuthor(), level.getAuthor())){
                return i+1;
            }
        }
        return -1;
    }
    public static boolean isBotOwner(User u) {
        return u.getId().equals(getBotOwnerID());
    }
    public static boolean isBlacklisted(User u) {
        HashSet<String> blacklistedIDs = new HashSet<>(Set.of(getBrainrotterID())){};
        return blacklistedIDs.contains(u.getId());
    }
    public static String getBrainrotterID() {
        return Dotenv.configure().load().get("BRAINROTTER_ID");
    }
    public static String getBotOwnerID() {
        return "739978476651544607";
    }
    public static boolean isRambleBot(User u) {
        return u.getId().equals("1295872060341616640");
    }
}
