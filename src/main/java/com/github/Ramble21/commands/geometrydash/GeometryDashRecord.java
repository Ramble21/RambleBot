package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.Objects;


public class GeometryDashRecord implements Command {


    @Override
    public void execute(SlashCommandInteractionEvent event) throws ErrorResponseException {
        long id = Objects.requireNonNull(event.getOption("id")).getAsLong();
        long submitterID = Objects.requireNonNull(event.getMember()).getIdLong();
        int attempts = Objects.requireNonNull(event.getOption("attempts")).getAsInt();

        if (id <= -1){
            event.reply("Invalid level ID!").setEphemeral(true).queue();
            return;
        }
        else if (attempts < 10){
            event.reply("Nice try, but I know you spent more than " + attempts + " attempts beating that.").setEphemeral(true).queue();
            return;
        }

        boolean memberIsBlacklisted = GDDatabase.memberIsBlacklisted(event.getMember());
        GDLevel level = GDLevel.fromID(id);
        boolean autoAccepted = level.getDifficultyAsInt() > 1 && !memberIsBlacklisted;
        boolean recordAlrExists = !GDDatabase.addRecord(submitterID, attempts, 0, autoAccepted, level);
        if (recordAlrExists) {
            event.reply("You have already submitted this level!").setEphemeral(true).queue();
            return;
        }
        else if (level.getStars() < 10){
            event.reply("Only rated demon levels are supported, double-check your ID or try another level.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed;
        if (level.getDifficulty().equals("Extreme Demon")) {
            embed = generateExtremeEmbed(level, attempts);
        }
        else if (memberIsBlacklisted) {
            embed = generateBlacklistEmbed(level, attempts);
        }
        else{
            embed = generateEmbed(level, attempts);
        }
        event.getInteraction().replyEmbeds(embed.build()).queue();
    }
    public EmbedBuilder generateEmbed(GDLevel level, int attempts){
        String emoji = Ramble21.getEmojiName(level.getDifficulty());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to " + RambleBot.your(false) + " profile!");
        embed.setColor(Color.green);
        embed.setDescription(
                "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                emoji + " Difficulty: **" + level.getDifficulty() + "**\n" +
                "<:length:1307507840864227468> Attempts: **" + attempts + "**\n");
        return embed;
    }
    public EmbedBuilder generateExtremeEmbed(GDLevel level, int attempts){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(RambleBot.killbotEnjoyer);
        embed.setDescription(
                "Extreme Demon completions have to be approved by a server moderator before getting added to " + RambleBot.your(false) + " profile. \n\n" +
                        "Submission: \n" +
                        "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                        "<:star:1307518203122942024> Difficulty: **" + level.getDifficulty() + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + attempts + "**\n");
        return embed;
    }
    public EmbedBuilder generateBlacklistEmbed(GDLevel level, int attempts){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(RambleBot.killbotEnjoyer);
        embed.setDescription(
                "Due to being blacklisted, all of " + RambleBot.your(false) + " submissions must be approved by a server moderator before getting added to your profile. \n\n" +
                        "Submission: \n" +
                        "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                        "<:star:1307518203122942024> Difficulty: **" + level.getDifficulty() + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + attempts + "**\n");
        return embed;
    }
}