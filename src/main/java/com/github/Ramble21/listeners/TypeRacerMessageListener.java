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

    public TypeRacerMessageListener(TypeRacer typeRacer, Sentence sentence){
        this.typeRacer = typeRacer;
        this.sentence = sentence;
        textChannel = typeRacer.getOriginalTextChannel();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()){
            return;
        }
        else if (Anticheat.isCheated(event.getMessage().getContentRaw())){
            User winner = event.getMessage().getAuthor();
            if (!((winner.equals(typeRacer.getPlayer1()) || winner.equals(typeRacer.getPlayer2())))){
                return;
            }

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
        }

        else if (event.getMessage().getContentRaw().contains("\u200B")){
            User loser = event.getMessage().getAuthor();

            if (!((loser.equals(typeRacer.getPlayer1()) || loser.equals(typeRacer.getPlayer2())))){
                return;
            }
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
        }
    }
}
