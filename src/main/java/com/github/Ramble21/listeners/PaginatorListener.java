package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.commands.GeometryDashProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class PaginatorListener extends ListenerAdapter {

    private int pageNo = 0;
    private GeometryDashProfile gdProfileInstance;
    private String messageId;

    public PaginatorListener( /* for the future */ ){

    }
    public PaginatorListener(GeometryDashProfile gdProfileInstance, String messageId){
        this.gdProfileInstance = gdProfileInstance;
        this.messageId = messageId;
    }
    public PaginatorListener(GeometryDashProfile gdProfileInstance, String messageId, int startingPage){
        this.gdProfileInstance = gdProfileInstance;
        this.messageId = messageId;
        this.pageNo = startingPage;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {
        if (!(buttonEvent.getMessageId().equals(messageId))){
            return;
        }
        switch(Objects.requireNonNull(buttonEvent.getComponent().getId())) {
            case "next_profile":
            {
                ArrayList<GeometryDashLevel> list = GeometryDashLevel.getPersonalJsonList(gdProfileInstance.getMember().getUser(), gdProfileInstance.isPlatformer());

                assert list != null;
                Ramble21.sortByEstimatedDiff(list);

                pageNo++;

                EmbedBuilder embedBuilder = new EmbedBuilder();

                String originalTitle = buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getTitle();
                embedBuilder.setTitle(originalTitle);

                String description = GeometryDashProfile.makePageProfileDescription(list, 10, pageNo);
                if (description.isEmpty()){
                    embedBuilder.setDescription(buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getDescription());
                }
                else{
                    embedBuilder.setDescription(description);
                }
                embedBuilder.setColor(Color.yellow);
                buttonEvent.editMessageEmbeds(embedBuilder.build()).queue();

                buttonEvent.getJDA().removeEventListener(this);
                PaginatorListener replacement = new PaginatorListener(gdProfileInstance, messageId, pageNo);
                buttonEvent.getJDA().addEventListener(replacement);
                break;
            }
            case "previous_profile":
            {
                ArrayList<GeometryDashLevel> list = GeometryDashLevel.getPersonalJsonList(gdProfileInstance.getMember().getUser(), gdProfileInstance.isPlatformer());
                if (pageNo > 0){
                    pageNo--;
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();

                String originalTitle = buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getTitle();
                embedBuilder.setTitle(originalTitle);

                String description = GeometryDashProfile.makePageProfileDescription(list, 10, pageNo);
                if (description.isEmpty()){
                    embedBuilder.setDescription(buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getDescription());
                }
                else{
                    embedBuilder.setDescription(description);
                }

                embedBuilder.setColor(Color.yellow);
                buttonEvent.editMessageEmbeds(embedBuilder.build()).queue();

                buttonEvent.getJDA().removeEventListener(this);
                PaginatorListener replacement = new PaginatorListener(gdProfileInstance, messageId, pageNo);
                buttonEvent.getJDA().addEventListener(replacement);
                break;
            }
            default:
            {
                System.out.println("Unhandled button ID: " + buttonEvent.getComponent().getId());
                break;
            }
        }
    }
}
