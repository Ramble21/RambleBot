package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.geometrydash.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.commands.geometrydash.GeometryDashLeaderboard;
import com.github.Ramble21.commands.geometrydash.GeometryDashProfile;
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
    private GeometryDashLeaderboard gdLeaderboardInstance;
    private final boolean isGDLeaderboard;
    private final boolean isGDProfile;
    private final String messageId;


    public PaginatorListener(GeometryDashProfile gdProfileInstance, String messageId){
        this.gdProfileInstance = gdProfileInstance;
        this.messageId = messageId;
        isGDLeaderboard = false;
        isGDProfile = true;
    }
    public PaginatorListener(GeometryDashProfile gdProfileInstance, String messageId, int startingPage){
        this.gdProfileInstance = gdProfileInstance;
        this.messageId = messageId;
        this.pageNo = startingPage;
        isGDLeaderboard = false;
        isGDProfile = true;
    }

    public PaginatorListener(GeometryDashLeaderboard gdLeaderboardInstance, String messageId){
        this.gdLeaderboardInstance = gdLeaderboardInstance;
        this.messageId = messageId;
        isGDLeaderboard = true;
        isGDProfile = false;
    }
    public PaginatorListener(GeometryDashLeaderboard gdLeaderboardInstance, String messageId, int startingPage){
        this.gdLeaderboardInstance = gdLeaderboardInstance;
        this.messageId = messageId;
        this.pageNo = startingPage;
        isGDLeaderboard = true;
        isGDProfile = false;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {

        if (!(buttonEvent.getMessageId().equals(messageId))){
            return;
        }
        switch(Objects.requireNonNull(buttonEvent.getComponent().getId())) {
            case "next_profile":
            {


                ArrayList<GeometryDashLevel> list;
                String description;
                if (isGDProfile){
                    list = GeometryDashLevel.getPersonalJsonList(gdProfileInstance.getMember().getUser(), gdProfileInstance.isPlatformer());
                    assert list != null;
                    Ramble21.sortByEstimatedDiff(list, false);
                    if (pageNo < list.size()/10) pageNo++;
                    description = GeometryDashProfile.makePageProfileDescription(list, 10, pageNo);
                }
                else if (isGDLeaderboard){
                    list = GeometryDashLevel.getGuildJsonList(gdLeaderboardInstance.getGuild(), gdLeaderboardInstance.isPlatformer());
                    assert list != null;
                    Ramble21.sortByEstimatedDiff(list, true);
                    if (pageNo < list.size()/10) pageNo++;
                    description = GeometryDashLeaderboard.makePageLeaderboardDescription(list, 10, pageNo, buttonEvent.getGuild(), gdLeaderboardInstance.isPlatformer());
                }
                else{
                    throw new RuntimeException();
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();

                String originalTitle = buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getTitle();
                embedBuilder.setTitle(originalTitle);

                if (description.isEmpty()){
                    embedBuilder.setDescription(buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getDescription());
                }
                else{
                    embedBuilder.setDescription(description);
                }
                embedBuilder.setColor(Color.yellow);
                buttonEvent.editMessageEmbeds(embedBuilder.build()).queue();

                buttonEvent.getJDA().removeEventListener(this);

                PaginatorListener replacement;
                if (isGDProfile){
                    replacement = new PaginatorListener(gdProfileInstance, messageId, pageNo);
                }
                else {
                    replacement = new PaginatorListener(gdLeaderboardInstance, messageId, pageNo);
                }
                buttonEvent.getJDA().addEventListener(replacement);
                break;
            }
            case "previous_profile":
            {
                if (pageNo > 0){
                    pageNo--;
                }

                ArrayList<GeometryDashLevel> list;
                String description;
                if (isGDProfile){
                    list = GeometryDashLevel.getPersonalJsonList(gdProfileInstance.getMember().getUser(), gdProfileInstance.isPlatformer());
                    assert list != null;
                    Ramble21.sortByEstimatedDiff(list, false);
                    description = GeometryDashProfile.makePageProfileDescription(list, 10, pageNo);
                }
                else if (isGDLeaderboard){
                    list = GeometryDashLevel.getGuildJsonList(gdLeaderboardInstance.getGuild(), gdLeaderboardInstance.isPlatformer());
                    assert list != null;
                    Ramble21.sortByEstimatedDiff(list, true);
                    description = GeometryDashLeaderboard.makePageLeaderboardDescription(list, 10, pageNo, buttonEvent.getGuild(), gdLeaderboardInstance.isPlatformer());
                }
                else{
                    throw new RuntimeException();
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();

                String originalTitle = buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getTitle();
                embedBuilder.setTitle(originalTitle);

                if (description.isEmpty()){
                    embedBuilder.setDescription(buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getDescription());
                }
                else{
                    embedBuilder.setDescription(description);
                }

                embedBuilder.setColor(Color.yellow);
                buttonEvent.editMessageEmbeds(embedBuilder.build()).queue();

                buttonEvent.getJDA().removeEventListener(this);
                PaginatorListener replacement;
                if (isGDProfile){
                    replacement = new PaginatorListener(gdProfileInstance, messageId, pageNo);
                }
                else {
                    replacement = new PaginatorListener(gdLeaderboardInstance, messageId, pageNo);
                }
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
