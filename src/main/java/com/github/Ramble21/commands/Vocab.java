package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.VocabWord;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.VocabMessageListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Vocab implements Command {

    private String originalMessageId;
    private String flagName;
    private boolean isReversed = false;
    private String reversedTranslationsAsString;
    private MessageChannel messageChannel;
    private Vocab vocab;

    public MessageChannel getMessageChannel() {
        return messageChannel;
    }

    public String getOriginalMessageId() {
        return originalMessageId;
    }

    public String getFlagName() {
        return flagName;
    }

    public String getReversedTranslationsAsString() {
        return reversedTranslationsAsString;
    }

    public boolean getIsReversed(){
        return isReversed;
    }

    public static ArrayList<Vocab> games = new ArrayList<>();

    @Override
    public void execute(SlashCommandInteractionEvent event){

        this.messageChannel = event.getMessageChannel();
        for (Vocab game : games){
            if (game.getMessageChannel() == this.messageChannel){
                event.reply("There is already a game going on in this channel!").setEphemeral(true).queue();
                return;
            }
        }

        vocab = this; // i don't know why i need to do this but i do for some reason
        games.add(vocab);

        flagName = Objects.requireNonNull(event.getOption("language")).getAsString().toLowerCase() + ".png";
        String language;
        if (flagName.equals("french.png")){
            language = "French";
        }
        else{
            language = "Spanish";
        }
        var fileStream = RambleBot.class.getResourceAsStream("images/" + flagName);
        VocabWord vocabWord = new VocabWord(flagName);

        int num = (int)(1+Math.random()*2);
        if (num == 1){
            isReversed = true;
            for (int i = 0; i < vocabWord.getEnglishTranslations().length; i++){
                if (i == 0){
                    reversedTranslationsAsString = vocabWord.getEnglishTranslations()[i];
                }
                else{
                    reversedTranslationsAsString += ", " + vocabWord.getEnglishTranslations()[i];
                }
            }
        }

        Color blue = new Color(0, 122, 255);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(blue);
        if (isReversed){
            embed.setTitle(reversedTranslationsAsString);
            embed.setDescription("What is the " + language + " translation of this word? \n*You have 10 seconds to answer!*");
        }
        else{
            embed.setTitle(vocabWord.getVocabWord());
            embed.setDescription("What is the English translation of this word? \n*You have 10 seconds to answer!*");
        }
        embed.setThumbnail("attachment://flag.png");
        assert fileStream != null;

        CompletableFuture<Void> future1 = new CompletableFuture<>();
        event.replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(fileStream, "flag.png"))
                .queue( interactionHook -> {
                    interactionHook.retrieveOriginal().queue(sentMessage -> {
                        originalMessageId = sentMessage.getId();
                        future1.complete(null);
                    });
                });
        future1.join();

        Timer timer = new Timer();

        TimerTask endGame = new TimerTask(){
            @Override
            public void run(){

                games.remove(vocab);
                EmbedBuilder editedEmbed = new EmbedBuilder();
                if (!isReversed){
                    editedEmbed.setTitle(vocabWord.getVocabWord());
                }
                else{
                    editedEmbed.setTitle(reversedTranslationsAsString);
                }
                editedEmbed.setDescription("Nobody answered correctly in time!");
                editedEmbed.setThumbnail("attachment://flag.png");
                editedEmbed.setColor(Color.red);

                var fileStream = RambleBot.class.getResourceAsStream("images/" + flagName);
                assert fileStream != null;
                FileUpload fileUpload = FileUpload.fromData(fileStream, "flag.png");

                event.getChannel().editMessageEmbedsById(originalMessageId, editedEmbed.build()).queue();

                EmbedBuilder correctionEmbed = new EmbedBuilder();
                correctionEmbed.setTitle("Nobody answered correctly in time!");
                correctionEmbed.setColor(Color.red);
                if (!isReversed){
                    if (vocabWord.getEnglishTranslations().length == 1){
                        correctionEmbed.setDescription("The correct translation was: `" + vocabWord.getEnglishTranslations()[0] + "`.");
                    }
                    else if (vocabWord.getEnglishTranslations().length == 2){
                        correctionEmbed.setDescription("The correct translations were: `" + vocabWord.getEnglishTranslations()[0] + ", " + vocabWord.getEnglishTranslations()[1] + "`.");
                    }
                    else{
                        correctionEmbed.setDescription("The correct translations were: `" + vocabWord.getEnglishTranslations()[0] + ", " + vocabWord.getEnglishTranslations()[1] + ", " + vocabWord.getEnglishTranslations()[2] + "`.");
                    }
                }
                else{
                    correctionEmbed.setDescription("The correct translation was: `" + vocabWord.getVocabWord() + "`");
                }
                MessageChannel channel = event.getChannel();
                channel.retrieveMessageById(originalMessageId).queue(originalMessage -> {
                    originalMessage.replyEmbeds(correctionEmbed.build()).queue();
                });
            }
        };
        VocabMessageListener vocabMessageListener = new VocabMessageListener(this, vocabWord, timer);
        event.getJDA().addEventListener(vocabMessageListener);
        timer.schedule(endGame, 10000);
    }
}
