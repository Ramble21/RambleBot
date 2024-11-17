package com.github.Ramble21.commands;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.GeometryDashReviewButtonListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.Objects;



public class GeometryDashReview implements Command {

    private String originalMessageId;
    private SlashCommandInteractionEvent originalEvent;

    public String getOriginalMessageId() {
        return originalMessageId;
    }
    public SlashCommandInteractionEvent getOriginalEvent() {
        return originalEvent;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){

        this.originalEvent = event;

        if (!(Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER))
                && !(event.getUser().getId().equals("739978476651544607"))){
            event.reply("You do not have permission to run this command!").setEphemeral(true).queue();
            return;
        }

        EmbedBuilder embed = generateNewEmbed(Objects.requireNonNull(event.getGuild()));
        if (embed == null){
            event.reply("There are no levels in this server to review!").setEphemeral(true).queue();
            return;
        }
        sendEmbed(embed, event, event.getChannel(), GeometryDashLevel.getFirstInGuild(event.getGuild()));
    }
    public EmbedBuilder generateNewEmbed(Guild guild){
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Approve completions for server " + guild.getName());
        embed.setColor(Color.yellow);
        GeometryDashLevel currentLevel = GeometryDashLevel.getFirstInGuild(guild);
        lastLevel = currentLevel;
        if (currentLevel == null){
            return null;
        }
        embed.setDescription(
                "\uD83D\uDC64 Submitter: **<@" + currentLevel.getSubmitterId() + ">**\n" +
                        "<:play:1307500271911309322> Name: **" + currentLevel.getName() + "**\n" +
                        "<:star:1307518203122942024> Difficulty: **" + currentLevel.getDifficulty() + "**\n" +
                        "<:length:1307507840864227468> Attempts: **" + currentLevel.getAttempts() + "**\n");
        return embed;
    }

    private GeometryDashLevel lastLevel;

    public GeometryDashLevel getLastLevel() {
        return lastLevel;
    }

    public void sendEmbed(EmbedBuilder embed, SlashCommandInteractionEvent event, MessageChannel channel, GeometryDashLevel level){

           event.deferReply().queue(hook -> {
               hook.sendMessageEmbeds(embed.build())
                        .addActionRow(
                                Button.success("acceptButtonGD", "Accept"),
                                Button.danger("rejectButton", "Reject"))
                        .queue(message -> {
                            this.originalMessageId = message.getId();
                            GeometryDashReviewButtonListener geometryDashReviewButtonListener = new GeometryDashReviewButtonListener(this, level);
                            event.getJDA().addEventListener(geometryDashReviewButtonListener);
               });
           });
    }
}
