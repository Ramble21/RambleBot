package com.github.Ramble21.listeners;
import com.github.Ramble21.classes.Ramble21;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Objects;

public class Test extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw().toLowerCase();
        if (message.equals("r!test") && Ramble21.isBotOwner(event.getAuthor())){
            System.out.println("Hello world!");
        }
        if (message.equals("r!make_new_data") && Ramble21.isBotOwner(event.getAuthor())) {
            String[] subDirects = new String[]{"classic", "platformer", "queue"};
            for (String subDirectory : subDirects) {
                File directory = new File("data/json/completions/" + subDirectory);
                for (File file : Objects.requireNonNull(directory.listFiles())) {
                    try {
                        Reader reader = new FileReader("data/json/completions/" + subDirectory + "/" + file.getName());
                        Writer writer = new FileWriter("data/json/gd-records/" + subDirectory + "/" + file.getName());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

        }
    }
}
