package com.github.Ramble21.commands.messageguessr;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

public class MGBlacklistChannel implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        GuildChannelUnion channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel();
        HashSet<Long> blacklistedChannels = MessageGuessr.getBlacklistedChannels();
        boolean channelAlreadyThere = true;
        if (!blacklistedChannels.contains(channel.getIdLong())) {
            MessageGuessr.addBlacklistedChannel(blacklistedChannels, channel.getIdLong());
            channelAlreadyThere = false;
        }

        StringBuilder desc = new StringBuilder("Blacklisted channels:\n");
        for (long l : blacklistedChannels) {
            GuildChannel gc = event.getJDA().getGuildChannelById(l);
            if (gc != null) {
                desc.append(gc.getJumpUrl()).append("\n");
            }
        }
        desc.deleteCharAt(desc.length() - 1); // remove the last \n, theres prolly a cleaner way to do this but whatever

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(channelAlreadyThere ? "That channel is already blacklisted!" : "Channel successfully blacklisted!");
        eb.setColor(RambleBot.killbotEnjoyer);
        eb.setDescription(desc.toString());
        event.replyEmbeds(eb.build()).queue();
    }
}
