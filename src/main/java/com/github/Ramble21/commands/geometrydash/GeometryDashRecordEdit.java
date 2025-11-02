package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.geometrydash.GDRecord;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

public class GeometryDashRecordEdit implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){

        User submitter = event.getUser();
        String name = Objects.requireNonNull(event.getOption("name")).getAsString();
        String creator = Objects.requireNonNull(event.getOption("creator")).getAsString();

        boolean ownEdit = true;

        if (!(event.getOption("member") == null || Objects.requireNonNull(event.getOption("member")).getAsUser() == event.getUser())){
            if (!Ramble21.isBotOwner(submitter)){
                event.reply("You do not have permission to run this command!").setEphemeral(true).queue();
                return;
            }
            else {
                submitter = Objects.requireNonNull(event.getOption("member")).getAsUser();
                ownEdit = false;
            }
        }

        GDLevel level = GDDatabase.getLevelFromNameAuthor(name, creator);
        if (level == null) {
            event.reply("This completion does not exist! (Names and creators are case sensitive!)").setEphemeral(true).queue();
            return;
        }

        GDRecord record = GDDatabase.getRecord(level.getId(), submitter.getIdLong());
        if (record == null) {
            event.reply("This completion does not exist! (Names and creators are case sensitive!)").setEphemeral(true).queue();
            return;
        }

        int bias = record.biasLevel();
        int attempts = record.attempts();

        if (event.getOption("difficulty") != null){
            bias += Integer.parseInt(Objects.requireNonNull(event.getOption("difficulty")).getAsString());
        }
        if (event.getOption("attempts") != null){
            attempts = Objects.requireNonNull(event.getOption("attempts")).getAsInt();
        }
        if (attempts < 10){
            event.reply("Nice try, but I know you spent more than " + attempts + " attempts beating that.").setEphemeral(true).queue();
            return;
        }

        GDDatabase.editRecord(submitter.getIdLong(), level.getId(), attempts, bias);
        if (ownEdit) {
            event.reply(RambleBot.your(true) + " completion of " + level.getName() + " has been successfully edited!").setEphemeral(true).queue();
        }
        else {
            event.reply(submitter.getEffectiveName() + "'s " + level.getName() + " completion has been successfully edited!").queue();
        }
    }
}
