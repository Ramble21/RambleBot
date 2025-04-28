package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.commands.Ghostping;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static com.github.Ramble21.classes.Ramble21.isBotOwner;
import static com.github.Ramble21.classes.Ramble21.isRambleBot;


public class TextCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();
        final Dotenv config;
        config = Dotenv.configure().load();
        String string1 = config.get("STRING_ONE");
        String string2 = config.get("STRING_TWO");
        if (message.equals("r!maint.on") && isBotOwner(user)) {
            RambleBot.setMaintenanceMode(true);
            event.getChannel().sendMessage("Maintenance mode successfully turned on.").queue();
        }
        else if (message.equals("r!maint.off") && isBotOwner(user)) {
            RambleBot.setMaintenanceMode(false);
            event.getChannel().sendMessage("Maintenance mode successfully turned off.").queue();
        }

        if ((message.length() > 12) && Ramble21.isBrainrotServer(event.getGuild()) && (message.startsWith("r!ghostping"))){
            Ghostping ghostping = new Ghostping(event);
            ghostping.ghostping(false);
        }
        else if ((message.length() > 14) && Ramble21.isBrainrotServer(event.getGuild()) && (message.startsWith("r!rotcerebros"))
                && !user.getId().equals("840216337119969301")
                && !user.getId().equals("710503097343934494")
                && !user.getId().equals("870078781308674098")
                && !user.getId().equals("1135014520964784128")){
            Ghostping ghostping = new Ghostping(event);
            ghostping.ghostping(true);
            System.out.println("1");
        }
        if (message.toLowerCase().contains("sigma sigma on the wall")){
            event.getChannel().sendMessage("who's the skibidiest of them all").queue();
        }
        if (Diacritics.containsOneAway(Diacritics.removeDiacritics(message.toLowerCase()), string1) && (Ramble21.isBrainrotServer(event.getGuild()) || event.getGuild().getId().equals("993983631007682620")) && !(event.getAuthor().isBot())){
            event.getChannel().asTextChannel().sendMessage(string2).queue();
        }
        // Needed in order to make ghostping messages deleted successfully
        if (message.contains("ㅤㅤ") && isRambleBot(user)){
            event.getMessage().delete().queue();
        }
    }
}
