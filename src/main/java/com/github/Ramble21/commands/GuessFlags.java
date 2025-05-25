package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Country;
import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.SpainCommunity;
import com.github.Ramble21.classes.State;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class GuessFlags implements Command {

    private InteractionHook storedHook;
    private static int streak = 1;
    private static User lastUser;
    private boolean gameIsInProgress;
    private MessageChannel currentChannel = null;
    private static GuessFlags thisInstance;

    @Override
    public void execute(SlashCommandInteractionEvent event){
        if (thisInstance != null){
            if (thisInstance.gameIsInProgress && event.getChannel() == thisInstance.currentChannel){
                event.reply("There is already a game going on in this channel!").setEphemeral(true).queue();
                return;
            }
        }

        String privateFileName;
        List<String> flagGuesses;
        String countrySymbol;

        if (event.getOption("country") == null){
            Map.Entry<String, List<String>> country = Country.getRandomCountry();
            privateFileName = country.getKey().toUpperCase();
            flagGuesses = country.getValue();
            countrySymbol = "wo";
        }
        else if (Objects.requireNonNull(event.getOption("country")).getAsString().equals("es")) {
            SpainCommunity comm = new SpainCommunity();
            privateFileName = comm.getNameSnakeCase();
            flagGuesses = List.of(comm.getName(), comm.getSpanishName());
            countrySymbol = "es";
        }
        else {
            State state = new State();
            privateFileName = state.getNameSnakeCase();
            flagGuesses = List.of(state.getName());
            countrySymbol = "us";
        }

        final InputStream fileStream;
        EmbedBuilder embed = getEmbed(countrySymbol);

        String path = "com/github/Ramble21/images/flags/" + countrySymbol + "/" + privateFileName + ".png";

        fileStream = RambleBot.class.getClassLoader().getResourceAsStream(path);
        embed.setImage("attachment://mystery.png");

        System.out.println("/guess-flags answers: " + flagGuesses);

        if (fileStream == null) {
            System.out.println("Resource not found: " + path);
            try {
                URL flagsDir = RambleBot.class.getClassLoader().getResource("com/github/Ramble21/images/flags/");
                System.out.println("Flags directory URL: " + flagsDir);
            } catch (Exception e) {
                System.out.println("Could not find flags directory");
            }
            event.reply("The bot is having a stupid bug again").setEphemeral(true).queue();
            return;
        }

        gameIsInProgress = true;
        currentChannel = event.getChannel();
        thisInstance = this;
        event.replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(fileStream, "mystery.png"))
                .queue(hook -> storedHook = hook);

        long startTime = System.currentTimeMillis();



        event.getJDA().addEventListener(new ListenerAdapter() {

            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> timeoutTask = null;

            @Override
            public void onMessageReceived(@NotNull MessageReceivedEvent event) {
                String guess = Diacritics.removeDiacritics(event.getMessage().getContentRaw().toLowerCase());

                timeoutTask = scheduler.schedule(() -> {
                    if (gameIsInProgress) {

                        EmbedBuilder timeoutEmbed = getEmbed(countrySymbol);
                        timeoutEmbed.setDescription("Nobody answered correctly during the time limit, losers");
                        streak = 0;
                        lastUser = null;
                        timeoutEmbed.setColor(Color.red);
                        timeoutEmbed.setImage("attachment://mystery.png");

                        storedHook.editOriginalEmbeds(timeoutEmbed.build()).queue();
                        gameIsInProgress = false;
                        currentChannel = null;

                        event.getJDA().removeEventListener(this);
                    }
                }, 15, TimeUnit.SECONDS);

                if (containsIgnoreCase(flagGuesses, guess)) {

                    double endTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
                    String time = String.format("%.1f", endTimeSeconds);

                    EmbedBuilder embed2 = getEmbed(countrySymbol);
                    embed2.setDescription("**Answered correctly by <@" + event.getAuthor().getId() + "> in " + time + " seconds!**");
                    embed2.setColor(Color.green);
                    embed2.setImage("attachment://mystery.png");

                    storedHook.editOriginalEmbeds(embed2.build()).queue();
                    event.getJDA().removeEventListener(this);

                    EmbedBuilder embed3 = new EmbedBuilder();
                    if (lastUser == event.getAuthor()) {
                        streak++;
                        embed3.setTitle("Congrats! You guessed correctly! x" + streak);
                    } else {
                        streak = 1;
                        embed3.setTitle("Congrats! You guessed correctly!");
                        lastUser = event.getAuthor();
                    }
                    embed3.setDescription("The flag was **" + flagGuesses.get(0) + "**");
                    embed3.setColor(Color.GREEN);
                    gameIsInProgress = false;
                    currentChannel = null;
                    event.getMessage().replyEmbeds(embed3.build()).queue();

                    if (timeoutTask != null && !timeoutTask.isDone()) {
                        timeoutTask.cancel(false);
                    }
                }
            }
        });
    }
    private boolean containsIgnoreCase(List<String> list, String target) {
        for (String s : list) {
            if (s.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }
    private EmbedBuilder getEmbed(String countrySymbol) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(RambleBot.killbotEnjoyer);
        embed.setTitle(
                countrySymbol.equals("us") ? "What US state flag is this?" :
                        countrySymbol.equals("es") ? "What Spanish autonomous community flag is this?" : "What country's flag is this?"
        );
        return embed;
    }
}
