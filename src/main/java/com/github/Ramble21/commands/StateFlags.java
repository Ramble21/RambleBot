package com.github.Ramble21.commands;

import com.github.Ramble21.RambleBot;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.*;

public class StateFlags implements Command {

    private InteractionHook storedHook;
    private static int streak = 1;
    private static User lastUser;
    private boolean gameIsInProgress;
    private MessageChannel currentChannel = null;
    private static StateFlags thisInstance;

    @Override
    public void execute(SlashCommandInteractionEvent event){
        if (thisInstance != null){
            if (thisInstance.gameIsInProgress && event.getChannel() == thisInstance.currentChannel){
                event.reply("There is already a game going on in this channel!").setEphemeral(true).queue();
                return;
            }
        }

        Color red = new Color(255, 0, 0);
        Color green = new Color(0, 255, 0);
        Color blue = new Color(0, 122, 255);

        String countrySymbol;
        EmbedBuilder embed = new EmbedBuilder();

        State state = null;
        SpainCommunity spainCommunity = null;
        if (event.getOption("country") == null){
            countrySymbol = "us";
            state = new State();
        }
        else{
            countrySymbol = Objects.requireNonNull(event.getOption("country")).getAsString();
            if (countrySymbol.equals("es")){
                spainCommunity = new SpainCommunity();
            }
            else{
                state = new State();
            }
        }

        embed.setColor(blue);
        gameIsInProgress = true;
        currentChannel = event.getChannel();
        thisInstance = this;

        String fileName;
        final InputStream fileStream;
        if (countrySymbol.equals("us")){
            embed.setTitle("What US state flag is this?");
            System.out.println("Flag: " + state.getName());
            fileName = state.getNameSnakeCase() + ".png";
            fileStream = RambleBot.class.getResourceAsStream("images/flags/us/" + fileName);
            embed.setImage("attachment://mystery.png");
            assert fileStream != null;
            event.replyEmbeds(embed.build())
                    .addFiles(FileUpload.fromData(fileStream, "mystery.png"))
                    .queue(hook -> storedHook = hook);
        }
        else{
            embed.setTitle("What Spanish autonomous community flag is this?");
            assert spainCommunity != null;
            System.out.println("Flag: " + spainCommunity.getName());
            fileName = spainCommunity.getNameSnakeCase() + ".png";
            fileStream = RambleBot.class.getResourceAsStream("images/flags/es/" + fileName);
            embed.setImage("attachment://mystery.png");
            assert fileStream != null;
            event.replyEmbeds(embed.build())
                    .addFiles(FileUpload.fromData(fileStream, "mystery.png"))
                    .queue(hook -> storedHook = hook);
        }

        LocalDateTime start = LocalDateTime.now();

        State finalState = state;
        SpainCommunity finalSpainCommunity = spainCommunity;
        event.getJDA().addEventListener(new ListenerAdapter() {

            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> timeoutTask = null;

            @Override
            public void onMessageReceived(@NotNull MessageReceivedEvent event) {
                String guess = event.getMessage().getContentRaw().toLowerCase();
                System.out.println("game is in progress: " + gameIsInProgress);

                timeoutTask = scheduler.schedule(() -> {
                    if (gameIsInProgress) {
                        EmbedBuilder timeoutEmbed = new EmbedBuilder();
                        if (countrySymbol.equals("us")) {
                            timeoutEmbed.setTitle("What US state flag is this?");
                        }
                        else{
                            timeoutEmbed.setTitle("What Spanish autonomous community flag is this?");
                        }
                        timeoutEmbed.setDescription("Nobody answered correctly during the time limit, losers");
                        streak = 1;
                        lastUser = null;
                        timeoutEmbed.setColor(red);
                        timeoutEmbed.setImage("attachment://mystery.png");

                        storedHook.editOriginalEmbeds(timeoutEmbed.build()).queue();
                        gameIsInProgress = false;
                        currentChannel = null;

                        event.getJDA().removeEventListener(this);
                    }
                }, 15, TimeUnit.SECONDS);

                if (countrySymbol.equals("us") && guess.equalsIgnoreCase(finalState.getName())) {
                    LocalDateTime end = LocalDateTime.now();
                    Duration duration = Duration.between(start, end);
                    double seconds = (double) (duration.toMillis());
                    String sussyTime = Double.toString(seconds / 1000);
                    String time = sussyTime.substring(0, sussyTime.length() - 2);

                    EmbedBuilder embed2 = new EmbedBuilder();
                    embed2.setTitle("What US state flag is this?");
                    embed2.setDescription("**Answered correctly by <@" + event.getAuthor().getId() + "> in " + time + " seconds!**");
                    embed2.setColor(green);
                    embed2.setImage("attachment://mystery.png");

                    storedHook.editOriginalEmbeds(embed2.build()).queue();
                    event.getJDA().removeEventListener(this);

                    EmbedBuilder embed3 = new EmbedBuilder();
                    if (lastUser == event.getAuthor()) {
                        streak++;
                        System.out.println(streak);
                        embed3.setTitle("Congrats! You guessed correctly! x" + streak);
                    } else {
                        streak = 1;
                        embed3.setTitle("Congrats! You guessed correctly!");
                        lastUser = event.getAuthor();
                    }
                    embed3.setDescription("The flag was **" + finalState.getName() + "**");
                    embed3.setColor(Color.GREEN);
                    gameIsInProgress = false;
                    currentChannel = null;
                    event.getMessage().replyEmbeds(embed3.build()).queue();

                    if (timeoutTask != null && !timeoutTask.isDone()) {
                        timeoutTask.cancel(false);
                    }
                }
                else if (countrySymbol.equals("es") && (guess.equalsIgnoreCase(finalSpainCommunity.getName()) || guess.equalsIgnoreCase(finalSpainCommunity.getSpanishName()))){
                    LocalDateTime end = LocalDateTime.now();
                    Duration duration = Duration.between(start, end);
                    double seconds = (double) (duration.toMillis());
                    String sussyTime = Double.toString(seconds / 1000);
                    String time = sussyTime.substring(0, sussyTime.length() - 2);
                    if (time.charAt(time.length()-1) == '.'){
                        time += "0";
                    }

                    EmbedBuilder embed2 = new EmbedBuilder();
                    embed2.setTitle("What Spanish autonomous community flag is this?");
                    embed2.setDescription("**Answered correctly by <@" + event.getAuthor().getId() + "> in " + time + " seconds!**");
                    embed2.setColor(green);
                    embed2.setImage("attachment://mystery.png");

                    storedHook.editOriginalEmbeds(embed2.build()).queue();
                    event.getJDA().removeEventListener(this);

                    EmbedBuilder embed3 = new EmbedBuilder();
                    if (lastUser == event.getAuthor()) {
                        streak++;
                        System.out.println(streak);
                        embed3.setTitle("Congrats! You guessed correctly! x" + streak);
                    } else {
                        streak = 1;
                        embed3.setTitle("Congrats! You guessed correctly!");
                        lastUser = event.getAuthor();
                    }
                    embed3.setDescription("The flag was **" + finalSpainCommunity.getName() + "**");
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
}
