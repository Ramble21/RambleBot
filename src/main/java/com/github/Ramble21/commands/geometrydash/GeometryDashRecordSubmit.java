package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.Objects;


/**
 * @param bySearch true if searching by name/diff, false if by id
 */
public record GeometryDashRecordSubmit(boolean bySearch) implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws ErrorResponseException {

        event.deferReply(false).queue();
        int attempts = Objects.requireNonNull(event.getOption("attempts")).getAsInt();
        if (attempts < 10) {
            event.getHook().sendMessage("Nice try, but I know you spent more than " + attempts + " attempts beating that.").setEphemeral(true).queue();
            return;
        }

        GDLevel level;
        if (bySearch) {
            String name = Objects.requireNonNull(event.getOption("name")).getAsString();
            String difficulty = Objects.requireNonNull(event.getOption("difficulty")).getAsString();
            level = GDLevel.fromNameAndDiff(name, difficulty);
            if (level.getStars() == -1) {
                event.getHook().sendMessage("This level does not exist!").setEphemeral(true).queue();

                return;
            }
        }
        else {
            long id = Objects.requireNonNull(event.getOption("id")).getAsLong();
            if (id <= -1) {
                event.getHook().sendMessage("Invalid level ID!").setEphemeral(true).queue();
                return;
            }
            level = GDLevel.fromID(id);
            if (level.getStars() == -1) {
                event.getHook().sendMessage("Invalid level ID!").setEphemeral(true).queue();
                return;
            }
        }

        boolean recordAlrExists = submitRecord(level, event.getMember(), attempts);

        String memberStatus = GDDatabase.getMemberStatus(Objects.requireNonNull(event.getMember()));
        boolean memberIsBlacklisted = memberStatus.equals("blacklisted");
        boolean memberIsModerator = memberStatus.equals("moderator");

        if (recordAlrExists) {
            event.getHook().sendMessage("You have already submitted this level!").setEphemeral(true).queue();
            return;
        }
        else if (level.getStars() < 10) {
            event.getHook().sendMessage("Only rated demon levels are supported, double-check your ID or try another level.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed;
        if (level.getDifficulty().equals("Extreme Demon") && !memberIsModerator) {
            embed = generateExtremeEmbed(level, attempts);
        }
        else if (memberIsBlacklisted) {
            embed = generateBlacklistEmbed(level, attempts);
        }
        else {
            embed = generateEmbed(level, attempts);
        }
        event.getHook().editOriginalEmbeds(embed.build()).queue();
    }

    public static boolean submitRecord(GDLevel level, Member member, int attempts) {
        // submits record, returns true if the record already exists and false if the record does not already exist
        String memberStatus = GDDatabase.getMemberStatus(member);
        boolean memberIsBlacklisted = memberStatus.equals("blacklisted");
        boolean memberIsModerator = memberStatus.equals("moderator");
        long submitterID = Objects.requireNonNull(member).getIdLong();
        System.out.println("Submitting record: GDRecord[name=" + level.getName() + ", ID=" + level.getId() + ", difficulty=" + level.getDifficulty() + ", submitter=" + member.getEffectiveName() + ", attempts=" + attempts + "]");

        boolean autoAccepted = (level.getDifficultyAsInt() < 10 && !memberIsBlacklisted) || memberIsModerator;
        return !GDDatabase.addRecord(submitterID, attempts, 0, autoAccepted, level);
    }

    public EmbedBuilder generateEmbed(GDLevel level, int attempts) {
        String emoji = Ramble21.getEmojiName(level.getDifficulty());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to " + RambleBot.your(false) + " profile!");
        embed.setColor(Color.green);
        embed.setDescription(
                "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                "<:creatorpoints:1434551362456129546> Creator: **" + level.getAuthor() + "**\n" +
                emoji + " Difficulty: **" + level.getDifficulty() + "**\n" +
                "<:length:1307507840864227468> Attempts: **" + attempts + "**\n"
        );
        return embed;
    }

    public EmbedBuilder generateExtremeEmbed(GDLevel level, int attempts) {
        EmbedBuilder embed = new EmbedBuilder();
        String emoji = Ramble21.getEmojiName(level.getDifficulty());
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(RambleBot.killbotEnjoyer);
        embed.setDescription(
                "Extreme Demon completions have to be approved by a RambleBot moderator before getting added to " + RambleBot.your(false) + " profile. \n\n" +
                "Submission: \n" +
                "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                "<:creatorpoints:1434551362456129546> Creator: **" + level.getAuthor() + "**\n" +
                emoji + " Difficulty: **" + level.getDifficulty() + "**\n" +
                "<:length:1307507840864227468> Attempts: **" + attempts + "**\n"
        );
        return embed;
    }

    public EmbedBuilder generateBlacklistEmbed(GDLevel level, int attempts) {
        EmbedBuilder embed = new EmbedBuilder();
        String emoji = Ramble21.getEmojiName(level.getDifficulty());
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(RambleBot.killbotEnjoyer);
        embed.setDescription(
                "Due to being blacklisted, all of " + RambleBot.your(false) + " submissions must be approved by a RambleBot moderator before getting added to your profile. \n\n" +
                "Submission: \n" +
                "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                "<:creatorpoints:1434551362456129546> Creator: **" + level.getAuthor() + "**\n" +
                emoji + " Difficulty: **" + level.getDifficulty() + "**\n" +
                "<:length:1307507840864227468> Attempts: **" + attempts + "**\n"
        );
        return embed;
    }
}