package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.geometrydash.GDRecord;
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
        int entriesPerPage = 10;

        String description = switch(Objects.requireNonNull(buttonEvent.getComponent().getId())) {
            case "next_profile" -> {
                if (isGDProfile) {
                    ArrayList<GDRecord> list = gdProfileInstance.getRecords();
                    if (pageNo < list.size() / entriesPerPage) {
                        pageNo++;
                    }
                    yield GeometryDashProfile.makePageProfileDescription(list, entriesPerPage, pageNo);
                } else if (isGDLeaderboard) {
                    ArrayList<GDLevel> list = gdLeaderboardInstance.getLeaderboard().levels();
                    if (pageNo < list.size() / entriesPerPage) {
                        pageNo++;
                    }
                    yield GeometryDashLeaderboard.makePageLeaderboardDescription(list, entriesPerPage, pageNo, Objects.requireNonNull(buttonEvent.getGuild()).getIdLong());
                } else {
                    throw new RuntimeException();
                }
            }
            case "previous_profile" -> {
                if (pageNo > 0) {
                    pageNo--;
                }
                if (isGDProfile) {
                    ArrayList<GDRecord> list = gdProfileInstance.getRecords();
                    yield GeometryDashProfile.makePageProfileDescription(list, entriesPerPage, pageNo);
                } else if (isGDLeaderboard) {
                    ArrayList<GDLevel> list = gdLeaderboardInstance.getLeaderboard().levels();
                    yield GeometryDashLeaderboard.makePageLeaderboardDescription(list, entriesPerPage, pageNo, Objects.requireNonNull(buttonEvent.getGuild()).getIdLong());
                } else {
                    throw new RuntimeException();
                }
            }
            default -> throw new RuntimeException();
        };
        
        EmbedBuilder embedBuilder = new EmbedBuilder();
        String originalTitle = buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getTitle();
        embedBuilder.setTitle(originalTitle);
        if (description.isEmpty()){
            embedBuilder.setDescription(buttonEvent.getMessage().getEmbeds().isEmpty() ? "" : buttonEvent.getMessage().getEmbeds().get(0).getDescription());
        }
        else{
            embedBuilder.setDescription(description);
        }
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
    }
}
