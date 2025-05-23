package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.GeometryDashRecord;
import com.github.Ramble21.classes.Ramble21;
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
    private final GeometryDashRecord record;

    public GeometryDashReviewButtonListener(GeometryDashReview reviewInstance, GeometryDashRecord record) {
        this.reviewInstance = reviewInstance;
        this.record = record;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {

        if (!(Objects.requireNonNull(buttonEvent.getMember()).hasPermission(Permission.MANAGE_SERVER))
                && !(Ramble21.isBotOwner(buttonEvent.getUser()))){
            return;
        }
        String buttonID = buttonEvent.getComponentId();
        if (buttonID.equals("rejectButton") || buttonID.equals("acceptButton")){
            buttonEvent.getJDA().removeEventListener(this);

            record.removeFromModeratorQueue();
            if (buttonID.equals("acceptButton")) {
                record.writeToPersonalJSON();
            }

            EmbedBuilder embed = reviewInstance.generateNewEmbed(Objects.requireNonNull(buttonEvent.getGuild()));
            if (GeometryDashRecord.getModeratorQueue().isEmpty() || embed == null){
                EmbedBuilder done = new EmbedBuilder();
                done.setTitle("All levels successfully reviewed!");
                done.setColor(Color.green);
                buttonEvent.editMessageEmbeds(done.build())
                        .setComponents()
                        .queue();
            }
            else {
                buttonEvent.getChannel().editMessageEmbedsById(reviewInstance.getOriginalMessageId(), embed.build()).queue();
                GeometryDashReviewButtonListener buttonListener = new GeometryDashReviewButtonListener(reviewInstance, GeometryDashRecord.getFirstQueuedInGuild(buttonEvent.getGuild()));
                buttonEvent.getJDA().addEventListener(buttonListener);
                buttonEvent.deferEdit().queue();
            }
        }

    }
}
