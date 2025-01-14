package com.github.Ramble21.listeners;
import com.github.Ramble21.classes.Refresh;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("r!test") && event.getAuthor().getId().equals("739978476651544607")){
            long curr = System.currentTimeMillis();
            Refresh.refreshAllLevels();
            System.out.println("All levels refreshed in " + (System.currentTimeMillis()-curr) + " ms");
        }
    }
}
