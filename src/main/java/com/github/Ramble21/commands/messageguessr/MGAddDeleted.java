package com.github.Ramble21.commands.messageguessr;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MGAddDeleted implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        long userId;
        try {
            userId = Long.parseLong(Objects.requireNonNull(event.getOption("user-id")).getAsString());
        } catch (NumberFormatException e) {
            event.reply("Invalid user ID!").queue();
            return;
        }

        String username = Objects.requireNonNull(event.getOption("username")).getAsString();
        String effectiveName = Objects.requireNonNull(event.getOption("effective-name")).getAsString();
        MGUser user = new MGUser(userId, username, effectiveName, effectiveName);

        ArrayList<MGUser> deletedUsers = MessageGuessr.getManualDeletedUsers();

        boolean alreadySet = true;
        if (!deletedUsers.contains(user)) {
            MessageGuessr.addManualDeletedUser(deletedUsers, user);
            deletedUsers.add(user);
            alreadySet = false;
        }

        System.out.println(deletedUsers);

        StringBuilder desc = new StringBuilder("Registered deleted accounts:\n");
        for (MGUser u : deletedUsers) {
            desc.append("ID ").append(u.idLong()).append(" -> ").append(u.effectiveName()).append(" (").append(u.username()).append(")\n");
        }
        desc.deleteCharAt(desc.length() - 1);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(alreadySet ? "This deleted account is already registered!" : "Alt successfully configured!");
        eb.setColor(RambleBot.killbotEnjoyer);
        eb.setDescription(desc.toString());
        event.replyEmbeds(eb.build()).queue();
    }
}
