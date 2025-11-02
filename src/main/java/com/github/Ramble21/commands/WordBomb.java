package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.WordBombPlayer;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.WordBombButtonListener;
import com.github.Ramble21.listeners.WordBombMessageListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.*;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;


import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

public class WordBomb implements Command {

    public final static HashSet<String> activeChannelIDs = new HashSet<>();

    private HashSet<String> dictionary;
    private ArrayList<String> prompts;

    public final ArrayList<WordBombPlayer> players = new ArrayList<>();
    public final HashSet<String> usedWords = new HashSet<>();
    public int currentPlayerIndex;
    public User host;
    public MessageChannel channel;
    public Guild guild;


    public final int STARTING_LIVES = 3;
    private final int TURN_TIME = 10;

    private int DIFFICULTY_CODE;
    private int LANGUAGE_CODE;
    public int NUM_TURNS;
    public int NUM_PLAYERS;

    private final InputStream img = RambleBot.class.getResourceAsStream("images/wordbomb.png");

    private final String defaultDescription = (
            "**Starting Lives:** " + STARTING_LIVES + "\n" +
            "**Turn Time**: " + TURN_TIME + "\n" +
            "**Language:** " + languageName() + "\n" +
            "**Players:**"
    );
    public WordBombPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    public String languageName() {
        return (LANGUAGE_CODE == 0) ? "English" : "Spanish";
    }
    public String playerList() {
        StringBuilder s = new StringBuilder();
        for (WordBombPlayer player : players) {
            s.append("\n").append(player.user.getAsMention());
        }
        return s.toString();
    }
    public EmbedBuilder getEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("WordBomb");
        eb.setThumbnail("attachment://wordbomb.png");
        eb.setDescription(defaultDescription + playerList());
        eb.setFooter(host.getEffectiveName(), host.getAvatarUrl());
        eb.setColor(RambleBot.killbotEnjoyer);
        return eb;
    }
    public ArrayList<String> decodeJSON(String filePath) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(WordBomb.class.getClassLoader().getResourceAsStream("com/github/Ramble21/wordbomb/" + filePath + ".json")))) {
            return gson.fromJson(reader, listType);
        } catch (Exception ignored) {
            System.out.println("Error decoding JSON at " + filePath);
        }
        throw new RuntimeException(filePath + " invalid path");
    }
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {

        if (activeChannelIDs.contains(event.getChannelId())) {
            event.reply("There is already a game going on in this channel!").setEphemeral(true).queue();
            return;
        }

        host = event.getUser();
        players.add(new WordBombPlayer(host, STARTING_LIVES));
        LANGUAGE_CODE = event.getOption("language") == null ? 0 : Objects.requireNonNull(event.getOption("language")).getAsInt();
        DIFFICULTY_CODE = event.getOption("difficulty") == null ? 1 : Objects.requireNonNull(event.getOption("difficulty")).getAsInt();
        boolean PRACTICE_MODE = event.getOption("practice") != null && Objects.requireNonNull(event.getOption("practice")).getAsBoolean();
        channel = event.getChannel();
        guild = event.getGuild();
        dictionary = (LANGUAGE_CODE == 0) ? new HashSet<>(decodeJSON("dictionary_en")) : new HashSet<>(decodeJSON("dictionary_es"));
        prompts = (DIFFICULTY_CODE == 1) ? decodeJSON("easy") : (DIFFICULTY_CODE == 2) ? decodeJSON("medium") : decodeJSON("hard");
        activeChannelIDs.add(channel.getId());

        if (PRACTICE_MODE) {
            event.reply("Practice mode is not supported yet!").queue();
            return;
        }

        WordBombButtonListener listener = new WordBombButtonListener(this);
        event.getJDA().addEventListener(listener);

        EmbedBuilder eb = getEmbed();

        event.deferReply().queue(hook -> {
            assert img != null;

            MessageCreateBuilder create = new MessageCreateBuilder()
                    .setEmbeds(eb.build())
                    .addFiles(FileUpload.fromData(img, "wordbomb.png"))
                    .addComponents(ActionRow.of(
                            Button.of(ButtonStyle.SUCCESS, "start", "Start Game"),
                            Button.of(ButtonStyle.PRIMARY, "join", "Join Game"),
                            Button.of(ButtonStyle.DANGER, "leave", "Leave Game"),
                            Button.of(ButtonStyle.SECONDARY, "help", "Help")
                    ));

            hook.sendMessage(create.build()).queue(message -> {
                Timer timer = new Timer();
                TimerTask expiration = new TimerTask() {
                    @Override
                    public void run() {
                        EmbedBuilder expired = new EmbedBuilder()
                                .setTitle("WordBomb")
                                .setColor(Color.RED)
                                .setDescription("Timed out due to inactivity")
                                .setFooter(host.getEffectiveName(), host.getAvatarUrl());

                        MessageEditBuilder edit = new MessageEditBuilder()
                                .setEmbeds(expired.build())
                                .setComponents()
                                .setAttachments();

                        message.editMessage(edit.build()).queue();
                        event.getJDA().removeEventListener(listener);
                        activeChannelIDs.remove(channel.getId());
                    }
                };

                timer.schedule(expiration, 300_000);
            });
        });

    }

    public String getRandomPrompt() {
        return prompts.get((int)(Math.random() * prompts.size()));
    }
    public boolean wordIsValid(String word) {
        return dictionary.contains(word);
    }
    public void endGame() {
        EmbedBuilder eb = new EmbedBuilder();
        User winner = players.get(0).user;
        int numPoints = getNumPoints();

        eb.setColor(Color.green);
        eb.setTitle(winner.getEffectiveName() + ", you are the winner! :tada:");
        eb.setDescription("Congratulations " + winner.getAsMention() + ", you earned " + numPoints + " points.");
        eb.setImage(winner.getEffectiveAvatarUrl());
        channel.sendMessageEmbeds(eb.build()).queue();

        activeChannelIDs.remove(channel.getId());
        WordBombLeaderboard.addServerScore(guild.getId(), winner.getId(), numPoints);
    }
    public int getNumPoints() {
        double playerFactor = 1 + (NUM_PLAYERS - 2) * 0.2; // +20% per player above 2
        double turnFactor = 1 + (NUM_TURNS - (NUM_PLAYERS * 3)) * 0.02; // +2% per turn above the minimum turns in a game
        double difficultyFactor = 1 + (DIFFICULTY_CODE - 1) * 0.25; // +25% per additional difficulty level

        double mean = 100 * playerFactor * turnFactor * difficultyFactor;
        double stdDeviation = mean * 0.15;

        double gaussian = new Random().nextGaussian();
        double value = mean + stdDeviation * gaussian;

        double maxCap = 200 * playerFactor * turnFactor;
        value = Math.round(Math.max(0, Math.min(maxCap, value)));

        return (int) value;
    }
    public void passTurn() {
        if (players.size() == 1) {
            endGame();
            return;
        }
        if (++currentPlayerIndex == players.size()) {
            currentPlayerIndex = 0;
        }
        promptTurn();
    }
    public void promptTurn() {
        EmbedBuilder eb = new EmbedBuilder();
        WordBombPlayer player = players.get(currentPlayerIndex);
        String prompt = getRandomPrompt();
        eb.setTitle(prompt.toUpperCase());
        eb.setDescription("It's " + player.user.getAsMention() + "'s turn!\n\n" + player);
        eb.setThumbnail(player.user.getEffectiveAvatarUrl());
        channel.sendMessageEmbeds(eb.build()).queue();
        NUM_TURNS++;

        Timer timer = new Timer();
        WordBombMessageListener listener = new WordBombMessageListener(this, prompt, timer);
        TimerTask removeLife = new TimerTask() {
            @Override
            public void run() {
                EmbedBuilder embed = new EmbedBuilder();
                int remainingLives = player.removeLife();
                if (remainingLives > 0) {
                    embed.setColor(Color.orange);
                    if (remainingLives == 1) {
                        embed.setDescription(player.user.getAsMention() + " **failed!** 1 life left");
                    }
                    else {
                        embed.setDescription(player.user.getAsMention() + " **failed!** " + remainingLives + " lives left");
                    }
                    channel.sendMessageEmbeds(embed.build()).queue();
                }
                else {
                    embed.setColor(Color.red);
                    embed.setDescription(player.user.getAsMention() + " **is out!**");
                    players.remove(currentPlayerIndex--);
                    channel.sendMessageEmbeds(embed.build()).queue();
                }
                channel.getJDA().removeEventListener(listener);
                passTurn();
            }
        };
        channel.getJDA().addEventListener(listener);
        timer.schedule(removeLife, TURN_TIME * 1000);
    }
}