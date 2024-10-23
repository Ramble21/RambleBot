package com.github.Ramble21.commands;

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
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
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
        User skibidi = event.getUser();
        String user = skibidi.getId();

        Color red = new Color(255, 0, 0);
        Color green = new Color(0, 255, 0);
        Color blue = new Color(0, 122, 255);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("What US state flag is this?");
        State state = new State();
        embed.setColor(blue);
        System.out.println(state.getName());
        gameIsInProgress = true;
        currentChannel = event.getChannel();
        thisInstance = this;

        String filename = state.getNameSnakeCase() + ".png";
        File file = new File(("src/main/images/flags/" + filename));



        embed.setImage("attachment://mystery.png");
        event.replyEmbeds(embed.build())
                .addFiles(FileUpload.fromData(file, "mystery.png"))
                .queue(hook -> {
                    storedHook = hook;
                });

        LocalDateTime start = LocalDateTime.now();



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
                        timeoutEmbed.setTitle("What US state flag is this?");
                        timeoutEmbed.setDescription("Nobody answered correctly during the time limit, losers");
                        streak = 1;
                        lastUser = null;
                        timeoutEmbed.setColor(red);
                        timeoutEmbed.setImage("attachment://mystery.png");

                        storedHook.editOriginalEmbeds(timeoutEmbed.build()).queue();
                        gameIsInProgress = false;
                        currentChannel = null;

                        event.getJDA().removeEventListener(this);
                        return;
                    }
                }, 15, TimeUnit.SECONDS);

                if (guess.equalsIgnoreCase(state.getName())) {
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
                    embed3.setDescription("The state was **" + state.getName() + "**");
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
