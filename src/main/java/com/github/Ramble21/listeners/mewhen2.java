package com.github.Ramble21.listeners;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class mewhen2 extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getGuild().getId().equals("931838136223412235")){
            return;
        }
        else if (!event.getChannel().asTextChannel().getName().equalsIgnoreCase("glisterbot-errors")){
            return;
        }
        else if (!event.getAuthor().getId().equals("1283474938895798344")){
            return;
        }
        event.getMessage().addReaction(Emoji.fromCustom("Mewhen2", 940572260421476352L, false)).queue();
    }
}
