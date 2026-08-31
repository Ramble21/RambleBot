package com.github.Ramble21.command;

import com.github.Ramble21.commands.*;
import com.github.Ramble21.commands.messageguessr.MGBlacklistChannel;
import com.github.Ramble21.commands.messageguessr.MGConfigAlt;
import com.github.Ramble21.commands.messageguessr.MGConfigMisc;
import com.github.Ramble21.commands.messageguessr.MGPlay;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageGuessrManager implements Command {
    private final Map<String, Command> subcommands = new HashMap<>();

    public MessageGuessrManager() {
        subcommands.put("play", new MGPlay());
        subcommands.put("configure-misc", new MGConfigMisc());
        subcommands.put("configure-alt", new MGConfigAlt());
        subcommands.put("blacklist-channel", new MGBlacklistChannel());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String subcommandName = event.getSubcommandName();
        Command subcommand = subcommands.get(subcommandName);
        String mgServer = Dotenv.configure().load().get("MESSAGEDB_SERVER_ID");

        if (!Objects.requireNonNull(event.getGuild()).getId().equals(mgServer)) {
            event.reply("This command is not supported in this server. Sorry!").queue();
        }
        else if (subcommand != null) {
            subcommand.execute(event);
        }
        else {
            event.reply("Unknown subcommand").queue();
        }
    }
}


