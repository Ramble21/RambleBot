package com.github.Ramble21.commands.messageguessr;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.command.Command;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.io.IOException;

public class MGConfigMisc implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        Integer timeCutoff = event.getOption("time-cutoff", OptionMapping::getAsInt);
        Boolean hideOldMembers = event.getOption("hide-old-members", OptionMapping::getAsBoolean);
        Integer numWrongAnswers = event.getOption("num-wrong-answers", OptionMapping::getAsInt);
        Boolean useNicknames = event.getOption("use-nicknames", OptionMapping::getAsBoolean);

        MessageGuessrOptions options = MessageGuessr.getMiscOptions();
        if (timeCutoff != null) {
            options.setTimeCutoff(timeCutoff);
        }
        if (hideOldMembers != null) {
            options.setHideOldMembers(hideOldMembers);
        }
        if (numWrongAnswers != null && numWrongAnswers >= 1 && numWrongAnswers <= 4) {
            options.setNumWrongAnswers(numWrongAnswers);
        }
        if (useNicknames != null) {
            options.setUseNicknames(useNicknames);
        }
        MessageGuessr.setMiscOptions(options);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String desc = "Current settings:\n" + gson.toJson(options);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Settings successfully changed!");
        eb.setColor(RambleBot.killbotEnjoyer);
        eb.setDescription(desc);
        event.replyEmbeds(eb.build()).queue();
    }
}
