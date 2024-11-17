package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class GrabIp implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        String user;
        if (event.getOption("member") == null){
            user = event.getUser().getId();
        }
        else{
            User skibidi = Objects.requireNonNull(event.getOption("member")).getAsUser();
            user = skibidi.getId();
        }

        int seed = Ramble21.generateSeed(user);
        String ip = Ramble21.generateIp(seed);

        user = "<@" + user + ">";

        event.reply(user + "'s IP address is " + ip).queue();
    }
}
