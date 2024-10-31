package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.WpmScore;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.File;
import java.util.Arrays;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.StringJoiner;


public class TypeRacerLeaderboard implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){

        Guild guild = event.getGuild();
        assert guild != null;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(guild.getName() + " WPM Leaderboard");
        final var trophy = RambleBot.class.getResourceAsStream("images/trophy.png");
        embed.setThumbnail("attachment://trophy.png");

        ArrayList<WpmScore> rawScores = TypeRacer.getServerScores(guild);
        if (rawScores == null || rawScores.isEmpty()){
            embed.setDescription("No TypeRacer games have been played yet in this server!");
            event.getInteraction().replyEmbeds(embed.build()).queue();
            return;
        }

        if (rawScores.size() > 10) rawScores.subList(10, rawScores.size()).clear();

        WpmScore[] scores = new WpmScore[rawScores.size()];
        for (int i = 0; i < rawScores.size(); i++){
            scores[i] = rawScores.get(i);
        }
        Arrays.sort(scores, Comparator.comparingDouble(WpmScore::getWpm).reversed());

        addDescriptionToEmbed(scores, embed);
        event.getInteraction().replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(trophy, "trophy.png")
                ).queue();
    }
    public static void addDescriptionToEmbed(WpmScore[] scoresToBeDisplayed, EmbedBuilder embed){
        assert scoresToBeDisplayed.length != 0;
        StringJoiner joiner = new StringJoiner("\n");
        for (WpmScore score : scoresToBeDisplayed) {
            joiner.add("1. **" + score.getWpm() + "** WPM from <@" + score.getUserId() + ">");
        }
        embed.setDescription(joiner.toString());
    }
}