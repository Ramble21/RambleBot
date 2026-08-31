package com.github.Ramble21.commands.messageguessr;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;

public class MGButtonListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {
        String[] parts = buttonEvent.getComponentId().split(":");
        if (parts.length != 4 || !parts[0].equals("mg")) {
            return;
        }
        long inviterId = Long.parseLong(parts[1]);
        long correctUserId = Long.parseLong(parts[2]);
        long guessedUserId = Long.parseLong(parts[3]);

        if (buttonEvent.getUser().getIdLong() != inviterId) {
            buttonEvent.reply("Only the person who started this game can answer!").setEphemeral(true).queue();
            return;
        }

        boolean isCorrect = correctUserId == guessedUserId;

        MessageEmbed originalEmbed = buttonEvent.getMessage().getEmbeds().get(0);
        MessageEmbed updatedEmbed = new EmbedBuilder(originalEmbed)
                .setColor(isCorrect ? Color.GREEN : Color.RED)
                .build();

        MessageComponentTree updatedTree = buttonEvent.getMessage().getComponentTree().replace(component -> {
            if (!(component instanceof Button button)) {
                return component;
            }

            if (button.getStyle() == ButtonStyle.LINK) {
                return button.asEnabled();
            }

            String customId = button.getCustomId();
            if (customId == null || !customId.startsWith("mg:")) {
                return component;
            }
            long thisButtonUserId = Long.parseLong(customId.split(":")[3]);
            if (thisButtonUserId == correctUserId) {
                return button.withStyle(ButtonStyle.SUCCESS).asDisabled();
            }
            if (thisButtonUserId == guessedUserId) {
                return button.withStyle(ButtonStyle.DANGER).asDisabled();
            }
            return button.asDisabled();
        });

        buttonEvent.editComponents(updatedTree)
                .setEmbeds(updatedEmbed)
                .queue();
    }
}