package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.Ramble21;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static com.github.Ramble21.classes.Ramble21.*;


public class TextCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw().toLowerCase());
        User user = event.getAuthor();

        if (message.equals("r!maint.on") && isBotOwner(user)) {
            if (RambleBot.maintenanceMode()) {
                event.getChannel().sendMessage("Maintenance mode is already turned on!").queue();
            }
            else {
                RambleBot.setMaintenanceMode(true);
                event.getChannel().sendMessage("Maintenance mode successfully turned on.").queue();
            }
        }
        else if (message.equals("r!maint.off") && isBotOwner(user)) {
            if (!RambleBot.maintenanceMode()) {
                event.getChannel().sendMessage("Maintenance mode is already turned off!").queue();
            }
            else {
                RambleBot.setMaintenanceMode(false);
                event.getChannel().sendMessage("Maintenance mode successfully turned off.").queue();
            }
        }
        else if (message.contains("sigma sigma on the wall")){
            event.getChannel().sendMessage("who's the skibidiest of them all").queue();
        }
        else if (message.contains("repuesta") && !isRambleBot(event.getAuthor()) && isBrainrotServer(event.getGuild())) {
            event.getChannel().sendMessage("<@" + getBrainrotterID() + "> dame repuestas").queue();
        }
    }
}
