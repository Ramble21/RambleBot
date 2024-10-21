package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Quote;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Objects;

public class RizzRater implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        User skibidi = Objects.requireNonNull(event.getOption("member")).getAsUser();
        String user = skibidi.getId();
        user = "<@" + user + ">";

        int ballzakz = (int)(1+Math.random()*10);
        event.reply(user + "'s " + Ramble21.rateRizz(ballzakz)).queue();
    }
}
