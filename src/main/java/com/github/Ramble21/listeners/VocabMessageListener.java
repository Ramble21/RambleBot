package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.VocabWord;
import com.github.Ramble21.commands.Vocab;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;

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
                    String description = "";
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.green);
                    embed.setTitle("That's correct!");

                    for (int ii = 0; ii < vocabWord.getEnglishTranslations().length; ii++){
                        if (!(vocabWord.getEnglishTranslations()[ii].equals(vocabWord.getEnglishTranslations()[i]))){
                            if (description.isEmpty()) {
                                description = vocabWord.getEnglishTranslations()[ii];
                            }
                            else{
                                description += ", vocabWord.getEnglishTranslations()[ii]";
                            }
                        }
                    }
                    if (!description.isEmpty()){
                        embed.setDescription("Other translations: `" + description + "`.");
                    }

                    event.getMessage().replyEmbeds(embed.build()).queue();

                    EmbedBuilder editedEmbed = new EmbedBuilder();
                    editedEmbed.setColor(Color.green);
                    editedEmbed.setTitle(vocabWord.getVocabWord());
                    editedEmbed.setDescription("Translated correctly by " + event.getAuthor().getAsMention() + "!");

                    var fileStream = RambleBot.class.getResourceAsStream("images/" + vocabInstance.getFlagName());
                    assert fileStream != null;
                    FileUpload fileUpload = FileUpload.fromData(fileStream, "flag.png");
                    editedEmbed.setThumbnail("attachment://flag.png");
                    MessageChannel channel = event.getChannel();
                    channel.retrieveMessageById(vocabInstance.getOriginalMessageId()).queue(originalMessage -> {
                        originalMessage.editMessageEmbeds(editedEmbed.build()).queue();
                    });
                    timer.cancel();
                    Vocab.games.remove(vocabInstance);
                }
            }
        }
        else{
            if (vocabWord.getVocabWord().equals(event.getMessage().getContentRaw().toLowerCase())){
                String description = "";
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.green);
                embed.setTitle("That's correct!");

                event.getMessage().replyEmbeds(embed.build()).queue();

                EmbedBuilder editedEmbed = new EmbedBuilder();
                editedEmbed.setColor(Color.green);
                editedEmbed.setTitle(vocabInstance.getReversedTranslationsAsString());
                editedEmbed.setDescription("Translated correctly by " + event.getAuthor().getAsMention() + "!");

                var fileStream = RambleBot.class.getResourceAsStream("images/" + vocabInstance.getFlagName());
                assert fileStream != null;
                FileUpload fileUpload = FileUpload.fromData(fileStream, "flag.png");
                editedEmbed.setThumbnail("attachment://flag.png");
                MessageChannel channel = event.getChannel();
                channel.retrieveMessageById(vocabInstance.getOriginalMessageId()).queue(originalMessage -> {
                    originalMessage.editMessageEmbeds(editedEmbed.build()).queue();
                });
                timer.cancel();
                Vocab.games.remove(vocabInstance);
            }
        }
    }
}
