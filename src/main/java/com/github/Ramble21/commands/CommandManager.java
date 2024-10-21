package com.github.Ramble21.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandManager extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName(); // gets name of command minus the slash

        if (command.equalsIgnoreCase("grab-ip")){
            User skibidi = Objects.requireNonNull(event.getOption("member")).getAsUser();
            String user = skibidi.getId();
            user = "<@" + user + ">";

            UserIP userip = new UserIP(event.getUser());
            String ip = userip.getIp();
            event.reply(user + "'s IP address is " + ip).queue();
        }
        else if (command.equalsIgnoreCase("rizz-rater")){
            User skibidi = Objects.requireNonNull(event.getOption("member")).getAsUser();
            String user = skibidi.getId();
            user = "<@" + user + ">";

            int ballzakz = (int)(1+Math.random()*10);
            event.reply(user + "'s " + Ramble21.rateRizz(ballzakz)).queue();
        }
        else if (command.equalsIgnoreCase("add-quote")) {

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
        else if (command.equalsIgnoreCase("delete-quote")) {
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
        else if (command.equalsIgnoreCase("random-quote")) {
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

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("grab-ip", "Grabs the totally real ip of the member that you choose")
                .addOption(OptionType.USER, "member", "Member to grab IP of", true));

        commandData.add(Commands.slash("rizz-rater", "Rate's a server member's rizz on a scale of 1 to 10")
                .addOption(OptionType.USER, "member", "Member to get rizz level of", true));

        commandData.add(Commands.slash("add-quote", "Add a server quote")
                .addOption(OptionType.STRING, "quote", "Quote that is being added", true)
                .addOption(OptionType.USER, "author", "Person who is being quoted", true));

        commandData.add(Commands.slash("random-quote", "Show a random server quote"));

        commandData.add(Commands.slash("delete-quote", "Deletes a server quote")
                .addOption(OptionType.INTEGER, "id", "ID of quote that is being deleted", true));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}
