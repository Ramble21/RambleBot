package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.geometrydash.*;
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
        event.deferReply(false).queue();
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

        ArrayList<GDRecord> classicRecords = GDDatabase.getMemberRecords(member.getIdLong(), false);
        ArrayList<GDRecord> platRecords = GDDatabase.getMemberRecords(member.getIdLong(), true);
        GDGuildLB guildClassicLeaderboard = new GDGuildLB(Objects.requireNonNull(event.getGuild()).getIdLong(), false);
        GDGuildLB guildPlatLeaderboard = new GDGuildLB(Objects.requireNonNull(event.getGuild()).getIdLong(), true);

        String description;
        if (classicRecords.isEmpty() && platRecords.isEmpty()){
            description = (member.getAsMention() + " has not submitted any completions yet!");
            embed.setDescription(description);
            event.getHook().editOriginalEmbeds(embed.build()).queue();
            return;
        }
        else{
            description = (
                "**Demons Submitted:**\n" +
                getDemonsSubmittedAsString(classicRecords, false) + "\n" +
                getDemonsSubmittedAsString(platRecords, true)+ "\n\n"
            );
        }

        String string1 = "Hardest Classic Completion: N/A\n";
        String string2 = "Hardest Platformer Completion: N/A\n";
        if (!classicRecords.isEmpty()){
            GDRecord hardestClassic = GDMisc.getHardest(classicRecords);
            GDLevel hardestClassicL = hardestClassic.level();
            string1 = (
                "Hardest Classic Completion:** " + Ramble21.getEmojiName(hardestClassicL.getDifficulty()) +
                " " + hardestClassicL.getName() + "** " + "(#" +
                guildClassicLeaderboard.lbPositionOf(hardestClassicL) + ")\n"
            );
        }
        if (!platRecords.isEmpty()){
            GDRecord hardestPlat = GDMisc.getHardest(platRecords);
            GDLevel hardestPlatL = hardestPlat.level();
            string2 = (
                "Hardest Platformer Completion:** " + Ramble21.getEmojiName(hardestPlatL.getDifficulty()) +
                " " + hardestPlatL.getName() + "** " + "(#" +
                guildPlatLeaderboard.lbPositionOf(hardestPlatL) + ")\n"
            );
        }
        description += string1 + string2 + "\n <:star:1307518203122942024> **Attempt Records**:\n";
        description +=
                (Ramble21.getEmojiName("Easy Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMin(classicRecords, "Easy Demon")) +
                Ramble21.getEmojiName("Medium Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMin(classicRecords, "Medium Demon")) +
                Ramble21.getEmojiName("Hard Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMin(classicRecords, "Hard Demon")) +
                Ramble21.getEmojiName("Insane Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMin(classicRecords, "Insane Demon")) +
                Ramble21.getEmojiName("Extreme Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMin(classicRecords, "Extreme Demon"))
        );

        description += "\n <:star:1307518203122942024> **Attempt Highs** :\n";

        description +=
                (Ramble21.getEmojiName("Easy Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMax(classicRecords, "Easy Demon")) +
                        Ramble21.getEmojiName("Medium Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMax(classicRecords, "Medium Demon")) +
                        Ramble21.getEmojiName("Hard Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMax(classicRecords, "Hard Demon")) +
                        Ramble21.getEmojiName("Insane Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMax(classicRecords, "Insane Demon")) +
                        Ramble21.getEmojiName("Extreme Demon") + ": " + GDMisc.makeExtremaString(GDMisc.getAttemptMax(classicRecords, "Extreme Demon"))
        );

        embed.setDescription(description);
        event.getHook().editOriginalEmbeds(embed.build()).queue();
    }
    public String getDemonsSubmittedAsString(ArrayList<GDRecord> records, boolean isPlatformer){

        String emojiURL = isPlatformer ? "<:moon:1320906679008886784>" : "<:star:1307518203122942024>";
        if (records.isEmpty()) {
            return emojiURL + ": " + "N/A";
        }

        String easyURL = "<:icon_demon_easy:1307789634415104000>";
        String mediumURL = "<:icon_demon_medium:1307789652010467465>";
        String hardURL = "<:icon_demon_hard:1307789610230874112>";
        String insaneURL = "<:icon_demon_insane:1307789727251955783>";
        String extremeURL = "<:icon_demon_extreme:1307789584331178045>";
        int easyCount = 0;
        int mediumCount = 0;
        int hardCount = 0;
        int insaneCount = 0;
        int extremeCount = 0;
        for (GDRecord record : records){
            GDLevel level = record.level();
            if (level.getDifficultyAsInt() == 6) easyCount++;
            else if (level.getDifficultyAsInt() == 7) mediumCount++;
            else if (level.getDifficultyAsInt() == 8) hardCount++;
            else if (level.getDifficultyAsInt() == 9) insaneCount++;
            else if (level.getDifficultyAsInt() == 10) extremeCount++;
        }
        return emojiURL + ": " + extremeURL + "x" + extremeCount + ", " + insaneURL + "x" + insaneCount + ", " + hardURL + "x" + hardCount + ", " + mediumURL + "x" + mediumCount + ", " + easyURL + "x" + easyCount;
    }
}
