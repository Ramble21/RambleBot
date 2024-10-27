package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Sentence;
import com.github.Ramble21.classes.Stopwatch;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.TypeRacerButtonListener;
import com.github.Ramble21.listeners.TypeRacerMessageListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import net.dv8tion.jda.api.utils.FileUpload;
import org.w3c.dom.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TypeRacer implements Command  {

    private String originalMessageId;
    private TextChannel originalTextChannel;
    private final Stopwatch stopwatch = new Stopwatch();

    public static List<TypeRacer> games = new ArrayList<>();

    private final Color blue = new Color(0, 122, 255);
    private final Color red = new Color(255,0, 0);
    private final Color green = new Color(0, 255, 0);
    private final Color yellow = new Color(255, 255, 0);
    private User user = null;

    private User player1;
    private User player2;

    private JDA jda;

    public User getUser(){
        return user;
    }
    public User getPlayer1(){
        return player1;
    }
    public User getPlayer2(){
        return player2;
    }
    public String getOriginalMessageId(){
        return originalMessageId;
    }
    public TextChannel getOriginalTextChannel(){
        return originalTextChannel;
    }
    public Stopwatch getStopwatch(){
        return stopwatch;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        jda = event.getJDA();
        TextChannel currentChannel = event.getChannel().asTextChannel();
        if (gameInChannel(currentChannel)){
            event.reply("There is already a game going on!").setEphemeral(true).queue();
            return;
        }
        challengeUser(event);
    }

    public boolean gameInChannel(TextChannel textChannel){
        for (TypeRacer game : games) {
            if (game.getOriginalTextChannel().equals(textChannel)) {
                return true;
            }
        }
        return false;
    }

    public void challengeUser(SlashCommandInteractionEvent event){

        games.add(this);
        user = event.getUser();
        String username = user.getEffectiveName();
        String pfpurl = user.getAvatarUrl();

        originalTextChannel = event.getChannel().asTextChannel();

        event.deferReply().queue(hook -> {
            EmbedBuilder eb = new EmbedBuilder();

            File flag = new File(("src/main/images/checkered-flag.png"));
            eb.setThumbnail("attachment://checkered-flag.png");

            eb.setTitle("TypeRacer");
            eb.setDescription(event.getUser().getAsMention() + " wants to play TypeRacer! The first person to type a random group of 20 words wins!");
            eb.setColor(blue);
            eb.setFooter(username, pfpurl);

            TypeRacerButtonListener typeRacerButtonListener = new TypeRacerButtonListener(this);
            event.getJDA().addEventListener(typeRacerButtonListener);

            hook.sendMessageEmbeds(eb.build())
                    .addFiles(FileUpload.fromData(flag, "checkered-flag.png"))
                    .addActionRow(
                            Button.success("acceptButton", "Play"),
                            Button.danger("cancelButton", "Cancel"))
                    .queue(message -> {
                            this.originalMessageId = message.getId();
                    });
        });
    }

    public void cancelGame(User buttonUser){
        System.out.println(originalMessageId + " cancel");
        EmbedBuilder cancelEmbed = new EmbedBuilder();
        cancelEmbed.setColor(red);
        cancelEmbed.setTitle("TypeRacer");
        cancelEmbed.setDescription("Game cancelled");
        cancelEmbed.setFooter(buttonUser.getEffectiveName(), buttonUser.getAvatarUrl());

        File flag = new File(("src/main/images/checkered-flag.png"));
        cancelEmbed.setThumbnail("attachment://checkered-flag.png");

        originalTextChannel.editMessageEmbedsById(this.originalMessageId, cancelEmbed.build())
                .setComponents()
                .queue();
        games.remove(this);
    }

    public void startGame(User player1, User player2) throws IOException {
        this.player1 = player1;
        this.player2 = player2;

        EmbedBuilder startEmbed = new EmbedBuilder();
        startEmbed.setColor(yellow);
        startEmbed.setTitle("TypeRacer");
        startEmbed.setDescription("**" + player2.getAsMention() + " accepted your TypeRacer challenge!** Generating words in 5 seconds!");
        startEmbed.setFooter(player1.getEffectiveName(), player1.getAvatarUrl());

        File flag = new File(("src/main/images/checkered-flag.png"));
        startEmbed.setThumbnail("attachment://checkered-flag.png");

        originalTextChannel.editMessageEmbedsById(this.originalMessageId, startEmbed.build())
                .setComponents()
                .queue();

        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        stopwatch.start();
        Sentence sentence = new Sentence();
        EmbedBuilder sentenceEmbed = new EmbedBuilder();
        sentenceEmbed.setTitle("TypeRacer");
        sentenceEmbed.setDescription("`" + sentence.getTextZwsp() + "`");
        originalTextChannel.sendMessageEmbeds(sentenceEmbed.build()).queue();
        TypeRacerMessageListener typeRacerMessageListener = new TypeRacerMessageListener(this, sentence);
        jda.addEventListener(typeRacerMessageListener);
        System.out.println(sentence.getTextRaw());
    }
}

