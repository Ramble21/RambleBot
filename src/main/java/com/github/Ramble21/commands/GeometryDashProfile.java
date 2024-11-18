package com.github.Ramble21.commands;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.GeometryDashReviewButtonListener;
import com.github.Ramble21.listeners.PaginatorListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;


public class GeometryDashProfile implements Command {

    private Member member;
    private String originalMessageId;

    public Member getMember() {
        return member;
    }
    public String getOriginalMessageId() {
        return originalMessageId;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){

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

        String description;
        if (list == null || list.isEmpty()){
            description = member.getAsMention() + " has not submitted any completions yet!";
        }
        else{
            Ramble21.sortByEstimatedDiff(list);
            description = makePageProfileDescription(list, 15, 0);
        }

        embed.setDescription(description);
        embed.setColor(Color.yellow);
        event.deferReply().queue(hook -> {
            hook.sendMessageEmbeds(embed.build())
                    .addActionRow(
                        Button.secondary("previous_profile", "Previous"),
                        Button.secondary("next_profile", "Next"))
                    .queue(message -> {
                        this.originalMessageId = message.getId();
                        PaginatorListener paginatorListener = new PaginatorListener(this, originalMessageId);
                        event.getJDA().addEventListener(paginatorListener);
                    });
        });
    }
    public static String makePageProfileDescription(ArrayList<GeometryDashLevel> list, int perPage, int pageNo){
        Ramble21.sortByEstimatedDiff(list);
        String description = "";
        for (int i = pageNo*perPage; i < perPage+(pageNo*perPage) && i < list.size(); i++){
            String emoji = Ramble21.getEmojiName(list.get(i).getDifficulty());
            description += (
                    i+1 + " - " + emoji + " **" + list.get(i).getName() + "** by " + list.get(i).getAuthor() + "\n" +
                            "<:length:1307507840864227468> *Attempts: " + list.get(i).getAttempts() + "*\n\n"
            );
        }
        return description;
    }
}