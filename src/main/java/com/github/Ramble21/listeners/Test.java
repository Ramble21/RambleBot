package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.GeometryDashLevel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith("r!apitest")){
            try {
                int id = Integer.parseInt(event.getMessage().getContentRaw().substring(10));
                GeometryDashLevel testLevel = new GeometryDashLevel(id);
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                System.out.println("Invalid ID");
            }
        }
    }
}
