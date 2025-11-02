package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDRecord;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.commands.geometrydash.GeometryDashReview;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class GeometryDashReviewButtonListener extends ListenerAdapter {

    private final GeometryDashReview reviewInstance;
    private final GDRecord record;

    public GeometryDashReviewButtonListener(GeometryDashReview reviewInstance, GDRecord record) {
        this.reviewInstance = reviewInstance;
        this.record = record;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {
        Guild guild = Objects.requireNonNull(buttonEvent.getGuild());

        if (Ramble21.memberNotTrustedUser(Objects.requireNonNull(buttonEvent.getMember()))){
            return;
        }
        if (Objects.equals(buttonEvent.getComponent().getId(), "acceptButtonGD")){
            buttonEvent.getJDA().removeEventListener(this);
            System.out.println("Received button: acceptButtonGD");
            GDDatabase.acceptRecord(record.submitterID(), record.levelID());

            EmbedBuilder embed = reviewInstance.generateNewEmbed(guild.getName());
            if (embed == null){
                EmbedBuilder done = new EmbedBuilder();
                done.setTitle("All levels successfully reviewed!");
                done.setColor(Color.green);
                buttonEvent.editMessageEmbeds(done.build())
                        .setComponents()
                        .queue();
                return;
            }

            buttonEvent.getChannel().editMessageEmbedsById(reviewInstance.getOriginalMessageId(), embed.build()).queue();
            GeometryDashReviewButtonListener geometryDashReviewButtonListener = new GeometryDashReviewButtonListener(reviewInstance, reviewInstance.getLastRecord());
            buttonEvent.getJDA().addEventListener(geometryDashReviewButtonListener);
            buttonEvent.deferEdit().queue();
        }
        else if (Objects.equals(buttonEvent.getComponent().getId(), "rejectButton")){
            buttonEvent.getJDA().removeEventListener(this);
            System.out.println("Received button: acceptButtonGD");
            GDDatabase.deleteRecord(record.submitterID(), record.levelID());

            EmbedBuilder embed = reviewInstance.generateNewEmbed(guild.getName());
            if (embed == null){
                EmbedBuilder done = new EmbedBuilder();
                done.setTitle("All levels successfully reviewed!");
                done.setColor(Color.green);
                buttonEvent.editMessageEmbeds(done.build())
                        .setComponents()
                        .queue();
                reviewInstance.removeButtons.cancel();
                return;
            }

            buttonEvent.getChannel().editMessageEmbedsById(reviewInstance.getOriginalMessageId(), embed.build()).queue();
            GeometryDashReviewButtonListener geometryDashReviewButtonListener = new GeometryDashReviewButtonListener(reviewInstance, reviewInstance.getLastRecord());
            buttonEvent.getJDA().addEventListener(geometryDashReviewButtonListener);
            buttonEvent.deferEdit().queue();
        }
        else {
            System.out.println("GD ReviewButtonListener: something bugged or theres another command going on");
        }
    }
}
