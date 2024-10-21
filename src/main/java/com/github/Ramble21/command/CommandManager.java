package com.github.Ramble21.command;


import com.github.Ramble21.commands.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.*;


public class CommandManager{
    private static final Map<String, Command> commands = new HashMap<>();

    public CommandManager(){
        commands.put("grab-ip", new GrabIp());
        commands.put("rizz-rater", new RizzRater());
        commands.put("add-quote", new AddQuote());
        commands.put("delete-quote", new DeleteQuote());
        commands.put("random-quote", new RandomQuote());
    }

    public Command getCommand(String name){
        return commands.get(name);
    }

    public void executeCommand(String commandName, SlashCommandInteractionEvent event) {
        Command command = getCommand(commandName);
        if (command != null) {
            command.execute(event);
        }
        else {
            System.out.println("command is null theres a bug");
        }
    }
}