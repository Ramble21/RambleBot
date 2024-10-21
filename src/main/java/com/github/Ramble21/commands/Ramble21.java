package com.github.Ramble21.commands;

import net.dv8tion.jda.api.entities.Guild;
import io.github.cdimascio.dotenv.Dotenv;

public class Ramble21 {

    public static String rateRizz(int rizz){
        if (rizz == 1){
            return "rizz count is 1/10. You're playing Minecraft, in a cave, looking for diamonds";
        }
        else if (rizz == 2){
            return "rizz count is 2/10. Maybe they should stop watching so much anime and touch some grass";
        }
        else if (rizz == 3){
            return "rizz count is 3/10. It would've been higher if they didn't piss their pants every time they see their crush";
        }
        else if (rizz == 4){
            return "rizz count is 4/10. Womp womp";
        }
        else if (rizz == 5){
            return "rizz count is 5/10. Literal NPC";
        }
        else if (rizz == 6){
            return "rizz count is 6/10. I'm honestly shocked that its even this high";
        }
        else if (rizz == 7){
            return "rizz count is 7/10. They only know how to rizz up dudes though, maybe they should try to vary it up";
        }
        else if (rizz == 8){
            return "rizz count is 8/10. While getting dates is easy for them, their 1 inch penis makes it hard for anyone to want to stay with them for long";
        }
        else if (rizz == 9){
            return "rizz count is 9/10. Maybe this is a result of them finally not playing geometry dash anymore";
        }
        else if (rizz == 10){
            return "rizz count is 10/10. Holy shit. We have found the skibidi ohio rizzler themself. Maybe even the future CEO of Ohio";
        }
        return "rizz count is -1/12 because there's a bug in ur code";
    }

    public static boolean isBrainrotServer(Guild guild){
        final Dotenv config = Dotenv.configure().load();
        String id = config.get("BRAINROT_ID");
        if (guild.getId().equalsIgnoreCase(id)){
            return true;
        }
        return false;
    }
}
