package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;
import java.util.Random;

public class RizzRater implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        String user;
        if (event.getOption("member") == null){
            user = event.getUser().getId();
        }
        else{
            User skibidi = Objects.requireNonNull(event.getOption("member")).getAsUser();
            user = skibidi.getId();
        }

        int seed = Ramble21.generateSeed(user);
        int ballzakz = generateRizz(seed);

        user = "<@" + user + ">";

        event.reply(user + "'s " + rateRizz(seed, ballzakz)).queue();
    }
    public static String rateRizz(int seed, int rizz) {
        if (new Random(seed + 1).nextInt(100) == 0) {
            return "rizz count is **NaNe∞/10**. What the fuck. How. I didn't even know this was possible. I'm gonna try to hide my partner before they find out about the inventor of rizz themself";
        }
        else return switch (rizz) {
            case 1 -> "rizz count is **1/10**. I heard they are good friends with that P. Diddy fella, I see them together quite a bit";
            case 2 -> "rizz count is **2/10**. Maybe they should stop gooning to so much anime and try to touch some grass";
            case 3 -> "rizz count is **3/10**. They stare at their crush from a distance instead of actually trying to talk";
            case 4 -> "rizz count is **4/10**. From what I heard, they learned the hard way that chugging an Among Us potion at 3AM wouldn't improve their rizz";
            case 5 -> "rizz count is **5/10**. The overly racist rhetoric they often spill on first dates repels most, but the ones that do stay love it";
            case 6 -> "rizz count is **6/10**. From my personal experiences with them, I'm honestly shocked that its even this high";
            case 7 -> "rizz count is **7/10**. They only know how to rizz up dudes though, maybe they should try to vary it up";
            case 8 -> "rizz count is **8/10**. With such good looks and smooth talking, it's a shame they constantly scare away their dates with their atrocious body odor";
            case 9 -> "rizz count is **9/10**. Hanging out with Keanu Reeves and Jack Black has turned them into a rizzler who can rival Big Chungus himself!";
            case 10 -> "rizz count is **10/10**. Holy shit. We have found the skibidi sigma rizzler themself. Maybe even the future CEO of Ohio";
            default -> "rizz count is **-1/12** because there's a bug in this bot's stupid code";
        };
    }
    public static int generateRizz(int seed) {
        return new Random(seed).nextInt(10) + 1;
    }
}
