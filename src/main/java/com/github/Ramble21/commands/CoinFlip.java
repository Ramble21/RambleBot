package com.github.Ramble21.commands;

import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CoinFlip implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        boolean heads = Math.random() > 0.5;
        if (heads) {
            event.reply("Your coin landed on: Heads!").queue();
            return;
        }
        event.reply("Your coin landed on: Tails!").queue();
    }
}
