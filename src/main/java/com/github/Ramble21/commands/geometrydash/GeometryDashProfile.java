package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.geometrydash.GDMisc;
import com.github.Ramble21.classes.geometrydash.GDRecord;
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

    private String originalMessageId;
    private boolean isPlatformer = true;
    private ArrayList<GDRecord> records;

    public boolean isPlatformer() {
        return isPlatformer;
    }
    public ArrayList<GDRecord> getRecords() {
        return records;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        if (event.getOption("platformer") == null || !Objects.requireNonNull(event.getOption("platformer")).getAsBoolean()){
            isPlatformer = false;
        }
        Member member = event.getMember();
        if (event.getOption("member") != null){
            member = Objects.requireNonNull(event.getOption("member")).getAsMember();
        }
        assert member != null;

        records = GDDatabase.getMemberRecords(member.getIdLong(), isPlatformer);
        GDMisc.sortUserRecordsByDiff(records);

        EmbedBuilder embed = new EmbedBuilder();
        if (isPlatformer){
            embed.setTitle(member.getEffectiveName() + "'s Hardest Platformer Completions");
        }
        else{
            embed.setTitle(member.getEffectiveName() + "'s Hardest Classic Completions");
        }

        String description;
        boolean includeButtons = true;
        if (records.isEmpty()){
            description = member.getAsMention() + " has not submitted any " + (isPlatformer ? "platformer" : "classic") + " completions yet!";
            includeButtons = false;
        }
        else{
            description = makePageProfileDescription(records, 10, 0);
        }

        embed.setDescription(description);
        embed.setColor(Color.yellow);
        if (includeButtons){
            final PaginatorListener[] paginatorListener = {null}; // it has to be a 1 element array bc of some dumb java shit
            event.deferReply().queue(hook -> hook.sendMessageEmbeds(embed.build())
                    .addActionRow(
                            Button.secondary("previous_profile", "Previous"),
                            Button.secondary("next_profile", "Next"))
                    .queue(message -> {
                        this.originalMessageId = message.getId();
                        paginatorListener[0] = new PaginatorListener(this, originalMessageId);
                        event.getJDA().addEventListener(paginatorListener[0]);
                    }));
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
            event.deferReply().queue(hook -> hook.sendMessageEmbeds(embed.build()).queue());
        }
    }
    public static String makePageProfileDescription(ArrayList<GDRecord> list, int perPage, int pageNo){
        StringBuilder s = new StringBuilder();
        for (int i = pageNo*perPage; i < perPage+(pageNo*perPage) && i < list.size(); i++){
            GDLevel level = list.get(i).level();
            String emoji = Ramble21.getEmojiName(level.getDifficulty());
            String recordDesc = (
                    i+1 + " - " + emoji + " **" + level.getName() + "** by " + level.getAuthor() + "\n" +
                            "<:length:1307507840864227468> *Attempts: " + list.get(i).attempts() + "*\n\n"
            );
            s.append(recordDesc);
        }
        return s.toString();
    }
}