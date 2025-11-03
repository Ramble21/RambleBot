package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.io.IOException;
import java.util.Objects;

public class GeometryDashRefresh implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        boolean trustedUser = GDDatabase.getMemberStatus(Objects.requireNonNull(event.getMember())).equals("moderator");
        if (!trustedUser) {
            event.reply("You do not have permission to run this command!").queue();
            return;
        }
        boolean onlyNulls = false;
        if (event.getOption("onlynulls") != null) {
            onlyNulls = Objects.requireNonNull(event.getOption("onlynulls")).getAsBoolean();
        }
        long curr = System.currentTimeMillis();
        event.reply("Refreshing all levels. This may take a long time.").queue();
        if (onlyNulls) {
            GDDatabase.updateNullLevels();
        }
        else {
            GDDatabase.updateAllLevels();
        }
        System.out.println("All levels refreshed in " + (System.currentTimeMillis()-curr) + " ms");
        event.getChannel().sendMessage("All levels successfully refreshed!").queue();
    }
}