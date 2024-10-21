package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Quote;
import com.github.Ramble21.classes.QuoteManager;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Objects;

public class DeleteQuote implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        Member member = event.getMember();
        int id = Objects.requireNonNull(event.getOption("id")).getAsInt();
        Quote bad = QuoteManager.findById(id);
        assert member != null;

        // only administrators plus bot owner can run command
        if (!(member.hasPermission(net.dv8tion.jda.api.Permission.ADMINISTRATOR)) && !(member.getId().equals("739978476651544607"))) {
            event.reply("You do not have permission to run this command!").setEphemeral(true).queue();
        }
        else if (bad == null){
            event.reply("This quote doesn't exist!").setEphemeral(true).queue();
        }
        else{
            QuoteManager.deleteObject(bad);
            event.reply("Quote ID " + id + " has successfully been deleted").queue();
        }
    }
}
