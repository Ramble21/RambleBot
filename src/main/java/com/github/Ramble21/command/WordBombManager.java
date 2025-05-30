package com.github.Ramble21.command;

import com.github.Ramble21.commands.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordBombManager implements Command {
    private final Map<String, Command> subcommands = new HashMap<>();

    public WordBombManager() {
        subcommands.put("play", new WordBomb());
        subcommands.put("leaderboard", new WordBombLeaderboard());
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


