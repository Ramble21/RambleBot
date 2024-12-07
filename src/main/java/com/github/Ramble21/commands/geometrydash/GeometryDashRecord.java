package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;


public class GeometryDashRecord implements Command {

    private boolean isPlatformer = false;

    @Override
    public void execute(SlashCommandInteractionEvent event){
        GeometryDashLevel level;
        if (Objects.requireNonNull(event.getOption("id")).getAsInt() < 4 && Objects.requireNonNull(event.getOption("id")).getAsInt() > 0){
            if (Objects.requireNonNull(event.getOption("id")).getAsInt() == 3){
                level = new GeometryDashLevel(
                        "Deadlocked",
                        Objects.requireNonNull(event.getOption("attempts")).getAsInt(),
                        event.getUser());
            }
            else if (Objects.requireNonNull(event.getOption("id")).getAsInt() == 2){
                level = new GeometryDashLevel(
                        "Theory of Everything 2",
                        Objects.requireNonNull(event.getOption("attempts")).getAsInt(),
                        event.getUser());
            }
            else {
                level = new GeometryDashLevel(
                        "Clubstep",
                        Objects.requireNonNull(event.getOption("attempts")).getAsInt(),
                        event.getUser());
            }
        }
        else{
            level = new GeometryDashLevel(
                    Objects.requireNonNull(event.getOption("id")).getAsInt(),
                    Objects.requireNonNull(event.getOption("attempts")).getAsInt(),
                    event.getUser()
            );
        }
        if (level.getId() == -1){
            event.reply("Invalid level ID!").setEphemeral(true).queue();
            return;
        }
        else if (level.getStars() < 10){
            event.reply("Only rated demon levels are supported, try another level.").setEphemeral(true).queue();
            return;
        }
        else if (level.isPlatformer()){
            isPlatformer = true;
        }
        else if (level.getAttempts() < 1){
            event.reply("Nice try, but I know you spent more than " + level.getAttempts() + " attempts beating that.").setEphemeral(true).queue();
            return;
        }
        ArrayList<GeometryDashLevel> previousCompletions = GeometryDashLevel.getPersonalJsonList(event.getUser(), isPlatformer);
        if (previousCompletions != null) {
            for (GeometryDashLevel level2 : previousCompletions) {
                if (level2.getId() == level.getId()) {
                    event.reply("You have already submitted this level!").setEphemeral(true).queue();
                    return;
                }
            }
        }

        EmbedBuilder embed;
        if (level.getDifficulty().equals("Extreme Demon")) {
            embed = generateExtremeEmbed(level);
            level.addToModeratorQueue();
        }
        else if (event.getUser().getId().equalsIgnoreCase("840216337119969301")) {
            embed = generateBlacklistEmbed(level);
            level.addToModeratorQueue();
        }
        else{
            embed = generateEmbed(level);
            level.writeToPersonalJson(isPlatformer);
        }
        event.getInteraction().replyEmbeds(embed.build()).queue();
    }
    public EmbedBuilder generateEmbed(GeometryDashLevel level){
        String emoji = Ramble21.getEmojiName(level.getDifficulty());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to your profile!");
        embed.setColor(Color.green);
        embed.setDescription(
                "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                emoji + " Difficulty: **" + level.getDifficulty() + "**\n" +
                "<:length:1307507840864227468> Attempts: **" + level.getAttempts() + "**\n");
        return embed;
    }
    public EmbedBuilder generateExtremeEmbed(GeometryDashLevel level){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(new Color(0, 122, 255));
        embed.setDescription(
                "Extreme Demon completions have to be approved by a server moderator before getting added to your profile. \n\n" +
                        "Submission: \n" +
                        "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                        "<:star:1307518203122942024> Difficulty: **" + level.getDifficulty() + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + level.getAttempts() + "**\n");
        return embed;
    }
    public EmbedBuilder generateBlacklistEmbed(GeometryDashLevel level){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Completion successfully added to moderator queue!");
        embed.setColor(new Color(0, 122, 255));
        embed.setDescription(
                "Due to being blacklisted, all of your submissions must be approved by a server moderator before getting added to your profile. \n\n" +
                        "Submission: \n" +
                        "<:play:1307500271911309322> Name: **" + level.getName() + "**\n" +
                        "<:star:1307518203122942024> Difficulty: **" + level.getDifficulty() + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + level.getAttempts() + "**\n");
        return embed;
    }
}