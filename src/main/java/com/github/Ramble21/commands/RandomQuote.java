package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Quote;
import com.github.Ramble21.classes.QuoteManager;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.Objects;

public class RandomQuote implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        int quoteNo = (int)(1+Math.random()*Quote.getTotalQuotes());
        System.out.println(quoteNo);
        Quote quote = QuoteManager.findById(quoteNo);

        if (quote == null){
            event.reply("<@739978476651544607> theres a bug in ur code lol (quote = null)").queue();
        }
        else{
            String text = "\"" + quote.getQuote() + "\" - " + quote.getAuthor() + "\n`Quote ID = " + quote.getId() + "`";
            event.reply(text).queue();
        }
    }
}
