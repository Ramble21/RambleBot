package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.geometrydash.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GeometryDashStats implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        Member member;
        if (event.getOption("member") == null){
            member = event.getMember();
        }
        else{
            member = Objects.requireNonNull(event.getOption("member")).getAsMember();
        }
        assert member != null;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.magenta);
        embed.setTitle(member.getEffectiveName() + "'s Profile Stats");
        embed.setThumbnail(member.getUser().getEffectiveAvatarUrl());

        ArrayList<GeometryDashLevel> levels = GeometryDashLevel.getPersonalJsonList(member.getUser(), true);
        if (levels == null) levels = new ArrayList<>();
        try {
            levels.addAll(GeometryDashLevel.getPersonalJsonList(member.getUser(), false));
        } catch (Exception e){
            levels = GeometryDashLevel.getPersonalJsonList(member.getUser(), true);
        }

        String description;
        if (levels == null || levels.isEmpty()){
            description = (member.getAsMention() + " has not submitted any completions yet!");
            embed.setDescription(description);
            event.replyEmbeds(embed.build()).queue();
            return;
        }
        else{
            description =
                    "**Demons Submitted:**\n" +
                    getDemonsSubmittedAsString(false, member) + "\n" +
                    getDemonsSubmittedAsString(true, member)+ "\n\n";

        }
        ArrayList<GeometryDashLevel> plats = GeometryDashLevel.getPersonalJsonList(member.getUser(), true);
        ArrayList<GeometryDashLevel> classics = GeometryDashLevel.getPersonalJsonList(member.getUser(), false);
        String string1 = "Hardest Classic Completion: N/A\n";
        String string2 = "Hardest Platformer Completion: N/A\n";
        if (!(classics == null || classics.isEmpty())){
            GeometryDashLevel hardestClassic = Ramble21.getHardest(member.getUser(), false);
            string1 = "Hardest Classic Completion:** " + Ramble21.getEmojiName(hardestClassic.getDifficulty()) + " " + hardestClassic.getName() + "** " + "(#" + Ramble21.getLeaderboardPosition(hardestClassic, event.getGuild(), false) + ")\n";
        }
        if (!(plats == null || plats.isEmpty())){
            GeometryDashLevel hardestPlat = Ramble21.getHardest(member.getUser(), true);
            string2 = "Hardest Platformer Completion:** " + Ramble21.getEmojiName(hardestPlat.getDifficulty()) + " " + hardestPlat.getName() + "** " + "(#" + Ramble21.getLeaderboardPosition(hardestPlat, event.getGuild(), true) + ")\n";
        }
        description += string1 + string2 + "\n <:star:1307518203122942024> **Attempt Records**:\n";
        description +=
                (Ramble21.getEmojiName("Easy Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Easy Demon", false)) +
                Ramble21.getEmojiName("Medium Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Medium Demon", false)) +
                Ramble21.getEmojiName("Hard Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Hard Demon", false)) +
                Ramble21.getEmojiName("Insane Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Insane Demon", false)) +
                Ramble21.getEmojiName("Extreme Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Extreme Demon", false))
        );

        description += "\n <:star:1307518203122942024> **Attempt Highs** :\n";

        description +=
                (Ramble21.getEmojiName("Easy Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Easy Demon", true)) +
                        Ramble21.getEmojiName("Medium Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Medium Demon", true)) +
                        Ramble21.getEmojiName("Hard Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Hard Demon", true)) +
                        Ramble21.getEmojiName("Insane Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Insane Demon", true)) +
                        Ramble21.getEmojiName("Extreme Demon") + ": " + Ramble21.makeExtremaString(Ramble21.getAttemptExtrema(member.getUser(), "Extreme Demon", true))
        );

        embed.setDescription(description);
        event.replyEmbeds(embed.build()).queue();
    }
    public String getDemonsSubmittedAsString(boolean isPlatformer, Member member){
        String emojiURL = isPlatformer ? "<:moon:1320906679008886784>" : "<:star:1307518203122942024>";
        String easyURL = "<:icon_demon_easy:1307789634415104000>";

        ArrayList<GeometryDashLevel> levels = GeometryDashLevel.getPersonalJsonList(member.getUser(), isPlatformer);
        if (levels == null || levels.isEmpty()) return emojiURL + ": " + "N/A";

        String mediumURL = "<:icon_demon_medium:1307789652010467465>";
        String hardURL = "<:icon_demon_hard:1307789610230874112>";
        String insaneURL = "<:icon_demon_insane:1307789727251955783>";
        String extremeURL = "<:icon_demon_extreme:1307789584331178045>";
        int easyCount = 0;
        int mediumCount = 0;
        int hardCount = 0;
        int insaneCount = 0;
        int extremeCount = 0;
        for (GeometryDashLevel level : levels){
            if (level.getDifficultyAsInt() == 5) easyCount++;
            else if (level.getDifficultyAsInt() == 4) mediumCount++;
            else if (level.getDifficultyAsInt() == 3) hardCount++;
            else if (level.getDifficultyAsInt() == 2) insaneCount++;
            else extremeCount++;
        }
        return emojiURL + ": " + extremeURL + "x" + extremeCount + ", " + insaneURL + "x" + insaneCount + ", " + hardURL + "x" + hardCount + ", " + mediumURL + "x" + mediumCount + ", " + easyURL + "x" + easyCount;
    }
}
