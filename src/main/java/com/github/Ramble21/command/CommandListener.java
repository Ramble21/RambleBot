package com.github.Ramble21.command;

import com.github.Ramble21.commands.TypeRacer;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("grab-ip", "Grabs the totally real ip of the member that you choose")
                .addOption(OptionType.USER, "member", "Member to grab IP of", true));

        commandData.add(Commands.slash("rizz-rater", "Rate's a server member's rizz on a scale of 1 to 10")
                .addOption(OptionType.USER, "member", "Member to get rizz level of", true));

        commandData.add(Commands.slash("state-flags", "Test your US state flag knowledge by guessing a random flag"));

        commandData.add(Commands.slash("typeracer", "Play TypeRacer against another server member!"));

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
