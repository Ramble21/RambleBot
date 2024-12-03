package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.GeometryDashLevel;
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

public class GeometryDashRecordEdit implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){

        User submitter = event.getUser();
        String name = Objects.requireNonNull(event.getOption("name")).getAsString();
        String creator = Objects.requireNonNull(event.getOption("creator")).getAsString();

        boolean isPlatformer = Objects.requireNonNull(event.getOption("type")).getAsString().equals("platformer");
        String type = "classic"; if (isPlatformer) type = "platformer";
        boolean ownEdit = true;

        String bias = "no bias";
        int newAttempts = 314159265;

        if (event.getOption("difficulty") != null){
            bias = Objects.requireNonNull(event.getOption("difficulty")).getAsString();
        }
        if (event.getOption("attempts") != null){
            newAttempts = Objects.requireNonNull(event.getOption("attempts")).getAsInt();
        }

        if (!(event.getOption("member") == null || Objects.requireNonNull(event.getOption("member")).getAsUser() == event.getUser())){
            if (!submitter.getId().equalsIgnoreCase("739978476651544607")){
                event.reply("You do not have permission to run this command!").setEphemeral(true).queue();
                return;
            }
            else {
                submitter = Objects.requireNonNull(event.getOption("member")).getAsUser();
                ownEdit = false;
            }
        }
        if (newAttempts < 1){
            event.reply("Nice try, but I know you spent more than " + newAttempts + " attempts beating that.").setEphemeral(true).queue();
            return;
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
        GeometryDashLevel editedLevel = levels.get(index);
        if (newAttempts != 314159265){
            editedLevel.setAttempts(newAttempts);
        }
        if (bias.equals("underrated")){
            editedLevel.setBiasLevel(editedLevel.getBiasLevel() + 1);
        }
        else if (bias.equals("overrated")){
            editedLevel.setBiasLevel(editedLevel.getBiasLevel() - 1);
        }

        levels.remove(index);
        levels.add(editedLevel);

        try (FileWriter writer = new FileWriter("data/json/completions/" + type + "/" + submitter.getId() + ".json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(levels,writer);
            if (ownEdit){
                event.reply("Your completion of " + targetLevel.getName() + " has been successfully edited!").setEphemeral(true).queue();
            }
            else{
                event.reply( submitter.getEffectiveName() + "'s " + targetLevel.getName() + " completion has been successfully edited!").queue();
            }
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
