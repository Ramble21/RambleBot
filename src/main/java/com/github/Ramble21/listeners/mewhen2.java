package com.github.Ramble21.listeners;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class mewhen2 extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getId().equals("1283474550599974932") && event.getAuthor().getId().equals("1029237685656760352")) {
            event.getMessage().addReaction(Emoji.fromCustom("Mewhen2", 940572260421476352L, false)).queue();
        }
    }
}
