package com.github.Ramble21.commands.messageguessr;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class MGConfigAlt implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        long alt;
        try {
            alt = Long.parseLong(Objects.requireNonNull(event.getOption("alt-userid")).getAsString());
        } catch (NumberFormatException e) {
            event.reply("Invalid user ID!").queue();
            return;
        }
        User main = Objects.requireNonNull(event.getOption("main")).getAsUser();
        HashMap<Long, Long> altsMap = MessageGuessr.getAltMap();

        boolean alreadySet = true;
        if (altsMap.get(alt) == null || altsMap.get(alt) != main.getIdLong()) {
            MessageGuessr.addAltToMap(altsMap, alt, main.getIdLong());
            alreadySet = false;
        }

        StringBuilder desc = new StringBuilder("Configured alt accounts (alt -> main):\n");
        for (long altId : altsMap.keySet()) {
            if (altId != altsMap.get(altId) && altsMap.get(altId) != 1295872060341616640L) {
                desc.append("<@").append(altId).append("> -> <@").append(altsMap.get(altId)).append(">\n");
            }
        }
        desc.deleteCharAt(desc.length() - 1);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(alreadySet ? "This alt is already configured to user " + main.getAsTag() + "!" : "Alt successfully configured!");
        eb.setColor(RambleBot.killbotEnjoyer);
        eb.setDescription(desc.toString());
        event.replyEmbeds(eb.build()).queue();
    }
}
