package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.geometrydash.Refresh;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.io.IOException;

public class GeometryDashRefresh implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        long curr = System.currentTimeMillis();
        event.reply("Refreshing all levels. This may take a long time.").queue();
        Refresh.refreshAllLevels();
        System.out.println("All levels refreshed in " + (System.currentTimeMillis()-curr) + " ms");
        event.getChannel().sendMessage("All levels successfully refreshed!").queue();
    }
}