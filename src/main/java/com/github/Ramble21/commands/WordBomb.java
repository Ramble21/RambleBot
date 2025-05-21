package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.WordBombPlayer;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.listeners.WordBombButtonListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class WordBomb implements Command {

    private HashSet<String> dictionary;
    private ArrayList<String> prompts;

    public final ArrayList<WordBombPlayer> players = new ArrayList<>();
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
        long time = System.currentTimeMillis();
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
        host = event.getUser();
        players.add(new WordBombPlayer(host, STARTING_LIVES));
        LANGUAGE_CODE = event.getOption("language") == null ? 0 : Objects.requireNonNull(event.getOption("language")).getAsInt();
        DIFFICULTY_CODE = event.getOption("difficulty") == null ? 1 : Objects.requireNonNull(event.getOption("difficulty")).getAsInt();
        PRACTICE_MODE = event.getOption("practice") != null && Objects.requireNonNull(event.getOption("practice")).getAsBoolean();
        channel = event.getChannel();
        dictionary = (LANGUAGE_CODE == 0) ? new HashSet<>(decodeJSON("dictionary_en")) : new HashSet<>(decodeJSON("dictionary_es"));
        prompts = (DIFFICULTY_CODE == 1) ? decodeJSON("easy") : (DIFFICULTY_CODE == 2) ? decodeJSON("medium") : decodeJSON("hard");

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
                    .queue();
        });
    }

    public void startGame() {
        promptTurn(0);
    }
    public String getRandomPrompt() {
        return prompts.get((int)(Math.random() * prompts.size()));
    }
    public boolean checkIfWordValid(String word) {
        return dictionary.contains(word);
    }
    public void promptTurn(int playerIndex) {
        EmbedBuilder eb = new EmbedBuilder();
        WordBombPlayer player = players.get(playerIndex);
        String prompt = getRandomPrompt();
        eb.setTitle(prompt.toUpperCase());
        eb.setDescription("It's " + player.user.getAsMention() + "'s turn!\n\n" + player);
        eb.setThumbnail(player.user.getEffectiveAvatarUrl());
        channel.sendMessageEmbeds(eb.build()).queue();
    }

}