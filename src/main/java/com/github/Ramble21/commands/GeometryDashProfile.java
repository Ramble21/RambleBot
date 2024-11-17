package com.github.Ramble21.commands;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;


public class GeometryDashProfile implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        Member member;
        if (event.getOption("member") == null){
            member = event.getMember();
        }
        else{
            member = Objects.requireNonNull(event.getOption("member")).getAsMember();
        }
        assert member != null;
        ArrayList<GeometryDashLevel> list = GeometryDashLevel.getPersonalJsonList(member.getUser());

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(member.getEffectiveName() + "'s Hardest Completions");

        String description = "";

        if (list == null || list.isEmpty()){
            description = member.getAsMention() + " has not submitted any completions yet!";
        }
        else{
            Ramble21.sortByEstimatedDiff(list);
            for (int i = 0; i < 15 && i < list.size(); i++){
                String emoji = Ramble21.getEmojiName(list.get(i).getDifficulty());
                description += (
                    i+1 + " - " + emoji + " **" + list.get(i).getName() + "** by " + list.get(i).getAuthor() + "\n" +
                    "<:length:1307507840864227468> *Attempts: " + list.get(i).getAttempts() + "*\n\n"
                );
            }
        }
        embed.setDescription(description);
        embed.setColor(Color.yellow);
        event.replyEmbeds(embed.build()).queue();
    }
}