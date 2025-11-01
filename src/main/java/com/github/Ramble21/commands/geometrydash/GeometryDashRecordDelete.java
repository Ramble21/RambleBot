package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.geometrydash.GeometryDashLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class GeometryDashRecordDelete implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){

        User submitter = event.getUser();
        String name = Objects.requireNonNull(event.getOption("name")).getAsString();
        String creator = Objects.requireNonNull(event.getOption("creator")).getAsString();
        boolean isPlatformer = Objects.requireNonNull(event.getOption("type")).getAsString().equals("platformer");
        String type = "classic"; if (isPlatformer) type = "platformer";
        boolean ownDelete = true;

        if (!(event.getOption("member") == null || Objects.requireNonNull(event.getOption("member")).getAsUser() == event.getUser())){
            if (!Ramble21.isBotOwner(submitter)){
                event.reply("You do not have permission to run this command!").setEphemeral(true).queue();
                return;
            }
            else {
                submitter = Objects.requireNonNull(event.getOption("member")).getAsUser();
                ownDelete = false;
            }
        }

        ArrayList<GeometryDashLevel> levels = GeometryDashLevel.getPersonalJsonList(submitter, isPlatformer);
        GeometryDashLevel targetLevel = null;

        if (levels == null){
            event.reply("This completion does not exist!").setEphemeral(true).queue();
            return;
        }
        int index = -1;
        for (int i = 0; i < levels.size(); i++){
            if (levels.get(i).getName().equalsIgnoreCase(name) && levels.get(i).getAuthor().equalsIgnoreCase(creator)){
                index = i;
                targetLevel = levels.get(i);
            }
        }
        if (index == -1){
            event.reply("This completion does not exist!").setEphemeral(true).queue();
            return;
        }
        levels.remove(index);

        try (FileWriter writer = new FileWriter("data/json/completions/" + type + "/" + submitter.getId() + ".json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(levels,writer);
            if (ownDelete){
                event.reply(RambleBot.your() + " completion of " + targetLevel.getName() + " has been deleted from the records").setEphemeral(true).queue();
            }
            else{
                event.reply( submitter.getEffectiveName() + "'s " + targetLevel.getName() + " completion has been deleted from the records").queue();
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}