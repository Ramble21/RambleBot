package com.github.Ramble21.command;

import com.github.Ramble21.commands.geometrydash.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeometryDashManager implements Command {
    private final Map<String, Command> subcommands = new HashMap<>();

    public GeometryDashManager() {
        subcommands.put("submitrecord", new GeometryDashRecord());
        subcommands.put("editrecord", new GeometryDashRecordEdit());
        subcommands.put("deleterecord", new GeometryDashRecordDelete());
        subcommands.put("profile", new GeometryDashProfile());
        subcommands.put("stats", new GeometryDashStats());
        subcommands.put("review", new GeometryDashReview());
        subcommands.put("leaderboard", new GeometryDashLeaderboard());
        subcommands.put("level", new GeometryDashLevelCommand());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        String subcommandName = event.getSubcommandName();

        Command subcommand = subcommands.get(subcommandName);
        if (subcommand != null) {
            subcommand.execute(event);
        } else {
            event.reply("Unknown subcommand").queue();
        }
    }
}
