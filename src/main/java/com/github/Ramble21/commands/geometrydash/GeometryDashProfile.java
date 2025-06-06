package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.PaginatorListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class GeometryDashProfile implements Command {

    private Member member;
    private String originalMessageId;
    private boolean isPlatformer = true;

    public Member getMember() {
        return member;
    }
    public String getOriginalMessageId() {
        return originalMessageId;
    }
    public boolean isPlatformer() {
        return isPlatformer;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        if (event.getOption("platformer") == null || !Objects.requireNonNull(event.getOption("platformer")).getAsBoolean()){
            isPlatformer = false;
        }
        if (event.getOption("member") == null){
            member = event.getMember();
        }
        else{
            member = Objects.requireNonNull(event.getOption("member")).getAsMember();
        }
        assert member != null;
        ArrayList<GeometryDashLevel> list = GeometryDashLevel.getPersonalJsonList(member.getUser(), isPlatformer);

        EmbedBuilder embed = new EmbedBuilder();
        if (isPlatformer){
            embed.setTitle(member.getEffectiveName() + "'s Hardest Platformer Completions");
        }
        else{
            embed.setTitle(member.getEffectiveName() + "'s Hardest Completions");
        }

        String description;
        boolean includeButtons = true;
        if (list == null || list.isEmpty()){
            description = member.getAsMention() + " has not submitted any completions yet!";
            includeButtons = false;
        }
        else{
            Ramble21.sortByEstimatedDiff(list, false);
            description = makePageProfileDescription(list, 10, 0);
        }

        embed.setDescription(description);
        embed.setColor(Color.yellow);
        if (includeButtons){
            final PaginatorListener[] paginatorListener = {null}; // it has to be a 1 element array bc of some dumb java shit
            event.deferReply().queue(hook -> {
                hook.sendMessageEmbeds(embed.build())
                        .addActionRow(
                                Button.secondary("previous_profile", "Previous"),
                                Button.secondary("next_profile", "Next"))
                        .queue(message -> {
                            this.originalMessageId = message.getId();
                            paginatorListener[0] = new PaginatorListener(this, originalMessageId);
                            event.getJDA().addEventListener(paginatorListener[0]);
                        });
            });
            Timer buttonTimeout = new Timer();
            TimerTask removeButtons = new TimerTask() {
                @Override
                public void run() {
                    event.getChannel().editMessageComponentsById(originalMessageId)
                            .setActionRow(
                                    Button.secondary("previous_profile", "Previous").asDisabled(),
                                    Button.secondary("next_profile", "Next").asDisabled())
                            .queue();
                    event.getJDA().removeEventListener(paginatorListener[0]);
                }
            };
            buttonTimeout.schedule(removeButtons, 300000);
        }
        else{
            event.deferReply().queue(hook -> {
                hook.sendMessageEmbeds(embed.build()).queue();
            });
        }
    }
    public static String makePageProfileDescription(ArrayList<GeometryDashLevel> list, int perPage, int pageNo){
        Ramble21.sortByEstimatedDiff(list, false);
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