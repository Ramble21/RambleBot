package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.VocabWord;
import com.github.Ramble21.commands.Vocab;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class VocabMessageListener extends ListenerAdapter {
    private final Vocab vocabInstance;
    private final VocabWord vocabWord;
    private final Timer timer;

    public VocabMessageListener(Vocab vocabInstance, VocabWord vocabWord, Timer timer) {
        this.vocabInstance = vocabInstance;
        this.vocabWord = vocabWord;
        this.timer = timer;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!vocabInstance.getIsReversed()){
            for (int i = 0; i < vocabWord.getEnglishTranslations().length; i++){

                if (vocabWord.getEnglishTranslations()[i].equals(event.getMessage().getContentRaw().toLowerCase())){
                    boolean shouldCongratulate = false;
                    if (vocabInstance.getIsReview()){
                        shouldCongratulate = updateMasteryLevel(event, vocabWord, vocabInstance);
                    }

                    StringBuilder otherTranslations = new StringBuilder();
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.green);
                    embed.setTitle("That's correct!");

                    for (int ii = 0; ii < vocabWord.getEnglishTranslations().length; ii++){
                        if (!(vocabWord.getEnglishTranslations()[ii].equals(vocabWord.getEnglishTranslations()[i]))){
                            if (otherTranslations.isEmpty()) {
                                otherTranslations = new StringBuilder(vocabWord.getEnglishTranslations()[ii]);
                            }
                            else{
                                otherTranslations.append(", ").append(vocabWord.getEnglishTranslations()[ii]);
                            }
                        }
                    }

                    if (shouldCongratulate && (otherTranslations.isEmpty())){
                        embed.setDescription("You mastered the verb `" + vocabWord.getVocabWord() +"`!");
                    }
                    else if (shouldCongratulate){
                        embed.setDescription("You mastered the verb `" + vocabWord.getVocabWord() +"`!" + "\n\nOther translations: `" + otherTranslations + "`.");
                    }
                    else if (!otherTranslations.isEmpty()){
                        embed.setDescription("Other translations: `" + otherTranslations + "`.");
                    }
                    event.getMessage().replyEmbeds(embed.build()).queue();

                    EmbedBuilder editedEmbed = new EmbedBuilder();
                    editedEmbed.setColor(Color.green);
                    editedEmbed.setTitle(vocabWord.getVocabWord());
                    editedEmbed.setDescription("Translated correctly by " + event.getAuthor().getAsMention() + "!");

                    var fileStream = RambleBot.class.getResourceAsStream("images/" + vocabInstance.getFlagName());
                    assert fileStream != null;
                    editedEmbed.setThumbnail("attachment://flag.png");
                    MessageChannel channel = event.getChannel();
                    channel.retrieveMessageById(vocabInstance.getOriginalMessageId()).queue(originalMessage -> {
                        originalMessage.editMessageEmbeds(editedEmbed.build()).queue();
                    });
                    timer.cancel();
                    Vocab.games.remove(vocabInstance);
                    event.getJDA().removeEventListener(this);
                }
            }
        }
        else{
            if (vocabWord.getVocabWord().equals(event.getMessage().getContentRaw().toLowerCase())) {

                boolean shouldCongratulate = false;
                if (vocabInstance.getIsReview()){
                    shouldCongratulate = updateMasteryLevel(event, vocabWord, vocabInstance);
                }

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.green);
                embed.setTitle("That's correct!");
                if (shouldCongratulate){
                    embed.setDescription("You mastered the verb `" + vocabWord.getVocabWord() +"`!");
                }
                event.getMessage().replyEmbeds(embed.build()).queue();

                EmbedBuilder editedEmbed = new EmbedBuilder();
                editedEmbed.setColor(Color.green);
                editedEmbed.setTitle(vocabInstance.getReversedTranslationsAsString());
                editedEmbed.setDescription("Translated correctly by " + event.getAuthor().getAsMention() + "!");

                var fileStream = RambleBot.class.getResourceAsStream("images/" + vocabInstance.getFlagName());
                assert fileStream != null;
                editedEmbed.setThumbnail("attachment://flag.png");
                MessageChannel channel = event.getChannel();
                channel.retrieveMessageById(vocabInstance.getOriginalMessageId()).queue(originalMessage -> {
                    originalMessage.editMessageEmbeds(editedEmbed.build()).queue();
                });
                timer.cancel();
                Vocab.games.remove(vocabInstance);
                event.getJDA().removeEventListener(this);
            }
        }
    }

    public static boolean updateMasteryLevel(MessageReceivedEvent event, VocabWord vocabWord, Vocab vocabInstance){
        // returns true if follow-up message should congratulate
        boolean returnValue = false;
        JsonArray jsonArray;
        String filePath;
        if (vocabInstance.getFlagName().equals("spanish.png")){
            filePath = "data/json/personalvocab/spanish/" + event.getAuthor().getId() + ".json";
        }
        else{
            filePath = "data/json/personalvocab/french/" + event.getAuthor().getId() + ".json";
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
                object.addProperty("masteryLevel", object.get("masteryLevel").getAsInt()+1);
                if (object.get("masteryLevel").getAsInt() >= 3){
                   jsonArray.remove(jsonArray.get(i));
                   returnValue = true;
                }
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try{
            FileWriter writer = new FileWriter(filePath);
            gson.toJson(jsonArray, writer);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return returnValue;
    }
}
