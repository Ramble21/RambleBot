package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class RizzRater implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        User skibidi = Objects.requireNonNull(event.getOption("member")).getAsUser();
        String user = skibidi.getId();
        System.out.println(user);

        int seed = Ramble21.generateSeed(user);
        int ballzakz = Ramble21.generateRizz(seed);

        user = "<@" + user + ">";

        event.reply(user + "'s " + Ramble21.rateRizz(ballzakz)).queue();
    }
}
