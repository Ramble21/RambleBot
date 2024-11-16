package com.github.Ramble21.commands;

import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class UpdateLogs implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        event.reply("This command hasn't been implemented yet, don't try to use it!").queue();
    }
}
