package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.geometrydash.GDMisc;
import com.github.Ramble21.classes.geometrydash.GDRecord;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class GeometryDashLevelCommand implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String name = Objects.requireNonNull(event.getOption("name")).getAsString();
        String author = Objects.requireNonNull(event.getOption("creator")).getAsString();
        long guildID = Objects.requireNonNull(event.getGuild()).getIdLong();
        GDLevel level = GDDatabase.getLevelFromNameAuthor(name, author);

        if (level == null){
            event.reply("Nobody in this server has beaten that level yet!").setEphemeral(true).queue();
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(level.getName() + " Server Statistics");
        embed.setDescription(makeDescription(level, guildID));
        embed.setColor(Color.magenta);

        final var png = RambleBot.class.getResourceAsStream(level.getDifficultyPngName());
        embed.setThumbnail("attachment://demon.png");
        assert png != null;
        event.replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(png, "demon.png"))
                .queue();

    }
    public String makeDescription(GDLevel level, long guildID){
        ArrayList<GDRecord> records = GDDatabase.getLevelRecords(level.getId(), guildID);
        return (
                Ramble21.getEmojiName(level.getDifficulty()) + " Difficulty: **" + level.getDifficulty() + "**\n" +
                "<:star:1307518203122942024> Victors: " + GDMisc.getVictorsAsMention(records) + "\n" +
                "<:length:1307507840864227468> Average Attempts: **" + GDMisc.getAverageAttempts(records) + "**\n\n" +
                GDMisc.getHardestsAsString(records)
        );
    }
}
