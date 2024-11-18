package com.github.Ramble21.command;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("grab-ip", "Grabs the totally real ip of the member that you choose")
                .addOption(OptionType.USER, "member", "Member to grab IP of", false));

        commandData.add(Commands.slash("rizz-rater", "Rate's a server member's rizz on a scale of 1 to 10")
                .addOption(OptionType.USER, "member", "Member to get rizz level of", false));

        commandData.add(Commands.slash("state-flags", "Test your US state flag knowledge by guessing a random flag"));

        commandData.add(Commands.slash("update-logs", "[BETA] Updates the message logs for this server. May take a long time."));

        commandData.add(
                Commands.slash("typeracer", "Play TypeRacer commands")
                        .addSubcommands(
                                new SubcommandData("play", "Play TypeRacer against another server member!"),
                                new SubcommandData("leaderboard", "See the leaderboard of the highest WPM scores achieved in this server!")
                        )
        );

        commandData.add(
                Commands.slash("gd", "Geometry Dash-related commands")
                        .addSubcommands(
                                new SubcommandData("submitrecord", "Submit Geometry Dash completion record")
                                        .addOptions(
                                                (new OptionData(OptionType.INTEGER, "id", "Level ID to submit record of", true)),
                                                (new OptionData(OptionType.INTEGER, "attempts", "Attempts it took you to complete (including practice)")
                                                        .setRequired(true))
                                        ),
                                                new SubcommandData("profile", "View RambleBot GD profile")
                                        .addOptions(
                                                (new OptionData (OptionType.USER, "member", "Member to get profile of", false)),
                                                (new OptionData(OptionType.BOOLEAN, "platformer", "Show platformer completions rather than classic ones", false))
                                        ),
                                new SubcommandData("review", "[Moderator only] Review and accept/deny Extreme Demon completions")
                        )
        );

        commandData.add(Commands.slash("vocab", "Study Spanish or French vocabulary")
                .addOptions(
                        (new OptionData(OptionType.STRING, "language", "Choose a language to study vocab from")
                                .addChoice("Spanish", "Spanish")
                                .addChoice("French", "French")
                                .setRequired(true)),
                        (new OptionData(OptionType.BOOLEAN, "onlyreview", "Guarantee that the vocab word you pick is a review word")
                                .setRequired(false))
                )
        );

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName(); // gets name of command minus the slash
        System.out.println("Received command: " + commandName); // for debugging
        CommandManager commandManager = new CommandManager();
        try {
            commandManager.executeCommand(commandName, event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
