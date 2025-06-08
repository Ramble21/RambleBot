package com.github.Ramble21.command;

import com.github.Ramble21.commands.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WordCensorManager implements Command {
    private final Map<String, Command> subcommands = new HashMap<>();

    public WordCensorManager() {
        subcommands.put("add", new WordCensorAdd());
        subcommands.put("remove", new WordCensorRemove());
        subcommands.put("list", new WordCensorList());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        if (Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER)) {
            String subcommandName = event.getSubcommandName();

            Command subcommand = subcommands.get(subcommandName);
            if (subcommand != null) {
                subcommand.execute(event);
            } else {
                event.reply("Unknown subcommand!").queue();
            }
        }
        else {
            event.reply("You do not have permission to run this command!").queue();
        }
    }
}


