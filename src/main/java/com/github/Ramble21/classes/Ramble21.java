package com.github.Ramble21.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.Guild;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Ramble21 {

    public static String rateRizz(int rizz) {
        return switch (rizz) {
            case 1 -> "rizz count is 1/10. You're playing Minecraft, in a cave, looking for diamonds";
            case 2 -> "rizz count is 2/10. Maybe they should stop watching so much anime and touch some grass";
            case 3 ->
                    "rizz count is 3/10. It would've been higher if they didn't piss their pants every time they see their crush";
            case 4 -> "rizz count is 4/10. Womp womp";
            case 5 -> "rizz count is 5/10. Literal NPC";
            case 6 -> "rizz count is 6/10. I'm honestly shocked that its even this high";
            case 7 ->
                    "rizz count is 7/10. They only know how to rizz up dudes though, maybe they should try to vary it up";
            case 8 ->
                    "rizz count is 8/10. While getting dates is easy for them, their 1 inch penis makes it hard for anyone to want to stay with them for long";
            case 9 -> "rizz count is 9/10. Maybe this is a result of them finally not playing geometry dash anymore";
            case 10 ->
                    "rizz count is 10/10. Holy shit. We have found the skibidi sigma rizzler themself. Maybe even the future CEO of Ohio";
            default -> "rizz count is -1/12 because there's a bug in ur code";
        };
    }

    public static boolean isBrainrotServer(Guild guild){
        final Dotenv config = Dotenv.configure().load();
        String id = config.get("BRAINROT_ID");
        return guild.getId().equalsIgnoreCase(id);
    }
    public static int generateSeed(String userId) {
        return Integer.parseInt(userId.substring(14)) * LocalDate.now().getMonthValue();
    }

    public static int generateRizz(int seed) {
        Random random = new Random(seed);
        return random.nextInt(10) + 1; // Generates a number between 1 and 10
    }

    public static String generateIp(int seed) {
        Random random1 = new Random(seed);
        String rand1 = Integer.toString(random1.nextInt(255)+1);
        String rand2 = Integer.toString(random1.nextInt(255)+1);
        String rand3 = Integer.toString(random1.nextInt(255)+1);
        String rand4 = Integer.toString(random1.nextInt(255)+1);
        return rand1 + "." + rand2 + "." + rand3 + "." + rand4;
    }

    public static boolean isRambleBot(User user){
        String id = user.getId();
        return id.equalsIgnoreCase("1295872060341616640");
    }
    public static void bugOccurred(MessageChannel channel){
        channel.sendMessage("<@739978476651544607> theres a bug in your code lol").queue();
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

    public static void sortByEstimatedDiff (ArrayList<GeometryDashLevel> list){
        list.sort(
                Comparator.comparingInt(GeometryDashLevel::getDifficultyAsInt).reversed()
                        .thenComparingInt(GeometryDashLevel::getGddlTier)
                        .thenComparingInt(GeometryDashLevel::getAttempts).reversed()
        );
    }

    public static String getVictorsAsMention (GeometryDashLevel level, Guild guild, boolean isPlatformer){
        ArrayList<String> toReturn = getVictors(level, guild, isPlatformer);
        for (int i = 0; i < toReturn.size(); i++){
            String s = "<@" + toReturn.get(i) + ">";
            toReturn.set(i, s);
        }
        String amongUsPotionAtThreeAM = toReturn.get(0);
        for (int i = 1; i < toReturn.size(); i++){
            amongUsPotionAtThreeAM += (", " + toReturn.get(i));
        }
        return amongUsPotionAtThreeAM;
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
            } catch (IOException e){
                continue;
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
            } catch (IOException e){
                continue;
            }
        }
        return toReturn;
    }
}
