package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.WordBombPlayer;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.WordBombButtonListener;
import com.github.Ramble21.listeners.WordBombMessageListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

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


    public final int STARTING_LIVES = 3;
    private final int TURN_TIME = 10;
    private int DIFFICULTY_CODE;
    private int LANGUAGE_CODE;
    private boolean PRACTICE_MODE;
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
        PRACTICE_MODE = event.getOption("practice") != null && Objects.requireNonNull(event.getOption("practice")).getAsBoolean();
        channel = event.getChannel();
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
            hook.sendMessageEmbeds(eb.build())
                    .addFiles(FileUpload.fromData(img,"wordbomb.png"))
                    .addActionRow(
                            Button.success("start", "Start Game"),
                            Button.primary("join", "Join Game"),
                            Button.danger("leave", "Leave Game"),
                            Button.secondary("help", "Help")
                    )
                    .queue(message -> {
                        Timer timer = new Timer();
                        TimerTask expiration = new TimerTask() {
                            @Override
                            public void run() {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle("WordBomb");
                                eb.setColor(Color.red);
                                eb.setDescription("Timed out due to inactivity");
                                eb.setFooter(host.getEffectiveName(), host.getAvatarUrl());
                                message.editMessageEmbeds(eb.build())
                                        .setAttachments()
                                        .setComponents()
                                        .queue();
                                event.getJDA().removeEventListener(listener);
                                activeChannelIDs.remove(channel.getId());
                            }
                        };
                        timer.schedule(expiration, 300_000); // 5 minutes until expiration
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
        eb.setColor(Color.green);
        eb.setTitle(players.get(0).user.getEffectiveName() + ", you are the winner! :tada:");
        eb.setDescription("Congratulations " + players.get(0).user.getAsMention() + ", you earned " + getNumPoints() + " points.");
        eb.setImage(players.get(0).user.getEffectiveAvatarUrl());
        channel.sendMessageEmbeds(eb.build()).queue();
        activeChannelIDs.remove(channel.getId());
    }
    public int getNumPoints() {
        Random random = new Random();
        double mean = 100;
        double stdDeviation = 30;
        double gaussian = random.nextGaussian();
        double value = mean + stdDeviation * gaussian;
        double mult = (double) DIFFICULTY_CODE / 1.5;
        value = Math.max(0, Math.min(200, value));
        return (int) Math.round(value * mult);
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