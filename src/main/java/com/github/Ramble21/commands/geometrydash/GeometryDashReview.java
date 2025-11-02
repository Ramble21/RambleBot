package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.geometrydash.GDRecord;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.GeometryDashReviewButtonListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.*;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class GeometryDashReview implements Command {

    private String originalMessageId;
    private ArrayList<GDRecord> records;

    public String getOriginalMessageId() {
        return originalMessageId;
    }
    public TimerTask removeButtons;

    private GDRecord lastRecord;
    public GDRecord getLastRecord() {
        return lastRecord;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        String memberStatus = GDDatabase.getMemberStatus(Objects.requireNonNull(event.getMember()));
        boolean memberIsModerator = memberStatus.equals("moderator");
        if (!memberIsModerator) {
            event.reply("You do not have permission to run this command!").setEphemeral(true).queue();
            return;
        }

        Guild guild = Objects.requireNonNull(event.getGuild());
        records = GDDatabase.getUnverifiedRecords(guild.getIdLong());
        EmbedBuilder embed = generateNewEmbed(guild.getName());

        if (embed == null){
            event.reply("There are no levels in this server to review!").setEphemeral(true).queue();
            return;
        }
        sendEmbed(embed, event, lastRecord);
    }
    public EmbedBuilder generateNewEmbed(String guildName){
        if (records.isEmpty()) {
            return null;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Approve completions for server " + guildName);
        embed.setColor(Color.yellow);

        GDRecord currentRecord = records.remove(0);
        GDLevel currentLevel = currentRecord.level();
        String emoji = Ramble21.getEmojiName(currentLevel.getDifficulty());
        lastRecord = currentRecord;
        embed.setDescription(
            "\uD83D\uDC64 Submitter: **<@" + currentRecord.submitterID() + ">**\n" +
            "<:play:1307500271911309322> Name: **" + currentLevel.getName() + "**\n" +
            "<:creatorpoints:1434551362456129546> Creator: **" + currentLevel.getAuthor() + "**\n" +
            emoji + " Difficulty: **" + currentLevel.getDifficulty() + "**\n" +
            "<:length:1307507840864227468> Attempts: **" + currentRecord.attempts() + "**\n"
        );
        return embed;
    }

    public void sendEmbed(EmbedBuilder embed, SlashCommandInteractionEvent event, GDRecord record){
        final GeometryDashReviewButtonListener[] geometryDashReviewButtonListener = {null}; // again it has to be an array bc dumb java
           event.deferReply().queue(hook -> {
               hook.sendMessageEmbeds(embed.build())
                       .setComponents(ActionRow.of(
                               Button.of(ButtonStyle.SUCCESS, "acceptButtonGD", "Accept"),
                               Button.of(ButtonStyle.DANGER, "rejectButton", "Reject")))
                       .queue(message -> {
                           this.originalMessageId = message.getId();
                           geometryDashReviewButtonListener[0] = new GeometryDashReviewButtonListener(this, record);
                           event.getJDA().addEventListener(geometryDashReviewButtonListener[0]);
                       });
               Timer buttonTimeout = new Timer();
               removeButtons = new TimerTask() {
                   @Override
                   public void run() {
                       event.getChannel().editMessageComponentsById(originalMessageId)
                               .setComponents(ActionRow.of(
                                       Button.of(ButtonStyle.SUCCESS, "acceptButtonGD", "Accept").asDisabled(),
                                       Button.of(ButtonStyle.DANGER, "rejectButton", "Reject").asDisabled()))
                               .queue();
                       event.getJDA().removeEventListener(geometryDashReviewButtonListener[0]);
                   }
               };
               buttonTimeout.schedule(removeButtons, 60000);
           });
    }
}
