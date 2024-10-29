package com.github.Ramble21.commands;

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


public class TypeRacerLeaderboard implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){

        Guild guild = event.getGuild();
        assert guild != null;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(guild.getName() + " WPM Leaderboard");
        File trophy = new File(("src/main/images/trophy.png"));
        embed.setThumbnail("attachment://trophy.png");

        ArrayList<WpmScore> rawScores = TypeRacer.getServerScores(guild);
        if (rawScores == null){
            embed.setDescription("No TypeRacer games have been played yet in this server!");
            event.getInteraction().replyEmbeds(embed.build()).queue();
            return;
        }

        int n = rawScores.size();
        if (rawScores.size() >= 10){
            n = 10;
        }

        WpmScore[] scores = new WpmScore[rawScores.size()];
        for (int i = 0; i < rawScores.size(); i++){
            scores[i] = rawScores.get(i);
        }
        Arrays.sort(scores, Comparator.comparingDouble(WpmScore::getWpm).reversed());

        WpmScore[] scoresToBeDisplayed = new WpmScore[n];

        System.arraycopy(scores, 0, scoresToBeDisplayed, 0, n);

        addDescriptionToEmbed(scoresToBeDisplayed, scoresToBeDisplayed.length, embed);
        event.getInteraction().replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(trophy, "trophy.png")
                ).queue();
    }
    public static void addDescriptionToEmbed(WpmScore[] scoresToBeDisplayed, int n, EmbedBuilder embed){
        // im aware that this code blows but i was too lazy to make it good and it does what its supposed to do
        System.out.println(n);
        switch (n){
            case 1:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">"
                );
                break;
            case 2:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">"
                );
                break;
            case 3:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">"
                );
                break;
            case 4:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[3].getWpm() + "** WPM from <@" + scoresToBeDisplayed[3].getUserId() + ">"
                );
                break;
            case 5:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[3].getWpm() + "** WPM from <@" + scoresToBeDisplayed[3].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[4].getWpm() + "** WPM from <@" + scoresToBeDisplayed[4].getUserId() + ">"
                );
                break;
            case 6:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[3].getWpm() + "** WPM from <@" + scoresToBeDisplayed[3].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[4].getWpm() + "** WPM from <@" + scoresToBeDisplayed[4].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[5].getWpm() + "** WPM from <@" + scoresToBeDisplayed[5].getUserId() + ">"
                );
                break;
            case 7:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[3].getWpm() + "** WPM from <@" + scoresToBeDisplayed[3].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[4].getWpm() + "** WPM from <@" + scoresToBeDisplayed[4].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[5].getWpm() + "** WPM from <@" + scoresToBeDisplayed[5].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[6].getWpm() + "** WPM from <@" + scoresToBeDisplayed[6].getUserId() + ">"
                );
                break;
            case 8:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[3].getWpm() + "** WPM from <@" + scoresToBeDisplayed[3].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[4].getWpm() + "** WPM from <@" + scoresToBeDisplayed[4].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[5].getWpm() + "** WPM from <@" + scoresToBeDisplayed[5].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[6].getWpm() + "** WPM from <@" + scoresToBeDisplayed[6].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[7].getWpm() + "** WPM from <@" + scoresToBeDisplayed[7].getUserId() + ">"
                );
                break;
            case 9:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[3].getWpm() + "** WPM from <@" + scoresToBeDisplayed[3].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[4].getWpm() + "** WPM from <@" + scoresToBeDisplayed[4].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[5].getWpm() + "** WPM from <@" + scoresToBeDisplayed[5].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[6].getWpm() + "** WPM from <@" + scoresToBeDisplayed[6].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[7].getWpm() + "** WPM from <@" + scoresToBeDisplayed[7].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[8].getWpm() + "** WPM from <@" + scoresToBeDisplayed[8].getUserId() + ">"
                );
                break;
            case 10:
                embed.setDescription(
                        "1. **" + scoresToBeDisplayed[0].getWpm() + "** WPM from <@" + scoresToBeDisplayed[0].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[1].getWpm() + "** WPM from <@" + scoresToBeDisplayed[1].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[2].getWpm() + "** WPM from <@" + scoresToBeDisplayed[2].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[3].getWpm() + "** WPM from <@" + scoresToBeDisplayed[3].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[4].getWpm() + "** WPM from <@" + scoresToBeDisplayed[4].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[5].getWpm() + "** WPM from <@" + scoresToBeDisplayed[5].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[6].getWpm() + "** WPM from <@" + scoresToBeDisplayed[6].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[7].getWpm() + "** WPM from <@" + scoresToBeDisplayed[7].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[8].getWpm() + "** WPM from <@" + scoresToBeDisplayed[8].getUserId() + ">\n" +
                        "1. **" + scoresToBeDisplayed[9].getWpm() + "** WPM from <@" + scoresToBeDisplayed[9].getUserId() + ">"
                );
                break;
        }
    }
}