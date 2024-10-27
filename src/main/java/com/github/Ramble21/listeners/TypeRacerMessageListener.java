package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.Anticheat;
import com.github.Ramble21.classes.Sentence;
import com.github.Ramble21.commands.TypeRacer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.github.Ramble21.commands.TypeRacer.games;

public class TypeRacerMessageListener extends ListenerAdapter {
    private final TypeRacer typeRacer;
    private final Sentence sentence;
    private final TextChannel textChannel;
    private boolean hasReplied = false;
    private final User user1;
    private final User user2;

    public TypeRacerMessageListener(TypeRacer typeRacer, Sentence sentence){
        this.typeRacer = typeRacer;
        this.sentence = sentence;
        textChannel = typeRacer.getOriginalTextChannel();
        user1 = typeRacer.getPlayer1();
        user2 = typeRacer.getPlayer2();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()){
            return;
        }
        if (hasReplied){
            return;
        }
        if (!(event.getAuthor().equals(user1) || event.getAuthor().equals(user2))){
            return;
        }
        else if (event.getMessage().getContentRaw().equalsIgnoreCase(sentence.getTextRaw())){
            User winner = event.getMessage().getAuthor();
            int charCount = sentence.getCharacterCount();
            typeRacer.getStopwatch().stop();
            int timeInMs = typeRacer.getStopwatch().getElapsedTime();
            double timeInM = ((double)timeInMs) / 60000;
            int wpm = (int)(Math.round(((double)charCount/5)/(timeInM)));

            EmbedBuilder winEmbed = new EmbedBuilder();
            winEmbed.setTitle(winner.getEffectiveName() + ", you are the winner! :tada:");
            winEmbed.setDescription("You typed the sentence faster than your opponent, at a speed of " + wpm + " WPM! Congratulations!");
            winEmbed.setColor(Color.green);
            winEmbed.setImage(winner.getAvatarUrl());
            textChannel.sendMessageEmbeds(winEmbed.build()).queue();
            games.remove(typeRacer);
            hasReplied = true;
        }

        else if (Anticheat.isCheated(event.getMessage().getContentRaw())){

            User loser = event.getMessage().getAuthor();
            System.out.println(typeRacer.getPlayer1().getEffectiveName());
            System.out.println(typeRacer.getPlayer2().getEffectiveName());

            User winner;
            if (loser.equals(typeRacer.getPlayer2())){
                winner = typeRacer.getPlayer1();
            }
            else{
                winner = typeRacer.getPlayer2();
            }
            typeRacer.getStopwatch().stop();
            EmbedBuilder winEmbed = new EmbedBuilder();
            winEmbed.setTitle(winner.getEffectiveName() + ", you are the winner! :tada:");
            winEmbed.setDescription("Your dirty opponent " + loser.getAsMention() + " tried to use copy and paste to cheat the contest, so you won for free!");
            winEmbed.setColor(Color.green);
            winEmbed.setImage(winner.getAvatarUrl());
            textChannel.sendMessageEmbeds(winEmbed.build()).queue();
            games.remove(typeRacer);
            hasReplied = true;
        }
    }
}
