package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.commands.geometrydash.GeometryDashReview;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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

        if (!(Objects.requireNonNull(buttonEvent.getMember()).hasPermission(Permission.MANAGE_SERVER))
                && !(buttonEvent.getUser().getId().equals("739978476651544607"))){
            return;
        }

        if (Objects.equals(buttonEvent.getComponent().getId(), "acceptButtonGD")){
            buttonEvent.getJDA().removeEventListener(this);
            System.out.println("Received button: acceptButtonGD");

            GeometryDashLevel.updateModerateQueueJson(level, true);
            level.writeToPersonalJson(level.isPlatformer());

            EmbedBuilder embed = reviewInstance.generateNewEmbed(Objects.requireNonNull(buttonEvent.getGuild()));
            if (GeometryDashLevel.getModeratorQueue().isEmpty() || embed == null){
                EmbedBuilder done = new EmbedBuilder();
                done.setTitle("All levels successfully reviewed!");
                done.setColor(Color.green);
                buttonEvent.editMessageEmbeds(done.build())
                        .setComponents()
                        .queue();
                return;
            }

            System.out.println(reviewInstance.getOriginalMessageId());
            buttonEvent.getChannel().editMessageEmbedsById(reviewInstance.getOriginalMessageId(), embed.build()).queue();
            GeometryDashReviewButtonListener geometryDashReviewButtonListener = new GeometryDashReviewButtonListener(reviewInstance, reviewInstance.getLastLevel());
            buttonEvent.getJDA().addEventListener(geometryDashReviewButtonListener);
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

            buttonEvent.getChannel().editMessageEmbedsById(reviewInstance.getOriginalMessageId(), embed.build()).queue();
            GeometryDashReviewButtonListener geometryDashReviewButtonListener = new GeometryDashReviewButtonListener(reviewInstance, reviewInstance.getLastLevel());
            buttonEvent.getJDA().addEventListener(geometryDashReviewButtonListener);
            buttonEvent.deferEdit().queue();
        }
        else{
            System.out.println("something bugged or theres another command going on");
        }
    }
}
