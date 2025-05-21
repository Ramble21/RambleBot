package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.VocabWord;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.VocabMessageListener;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Vocab implements Command {

    private String originalMessageId;
    private String flagName;

    private boolean isReversed = false;
    private boolean isReview = false;

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

    public boolean getIsReview(){
        return isReview;
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

        VocabWord vocabWord;

        if (event.getOption("onlyreview") == null || !Objects.requireNonNull(event.getOption("onlyreview")).getAsBoolean()){
            if (VocabWord.getPersonalJsonList(event.getUser(), language.toLowerCase()) == null ||
                    Objects.requireNonNull(VocabWord.getPersonalJsonList(event.getUser(), language.toLowerCase())).isEmpty()){
                vocabWord = new VocabWord(flagName, event.getUser(), false);
                System.out.println("// vocab json is empty");
            }
            else{
                int chance = (int)(1+Math.random()*2);
                if (chance == 1){
                    vocabWord = new VocabWord(flagName, event.getUser(), false);
                }
                else{
                    isReview = true;
                    vocabWord = new VocabWord(flagName, event.getUser(), true);
                }
            }
        }
        else{
            if ((VocabWord.getPersonalJsonList(event.getUser(), language.toLowerCase()) == null ||
                    Objects.requireNonNull(VocabWord.getPersonalJsonList(event.getUser(), language.toLowerCase())).isEmpty()) ){
                vocabWord = new VocabWord(flagName, event.getUser(), false);
                event.reply("You do not have any words to review!").setEphemeral(true).queue();
                games.remove(vocab);
                return;
            }
            isReview = true;
            vocabWord = new VocabWord(flagName, event.getUser(), true);
        }


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

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(RambleBot.killbotEnjoyer);
        String additionIfReview = "";

        JsonArray jsonArray;
        String filePath;
        int vocabMasteryLevel = 4;
        if (flagName.equals("spanish.png")){
            filePath = "data/json/personalvocab/spanish/" + event.getUser().getId() + ".json";
        }
        else{
            filePath = "data/json/personalvocab/french/" + event.getUser().getId() + ".json";
        }

        if (isReview){
            try {
                FileReader reader = new FileReader(filePath);
                jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                reader.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                if (object.get("word").getAsString().equals(vocabWord.getVocabWord())){
                    vocabMasteryLevel = object.get("masteryLevel").getAsInt();
                }
            }
        }

        if (isReview && vocabMasteryLevel == 1){
            additionIfReview = "\n\nYou previously got this word wrong, get it 2 more times correct in a row for mastery!";
        }
        else if (isReview && vocabMasteryLevel == 2){
            additionIfReview = "\n\nYou previously got this word wrong, get it correct 1 final time for mastery!";
        }
        else if (isReview && vocabMasteryLevel == 0){
            additionIfReview = "\n\nYou previously got this word wrong, get it 3 times correct in a row for mastery!";
        }

        if (isReversed){
            embed.setTitle(reversedTranslationsAsString);
            embed.setDescription("What is the " + language + " translation of this word? \n*You have 10 seconds to answer!*" + additionIfReview);
        }
        else{
            embed.setTitle(vocabWord.getVocabWord());
            embed.setDescription("What is the English translation of this word? \n*You have 10 seconds to answer!*" + additionIfReview);
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
        VocabMessageListener vocabMessageListener = new VocabMessageListener(this, vocabWord, timer);

        TimerTask endGame = new TimerTask(){
            @Override
            public void run(){

                JsonArray jsonArray;
                String filePath;
                boolean alreadyExists = false;
                if (getFlagName().equals("spanish.png")){
                    filePath = "data/json/personalvocab/spanish/" + event.getUser().getId() + ".json";
                }
                else{
                    filePath = "data/json/personalvocab/french/" + event.getUser().getId() + ".json";
                }
                try {
                    FileReader reader = new FileReader(filePath);
                    jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                    reader.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    if (object.get("word").getAsString().equals(vocabWord.getVocabWord())){
                        object.addProperty("masteryLevel", 0);
                        alreadyExists = true;
                    }
                }
                if (alreadyExists){
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    try{
                        FileWriter writer = new FileWriter(filePath);
                        gson.toJson(jsonArray, writer);
                        writer.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else{
                    vocabWord.writeToPersonalJson(event.getUser(), language.toLowerCase());
                }

                games.remove(vocab);
                event.getJDA().removeEventListener(vocabMessageListener);

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

        event.getJDA().addEventListener(vocabMessageListener);
        timer.schedule(endGame, 10000);
    }
}
