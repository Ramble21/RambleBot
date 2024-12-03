package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class GeometryDashLevelCommand implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        ArrayList<GeometryDashLevel> possibleLevels = new ArrayList<>();
        possibleLevels.addAll(GeometryDashLevel.getGuildJsonList(event.getGuild(), false));
        possibleLevels.addAll(GeometryDashLevel.getGuildJsonList(event.getGuild(), true));
        String levelName = Objects.requireNonNull(event.getOption("name")).getAsString();
        String authorName = Objects.requireNonNull(event.getOption("creator")).getAsString();
        GeometryDashLevel level = null;
        for (GeometryDashLevel possibleLevel : possibleLevels){
            if (possibleLevel.getName().equalsIgnoreCase(levelName) && possibleLevel.getAuthor().equalsIgnoreCase(authorName)){
                level = possibleLevel;
            }
        }
        if (level == null){
            event.reply("Nobody in this server has beaten that level yet!").setEphemeral(true).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(level.getName() + " Server Statistics");
        embed.setDescription(makeDescription(level, event.getGuild()));
        embed.setColor(Color.magenta);

        final var png = RambleBot.class.getResourceAsStream(Ramble21.getDifficultyPngName(level));
        embed.setThumbnail("attachment://demon.png");
        assert png != null;
        event.replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(png, "demon.png"))
                .queue();

    }
    public String makeDescription(GeometryDashLevel level, Guild guild){
        return (
                Ramble21.getEmojiName(level.getDifficulty()) + " Difficulty: **" + level.getDifficulty() + "**\n" +
                "<:star:1307518203122942024> Victors: " + Ramble21.getVictorsAsMention(level, guild, level.isPlatformer()) + "\n" +
                "<:length:1307507840864227468> Average Attempts: **" + Ramble21.getAverageAttempts(level, guild, level.isPlatformer()) + "**\n\n" +
                Ramble21.getHardestsAsString(level, guild, level.isPlatformer())
        );
    }
}
