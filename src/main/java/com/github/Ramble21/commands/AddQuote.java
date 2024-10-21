package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Quote;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Objects;

public class AddQuote implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        String quote = Objects.requireNonNull(event.getOption("quote")).getAsString();
        String authorMinusFormatting = Objects.requireNonNull(event.getOption("author")).getAsString();

        User skibidi = Objects.requireNonNull(event.getOption("author")).getAsUser();
        String author = skibidi.getId();
        author = "<@" + author + ">";

        int id = Quote.getTotalQuotes() + 1;
        Quote.increaseTotalQuotes();
        Quote newQuote = new Quote(quote, author, id);

        EmbedBuilder embed = new EmbedBuilder();
        Color rambleRed = new Color(171, 43, 43);
        embed.setColor(rambleRed);
        embed.setTitle("Quote added successfully");
        embed.setDescription("Added quote: `\"" + quote + "\"` - " + author + "\n" + "This quote's ID is `" + id + "`");

        MessageEmbed finalEmbed = embed.build();
        event.replyEmbeds(finalEmbed).queue();
    }
}
