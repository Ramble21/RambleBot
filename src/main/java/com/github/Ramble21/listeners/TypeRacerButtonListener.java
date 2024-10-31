package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.commands.TypeRacer;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class TypeRacerButtonListener extends ListenerAdapter {

    private final TypeRacer typeRacer;
    public static boolean isStarting = false;

    public TypeRacerButtonListener(TypeRacer typeRacer){
        this.typeRacer = typeRacer;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {

        String buttonId = buttonEvent.getComponent().getId();
        String messageId = buttonEvent.getMessageId();
        User originalUser = typeRacer.getUser();
        User buttonUser = buttonEvent.getUser();

        // look glister i know how to use case and switch now are you proud of me
        switch(Objects.requireNonNull(buttonId)){
            case "acceptButton":
                if (isStarting){
                    return;
                }
                if (!messageId.equals(typeRacer.getOriginalMessageId())){
                    return;
                }
                if (buttonUser.equals(originalUser) && !(originalUser.getId().equals("739978476651544607"))){
                    return;
                }
                isStarting = true;
                try {
                    typeRacer.startGame(originalUser, buttonUser);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                isStarting = false;
                break;

            case "cancelButton":
                if (!messageId.equals(typeRacer.getOriginalMessageId())){
                    return;
                }
                if (!buttonUser.equals(originalUser)){
                    System.out.println("Cancel attempt by unauthorized user " + buttonUser.getEffectiveName());
                    System.out.println("Expected user: " + originalUser.getEffectiveName());
                    return;
                }
                typeRacer.cancelGame(buttonUser);
                break;
            default:
                Ramble21.bugOccurred(buttonEvent.getChannel());
        }
    }
}
