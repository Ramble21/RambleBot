package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Diacritics;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static com.github.Ramble21.classes.Ramble21.*;


public class TextCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw().toLowerCase());
        String messageUnedited = event.getMessage().getContentRaw();
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
        else if (message.equals("r!repues.on") && isBotOwner(user)) {
            boolean rva = modifyRepuestaServers(event.getGuild(), false);
            if (rva) {
                event.getChannel().sendMessage("Settings successfully modified for guild " + event.getGuild().getName()).queue();
            }
            else {
                event.getChannel().sendMessage("This setting was already enabled in this guild!").queue();
            }
        }
        else if (message.equals("r!repues.off") && isBotOwner(user)) {
            boolean rva = modifyRepuestaServers(event.getGuild(), true);
            if (rva) {
                event.getChannel().sendMessage("Settings successfully modified for guild " + event.getGuild().getName()).queue();
            }
            else {
                event.getChannel().sendMessage("This setting was already disabled in this guild!").queue();
            }
        }
        else if (message.contains("sigma sigma on the wall")){
            event.getChannel().sendMessage("who's the skibidiest of them all").queue();
        }
        else if (message.contains("repuesta") && !isRambleBot(event.getAuthor()) && isRepuestaServer(event.getGuild())) {
            event.getChannel().sendMessage("<@" + getBrainrotterID() + "> dame repuestas").queue();
        }
        else if (message.startsWith("r!say ") && isBotOwner(event.getAuthor())) {
            event.getMessage().delete().queue();
            if (message.length() > 6) {
                event.getChannel().sendMessage(messageUnedited.substring(6)).queue();
            }
        }
        else if (message.equals("r!angrybirds") && event.getGuild().getId().equals("931838136223412235")) {
            event.getMessage().delete().queue();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle
                    ("""
                    !! GOON ALERT. GOON ALERT !!
                  
                    GLISTERMELON HAS LOST THEIR STREAK OF __10 DAYS__ OF NO GOONING DUE TO ; `ANGRY BIRD FEET PORN`"""
                    );
            embed.setColor(RambleBot.scaryOrange);
            embed.setFooter("r!angrybirds");
            long userId = 674819147963564054L;
            event.getJDA().retrieveUserById(userId).queue(g -> {
                String avatarUrl = g.getEffectiveAvatarUrl();
                embed.setThumbnail(avatarUrl);
            });
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
