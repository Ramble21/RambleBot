package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.WordBombPlayer;
import com.github.Ramble21.commands.WordBomb;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WordBombButtonListener extends ListenerAdapter {
    private final WordBomb game;
    public final String helpDescription = (
            """
            WordBomb consists of a series of rounds in which each player, one by one, is prompted with a two or three letter phrase. The players must name a valid word or phrase/name which contains the prompted phrase.
            
            If you fail to name a valid word, you will lose a life. After you lose your last life, you are eliminated (you lose).
            
            You may notice a bar containing all the letters of the alphabet underneath the prompted phrase. Each time you successfully match a word, each letter present in that word is erased from the alphabet bar. If you manage to use all 26 letters, the alphabet bar resets and you receive an extra life.
            
            *Reactions*
            
            As you guess phrases, RambleBot will add reactions to your guesses.
            
            - :x: indicates that your phrase is invalid.
            - :repeat_one: indicates that somebody else already used that phrase.
            - :white_check_mark: indicates that your phrase was accepted.
            """
    );

    public WordBombButtonListener(WordBomb game) {
        this.game = game;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent buttonEvent) {
        User user = buttonEvent.getUser();
        switch (Objects.requireNonNull(buttonEvent.getComponent().getId())) {
            case "start":
                if (!user.getId().equals(game.host.getId())) {
                    buttonEvent.reply("Only the host can start the game!").setEphemeral(true).queue();
                }
                else if (game.players.size() <= 1) {
                    buttonEvent.reply("You need at least 2 players to start!").setEphemeral(true).queue();
                }
                else {
                    buttonEvent.reply("Starting WordBomb game...").queue();
                    try {
                        Thread.sleep(3500);
                    } catch (InterruptedException ignored) {}
                    game.currentPlayerIndex = (int)(Math.random()*game.players.size());
                    buttonEvent.getJDA().removeEventListener(this);
                    game.promptTurn();
                }
                break;

            case "join":
                if (game.players.contains(new WordBombPlayer(user, 69))) {
                    buttonEvent.reply("You already joined this game!").setEphemeral(true).queue();
                }
                else {
                    game.players.add(new WordBombPlayer(user, game.STARTING_LIVES));
                    buttonEvent.editMessageEmbeds(game.getEmbed().build()).queue();
                }
                break;

            case "leave":
                if (!game.players.contains(new WordBombPlayer(user, 69))) {
                    buttonEvent.reply("You can't leave a game you aren't in!").setEphemeral(true).queue();
                }
                else if (game.players.size() == 1) {
                    buttonEvent.deferEdit().queue(hook -> {
                        EmbedBuilder eb = game.getEmbed();
                        eb.setDescription("Game cancelled by host");
                        hook.sendMessageEmbeds(eb.build())
                                .queue();
                    });
                }
                else {
                    if (!game.players.get(0).user.equals(game.host)) {
                        game.host = game.players.get(1).user;
                    }
                    game.players.remove(new WordBombPlayer(user, game.STARTING_LIVES));
                    buttonEvent.editMessageEmbeds(game.getEmbed().build()).queue();
                }
                break;

            case "help":
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Word Bomb for RambleBot was adapted from https://jklm.fun/ \"BombParty\"");
                eb.setDescription(helpDescription);
                eb.setColor(RambleBot.killbotEnjoyer);
                buttonEvent.replyEmbeds(eb.build()).setEphemeral(true).queue();
                break;

            case "end_practice":
                System.out.println("Temp end practice");
                break;

            default:
                throw new RuntimeException();
        }
    }
}
