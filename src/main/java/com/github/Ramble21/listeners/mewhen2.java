package com.github.Ramble21.listeners;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class mewhen2 extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getGuild().getId().equals("931838136223412235")){
            return;
        }
        else if (!event.getChannel().asTextChannel().getName().contains("glisterbot-errors")){
            return;
        }
        else if (!(event.getAuthor().getId().equals("1283474938895798344") || !event.getAuthor().getId().equals("1029237685656760352"))){
            return;
        }
        else{
            String emojiName = "Mewhen2";
            long id = 940572260421476352L;
            if (LocalDate.now().getMonth().toString().equalsIgnoreCase("DECEMBER")){
                emojiName = "jolly_mewhen2";
                id = 1309966603412049920L;
            }
            event.getMessage().addReaction(Emoji.fromCustom(emojiName, id, false)).queue();
        }
    }
}
