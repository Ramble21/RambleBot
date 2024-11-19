package com.github.Ramble21.command;

import com.github.Ramble21.commands.geometrydash.GeometryDashLeaderboard;
import com.github.Ramble21.commands.geometrydash.GeometryDashProfile;
import com.github.Ramble21.commands.geometrydash.GeometryDashRecord;
import com.github.Ramble21.commands.geometrydash.GeometryDashReview;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeometryDashManager implements Command {
    private final Map<String, Command> subcommands = new HashMap<>();

    public GeometryDashManager() {
        subcommands.put("submitrecord", new GeometryDashRecord());
        subcommands.put("profile", new GeometryDashProfile());
        subcommands.put("review", new GeometryDashReview());
        subcommands.put("leaderboard", new GeometryDashLeaderboard());
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
