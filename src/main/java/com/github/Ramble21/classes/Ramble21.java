package com.github.Ramble21.classes;

import net.dv8tion.jda.api.entities.Guild;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;
import java.util.Random;

public class Ramble21 {

    public static String rateRizz(int rizz) {
        switch (rizz) {
            case 1:
                return "rizz count is 1/10. You're playing Minecraft, in a cave, looking for diamonds";
            case 2:
                return "rizz count is 2/10. Maybe they should stop watching so much anime and touch some grass";
            case 3:
                return "rizz count is 3/10. It would've been higher if they didn't piss their pants every time they see their crush";
            case 4:
                return "rizz count is 4/10. Womp womp";
            case 5:
                return "rizz count is 5/10. Literal NPC";
            case 6:
                return "rizz count is 6/10. I'm honestly shocked that its even this high";
            case 7:
                return "rizz count is 7/10. They only know how to rizz up dudes though, maybe they should try to vary it up";
            case 8:
                return "rizz count is 8/10. While getting dates is easy for them, their 1 inch penis makes it hard for anyone to want to stay with them for long";
            case 9:
                return "rizz count is 9/10. Maybe this is a result of them finally not playing geometry dash anymore";
            case 10:
                return "rizz count is 10/10. Holy shit. We have found the skibidi sigma rizzler themself. Maybe even the future CEO of Ohio";
            default:
                return "rizz count is -1/12 because there's a bug in ur code";
        }
    }

    public static boolean isBrainrotServer(Guild guild){
        final Dotenv config = Dotenv.configure().load();
        String id = config.get("BRAINROT_ID");
        if (guild.getId().equalsIgnoreCase(id)){
            return true;
        }
        return false;
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
        Random random2 = new Random(seed);
        Random random3 = new Random(seed);
        Random random4 = new Random(seed);
        String rand1 = Integer.toString(random1.nextInt(255)+1);
        String rand2 = Integer.toString(random1.nextInt(255)+1);
        String rand3 = Integer.toString(random1.nextInt(255)+1);
        String rand4 = Integer.toString(random1.nextInt(255)+1);
        return rand1 + "." + rand2 + "." + rand3 + "." + rand4;
    }
}
