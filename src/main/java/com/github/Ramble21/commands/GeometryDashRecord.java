package com.github.Ramble21.commands;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Objects;


public class GeometryDashRecord implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        GeometryDashLevel level = new GeometryDashLevel(
                Objects.requireNonNull(event.getOption("id")).getAsInt(),
                Objects.requireNonNull(event.getOption("attempts")).getAsInt(),
                event.getUser()
        );
        if (level.getId() == -1){
            event.reply("Invalid level ID!").setEphemeral(true).queue();
            return;
        }
        else if (level.getStars() < 10){
            event.reply("Only rated demon levels are supported, try another level.").setEphemeral(true).queue();
            return;
        }
        else if (level.isPlatformer()){
            event.reply("Only classic (non-platformer) levels are supported.").setEphemeral(true).queue();
            return;
        }
        else if (level.getAttempts() < 1){
            event.reply("Nice try, but I know you spent more than " + level.getAttempts() + " attempts beating that.").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed;
        if (level.getDifficulty().equals("Extreme Demon")) {
            embed = generateExtremeEmbed(level);
            level.addToModeratorQueue();
        }
        else{
            embed = generateEmbed(level);
            level.writeToPersonalJson();
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

}