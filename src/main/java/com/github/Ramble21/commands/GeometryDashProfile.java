package com.github.Ramble21.commands;

import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;



public class GeometryDashProfile implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        event.reply("this command isn't coded yet").setEphemeral(true).queue();
    }
}