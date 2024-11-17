package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.commands.GeometryDashReview;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class GeometryDashReviewButtonListener extends ListenerAdapter {

    private final GeometryDashReview reviewInstance;
    private final GeometryDashLevel level;

    public GeometryDashReviewButtonListener(GeometryDashReview reviewInstance, GeometryDashLevel level) {
        this.reviewInstance = reviewInstance;
        this.level = level;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {

        if (Objects.equals(buttonEvent.getComponent().getId(), "acceptButtonGD")){
            buttonEvent.getJDA().removeEventListener(this);
            System.out.println("Received button: acceptButtonGD");

            GeometryDashLevel.updateModerateQueueJson(level, true);
            level.writeToPersonalJson();
            EmbedBuilder embed = reviewInstance.generateNewEmbed(Objects.requireNonNull(buttonEvent.getGuild()));
            if (GeometryDashLevel.getModeratorQueue().isEmpty()){
                EmbedBuilder done = new EmbedBuilder();
                done.setTitle("All levels successfully reviewed!");
                done.setColor(Color.green);
                buttonEvent.editMessageEmbeds(done.build())
                        .setComponents()
                        .queue();
                return;
            }

            reviewInstance.sendEmbed(embed, reviewInstance.getOriginalEvent(), reviewInstance.getOriginalEvent().getChannel(), GeometryDashLevel.getFirstInGuild(reviewInstance.getOriginalEvent().getGuild()));
            buttonEvent.deferEdit().queue();
        }
        else if (Objects.equals(buttonEvent.getComponent().getId(), "rejectButton")){
            buttonEvent.getJDA().removeEventListener(this);
            System.out.println("Received button: rejectButton");

            GeometryDashLevel.updateModerateQueueJson(level, true);
            EmbedBuilder embed = reviewInstance.generateNewEmbed(Objects.requireNonNull(buttonEvent.getGuild()));
            if (GeometryDashLevel.getModeratorQueue().isEmpty()){
                EmbedBuilder done = new EmbedBuilder();
                done.setTitle("All levels successfully reviewed!");
                done.setColor(Color.green);
                buttonEvent.editMessageEmbeds(done.build())
                        .setComponents()
                        .queue();
                return;
            }
            reviewInstance.sendEmbed(embed, reviewInstance.getOriginalEvent(), reviewInstance.getOriginalEvent().getChannel(), GeometryDashLevel.getFirstInGuild(reviewInstance.getOriginalEvent().getGuild()));
            buttonEvent.deferEdit().queue();
        }
        else{
            System.out.println("something bugged or theres another command going on");
        }
    }
}
