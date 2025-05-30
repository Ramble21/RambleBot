package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.commands.WordBomb;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Timer;

public class WordBombMessageListener extends ListenerAdapter {
    private final WordBomb game;
    private final String prompt;
    private final Timer timer;

    public WordBombMessageListener(WordBomb game, String prompt, Timer timer) {
        this.game = game;
        this.prompt = prompt;
        this.timer = timer;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(game.getCurrentPlayer().user.getId())) {
            return;
        }
        if (!event.getChannel().getId().equals(game.channel.getId())) {
            return;
        }

        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw().toLowerCase().split("\\s+")[0]);
        if (message.contains(prompt) && game.wordIsValid(message) && !game.usedWords.contains(message)) {
            timer.cancel();
            event.getJDA().removeEventListener(this);
            game.usedWords.add(message);
            event.getMessage().addReaction(Emoji.fromUnicode("✅")).queue();

            boolean extraLife = game.getCurrentPlayer().processTurn(message);
            if (extraLife) {
                game.getCurrentPlayer().addLife();
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setDescription(event.getAuthor().getAsMention() + (extraLife ? "** passed and earned an extra life!**" : " **passed!**"));
            eb.setColor(Color.green);
            event.getMessage().replyEmbeds(eb.build()).mentionRepliedUser(false).queue();

            if (++game.currentPlayerIndex == game.players.size()) {
                game.currentPlayerIndex = 0;
            }
            game.promptTurn();

        }
        else if (message.contains(prompt) && game.wordIsValid(message)) {
            event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDD02")).queue();
        }
        else {
            event.getMessage().addReaction(Emoji.fromUnicode("❌")).queue();
        }
    }
}
