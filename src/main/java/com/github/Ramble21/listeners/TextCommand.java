package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.commands.Ghostping;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static com.github.Ramble21.classes.Ramble21.isRambleBot;


public class TextCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();

        if ((message.length() > 12) && Ramble21.isBrainrotServer(event.getGuild()) && (message.startsWith("r!ghostping"))){
            Ghostping ghostping = new Ghostping(event);
            ghostping.ghostping();
        }

        // Needed in order to make ghostping messages deleted successfully
        if (message.contains("ㅤㅤ") && isRambleBot(user)){
            event.getMessage().delete().queue();
        }
    }
}
