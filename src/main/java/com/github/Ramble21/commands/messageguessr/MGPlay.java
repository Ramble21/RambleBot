package com.github.Ramble21.commands.messageguessr;

import com.github.Ramble21.command.Command;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MGPlay implements Command {

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        EmbedBuilder embed = new EmbedBuilder();
        Member commandMember = Objects.requireNonNull(event.getMember());
        long serverId = Objects.requireNonNull(event.getGuild()).getIdLong();

        MessageGuessrOptions options = MessageGuessr.getMiscOptions();
        ArrayList<Long> badUserIds = MessageGuessr.getBadIds(event.getGuild(), options.hidesOldMembers());
        ArrayList<Long> badChannelIds = new ArrayList<>(MessageGuessr.getBlacklistedChannels());
        Message toGuess = MessageGuessrDB.getMessage(serverId, badUserIds, badChannelIds);

        if (toGuess == null) {
            event.reply("Message database is empty!").queue();
            return;
        }

        embed.setColor(Color.YELLOW);
        embed.setTitle("Who said this?");
        embed.setAuthor(commandMember.getEffectiveName(), commandMember.getEffectiveAvatarUrl());
        embed.setDescription(toGuess.content());

        ArrayList<Long> userIds = MessageGuessrDB.getUniqueUserIds(event.getGuild().getIdLong());
        ArrayList<Long> pool = new ArrayList<>(userIds);
        pool.remove(toGuess.userId());
        Collections.shuffle(pool);

        ArrayList<Long> answerList = new ArrayList<>(pool.subList(0, options.getNumWrongAnswers()));
        answerList.add(toGuess.userId());
        Collections.shuffle(answerList);

        ArrayList<Button> buttons = new ArrayList<>();
        for (long answer : answerList) {
            User user = event.getJDA().getUserById(answer);
            String label = getLabel(user, options);
            String buttonId = "mg:" + toGuess.userId() + ":" + answer;
            buttons.add(Button.primary(buttonId, label));
        }

        ArrayList<ActionRow> rows = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i += 5) {
            rows.add(ActionRow.of(buttons.subList(i, Math.min(i + 5, buttons.size()))));
        }

        event.replyEmbeds(embed.build()).addComponents(rows).queue();

    }

    private String getLabel(User user, MessageGuessrOptions options) {
        if (user == null) {
            return "Unknown User";
        }
        else if (options.usesNicknames()) {
            return user.getEffectiveName();
        }
        else {
            return user.getGlobalName();
        }
    }

}
