package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Sentence;
import com.github.Ramble21.classes.Stopwatch;
import com.github.Ramble21.classes.WpmScore;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.TypeRacerButtonListener;
import com.github.Ramble21.listeners.TypeRacerMessageListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class TypeRacer implements Command  {

    private String originalMessageId;
    private TextChannel originalTextChannel;
    private final Stopwatch stopwatch = new Stopwatch();

    public static List<TypeRacer> games = new ArrayList<>();

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
    public void execute(SlashCommandInteractionEvent event){
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

        originalTextChannel = event.getChannel().asTextChannel();

        event.deferReply().queue(hook -> {
            EmbedBuilder eb = new EmbedBuilder();

            final var flag = RambleBot.class.getResourceAsStream("images/checkered-flag.png");
            eb.setThumbnail("attachment://checkered-flag.png");

            eb.setTitle("TypeRacer");
            eb.setDescription(event.getUser().getAsMention() + " wants to play TypeRacer! The first person to type a random group of 20 words wins!");
            eb.setColor(RambleBot.killbotEnjoyer);
            eb.setFooter(user.getEffectiveName(), user.getAvatarUrl());

            TypeRacerButtonListener typeRacerButtonListener = new TypeRacerButtonListener(this);
            event.getJDA().addEventListener(typeRacerButtonListener);

            assert flag != null;
            hook.sendMessageEmbeds(eb.build())
                    .addFiles(FileUpload.fromData(flag, "checkered-flag.png"))
                    .addActionRow(
                            Button.success("acceptButton", "Play"),
                            Button.danger("cancelButton", "Cancel"))
                    .queue(message -> this.originalMessageId = message.getId());
        });
    }

    public void cancelGame(User buttonUser){
        System.out.println(originalMessageId + " cancel");
        EmbedBuilder cancelEmbed = new EmbedBuilder();
        cancelEmbed.setColor(Color.red);
        cancelEmbed.setTitle("TypeRacer");
        cancelEmbed.setDescription("Game cancelled");
        cancelEmbed.setFooter(buttonUser.getEffectiveName(), buttonUser.getAvatarUrl());
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
        startEmbed.setColor(Color.yellow);
        startEmbed.setTitle("TypeRacer");
        startEmbed.setDescription("**" + player2.getAsMention() + " accepted " + RambleBot.your() + " TypeRacer challenge!** Generating words in 5 seconds!");
        startEmbed.setFooter(player1.getEffectiveName(), player1.getAvatarUrl());
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
    public void saveToJson(WpmScore wpmScore){
        try {
            for (String pathStr : new String[]{
                    "data",
                    "data/json",
                    "data/json/wpmscore"
            }) {
                Path path = Paths.get(pathStr);
                if (!Files.exists(path)) Files.createDirectory(path);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Guild guild = jda.getGuildById(wpmScore.getGuildId());
        String jsonName;
        if (guild != null) {
            jsonName = "data/json/wpmscore/" + guild.getId() + ".json";
            List<WpmScore> wpmScoreList;

            try (FileReader reader = new FileReader(jsonName)) {
                Type listType = new TypeToken<ArrayList<WpmScore>>() {}.getType();
                wpmScoreList = gson.fromJson(reader, listType);

                if (wpmScoreList == null) {
                    wpmScoreList = new ArrayList<>();
                }
            } catch (IOException e) {
                wpmScoreList = new ArrayList<>();
            }

            wpmScoreList.add(wpmScore);

            try (FileWriter writer = new FileWriter(jsonName)){
                gson.toJson(wpmScoreList,writer);
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        else{
            System.out.println("guild is null");
        }
    }
    public static ArrayList<WpmScore> getServerScores(Guild guild) {
        Gson gson = new Gson();
        String guildJson = "data/json/wpmscore/" + guild.getId() + ".json";
        Type wpmScoreType = new TypeToken<ArrayList<WpmScore>>() {}.getType();
        try (FileReader reader = new FileReader(guildJson)) {

            ArrayList<WpmScore> wpmScores = gson.fromJson(reader, wpmScoreType);
            ArrayList<WpmScore> guildScores = new ArrayList<>();

            for (WpmScore wpmScore : wpmScores) {
                Guild g = RambleBot.getJda().getGuildById(wpmScore.getGuildId());
                if (g != null && g.equals(guild)) {
                    guildScores.add(wpmScore);
                }
                else if (g == null){
                    System.out.println("g is null");
                }
            }
            return guildScores;
        }
        catch (IOException e) {
            return null;
        }
    }

}

