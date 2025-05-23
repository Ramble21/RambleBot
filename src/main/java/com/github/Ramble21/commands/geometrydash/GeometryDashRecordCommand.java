package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.GeometryDashRecord;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;


public class GeometryDashRecordCommand implements Command {

    private boolean isPlatformer = false;

    @Override
    public void execute(SlashCommandInteractionEvent event) throws ErrorResponseException {
        GeometryDashLevel level;
        int attempts = Objects.requireNonNull(event.getOption("attempts")).getAsInt();
        if (Objects.requireNonNull(event.getOption("id")).getAsInt() < 4 && Objects.requireNonNull(event.getOption("id")).getAsInt() > 0){
            if (Objects.requireNonNull(event.getOption("id")).getAsInt() == 3){
                level = new GeometryDashLevel("Deadlocked");
            }
            else if (Objects.requireNonNull(event.getOption("id")).getAsInt() == 2){
                level = new GeometryDashLevel("Theory of Everything 2");
            }
            else {
                level = new GeometryDashLevel("Clubstep");
            }
        }
        else{
            level = new GeometryDashLevel(Objects.requireNonNull(event.getOption("id")).getAsInt());
        }
        if (level.id == -1){
            event.reply("Invalid level ID!").setEphemeral(true).queue();
            return;
        }
        else if (level.stars < 10){
            event.reply("Only rated demon levels are supported, try another level.").setEphemeral(true).queue();
            return;
        }
        else if (level.platformer){
            isPlatformer = true;
        }
        else if (attempts < 1){
            event.reply("Nice try, but I know you spent more than " + attempts + " attempts beating that.").setEphemeral(true).queue();
            return;
        }
        ArrayList<GeometryDashRecord> previousCompletions = GeometryDashRecord.getPersonalJSON(event.getUser().getId(), isPlatformer);
        if (previousCompletions != null) {
            for (GeometryDashRecord record : previousCompletions) {
                if (record.level.equals(level)) {
                    event.reply("You have already submitted this level!").setEphemeral(true).queue();
                    return;
                }
            }
        }
        GeometryDashRecord record = new GeometryDashRecord(level, attempts, event.getUser().getId());
        EmbedBuilder embed;
        if (level.difficulty.equals("Extreme Demon")) {
            embed = generateExtremeEmbed(record);
            record.addToModeratorQueue();
        }
        else if (Ramble21.isBlacklisted(event.getUser())) {
            embed = generateBlacklistEmbed(record);
            record.addToModeratorQueue();
        }
        else{
            embed = generateEmbed(record);
            record.writeToPersonalJSON();
        }
        event.getInteraction().replyEmbeds(embed.build()).queue();
    }
    public EmbedBuilder generateEmbed(GeometryDashRecord record){
        String emoji = Ramble21.getEmojiName(record.level.difficulty);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to your profile!");
        embed.setColor(Color.green);
        embed.setDescription(
                "<:play:1307500271911309322> Name: **" + record.level.name + "**\n" +
                        emoji + " Difficulty: **" + record.level.difficulty + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + record.attempts + "**\n");
        return embed;
    }
    public EmbedBuilder generateExtremeEmbed(GeometryDashRecord record){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(RambleBot.killbotEnjoyer);
        embed.setDescription(
                "Extreme Demon completions have to be approved by a server moderator before getting added to your profile. \n\n" +
                        "Submission: \n" +
                        "<:play:1307500271911309322> Name: **" + record.level.name + "**\n" +
                        "<:star:1307518203122942024> Difficulty: **" + record.level.difficulty + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + record.attempts + "**\n");
        return embed;
    }
    public EmbedBuilder generateBlacklistEmbed(GeometryDashRecord record){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(RambleBot.killbotEnjoyer);
        embed.setDescription(
                "Due to being blacklisted, all of your submissions must be approved by a server moderator before getting added to your profile. \n\n" +
                        "Submission: \n" +
                        "<:play:1307500271911309322> Name: **" + record.level.name + "**\n" +
                        "<:star:1307518203122942024> Difficulty: **" + record.level.difficulty + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + record.attempts + "**\n");
        return embed;
    }
}