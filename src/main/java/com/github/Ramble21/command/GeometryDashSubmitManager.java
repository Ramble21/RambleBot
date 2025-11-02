package com.github.Ramble21.command;

import com.github.Ramble21.commands.geometrydash.GeometryDashRecordSubmit;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;

public class GeometryDashSubmitManager implements Command {
    private final HashMap<String, Command> subcommands = new HashMap<>();

    public GeometryDashSubmitManager() {
        subcommands.put("id", new GeometryDashRecordSubmit(false));
        subcommands.put("search", new GeometryDashRecordSubmit(true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String subName = event.getSubcommandName();
        Command sub = subcommands.get(subName);
        if (sub != null) sub.execute(event);
        else event.reply("Unknown subcommand in /submitrecord").queue();
    }
}
