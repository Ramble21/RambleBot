package com.github.Ramble21.commands;

import com.github.Ramble21.command.Command;
import com.github.Ramble21.command.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TypeRacerManager implements Command {
    private final Map<String, Command> subcommands = new HashMap<>();

    public TypeRacerManager() {
        subcommands.put("play", new TypeRacer());
        subcommands.put("leaderboard", new TypeRacerLeaderboard());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String subcommandName = event.getSubcommandName();

        Command subcommand = subcommands.get(subcommandName);
        if (subcommand != null) {
            subcommand.execute(event);
        } else {
            event.reply("Unknown subcommand. Please use `/typeracer play` or `/typeracer leaderboard`.").queue();
        }
    }
}


