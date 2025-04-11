package com.github.Ramble21.listeners;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.classes.chess.Board;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("r!test") && Ramble21.isBotOwner(event.getAuthor())){
            Board b = new Board();
            EmbedBuilder e = new EmbedBuilder();
            e.setDescription(b.toString());
            event.getChannel().sendMessageEmbeds(e.build()).queue();
        }
    }
}
