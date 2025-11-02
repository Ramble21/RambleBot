package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.util.Objects;

public class GeometryDashRecordDelete implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) {

        User submitter = event.getUser();
        String name = Objects.requireNonNull(event.getOption("name")).getAsString();
        String creator = Objects.requireNonNull(event.getOption("creator")).getAsString();
        GDLevel level = GDDatabase.getLevelFromNameAuthor(name, creator);

        boolean ownDelete = true;
        if (!(event.getOption("member") == null || Objects.requireNonNull(event.getOption("member")).getAsUser() == event.getUser())) {
            if (!Ramble21.isBotOwner(submitter)) {
                event.reply("You do not have permission to run this command!").setEphemeral(true).queue();
                return;
            }
            else {
                submitter = Objects.requireNonNull(event.getOption("member")).getAsUser();
                ownDelete = false;
            }
        }

        if (level == null) {
            event.reply("This completion does not exist!").setEphemeral(true).queue();
            return;
        }
        GDDatabase.deleteRecord(submitter.getIdLong(), level.getId());
        if (ownDelete) {
            event.reply(RambleBot.your(true) + " completion of " + level.getName() + " has been deleted from the records").setEphemeral(true).queue();
        }
        else {
            event.reply(submitter.getEffectiveName() + "'s " + level.getName() + " completion has been deleted from the records").queue();
        }
    }
}