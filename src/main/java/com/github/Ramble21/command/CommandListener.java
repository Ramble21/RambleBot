package com.github.Ramble21.command;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Ramble21;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        commandData.add(Commands.slash("grab-ip", "Grabs the totally real ip of the member that you choose")
                .addOption(OptionType.USER, "member", "Member to grab IP of", false));

        commandData.add(Commands.slash("rizz-rater", "Rate's a server member's rizz on a scale of 1 to 10")
                .addOption(OptionType.USER, "member", "Member to get rizz level of", false));

        commandData.add(Commands.slash("guess-flags", "Test your world flag knowledge by guessing a random flag!")
            .addOptions(
                    (new OptionData(OptionType.STRING, "country", "Choose a specific country to guess flags from (default: world)")
                            .addChoice("United States", "us")
                            .addChoice("Spain", "es")
                            .setRequired(false)
                    )
            )
        );
        commandData.add(Commands.slash("1984-list", "[ADMIN] Make the bot 1984 certain words/phrases")
                .addSubcommands(
                        new SubcommandData("add", "[ADMIN] Add a word/phrase to the word censor list").addOptions(
                                (new OptionData(OptionType.STRING, "phrase", "Word or phrase to remove").setRequired(true)),
                                (new OptionData(OptionType.BOOLEAN, "word-only", "For single words, only remove the word *by itself* (i.e don't remove the \"ass\" in \"assessment\")").setRequired(true))
                        ),
                        new SubcommandData("remove", "[ADMIN] Remove a word/phrase from the word censor list").addOptions(
                                (new OptionData(OptionType.STRING, "phrase", "Word or phrase to remove").setRequired(true))
                        ),
                        new SubcommandData("list", "[ADMIN] List all currently censored words/phrases")
                )
        );
        commandData.add(Commands.slash("wordbomb", "Play WordBomb on Discord!")
                .addSubcommands(
                        new SubcommandData("play", "Play WordBomb on Discord!").addOptions(
                                (new OptionData(OptionType.INTEGER, "difficulty", "Difficulty of the letter sequences given")
                                        .addChoice("Easy", 1)
                                        .addChoice("Medium", 2)
                                        .addChoice("Hard", 3)
                                        .setRequired(true)
                                ),
                                (new OptionData(OptionType.INTEGER, "language", "Language to play WordBomb in (default: English)")
                                        .addChoice("English", 0)
                                        .addChoice("Español", 1)
                                        .setRequired(false)
                                ),
                                (new OptionData(OptionType.BOOLEAN, "practice", "Select \"True\" to play in practice mode (currently unsupported)")
                                        .setRequired(false)
                                )
                        ),
                        new SubcommandData("leaderboard", "See this server's WordBomb leaderboard!")
                )
        );

        commandData.add(Commands.slash("update-logs", "[BETA] Updates the message logs for this server. May take a long time."));

        commandData.add(
                Commands.slash("typeracer", "Play TypeRacer commands")
                        .addSubcommands(
                                new SubcommandData("play", "Play TypeRacer against another server member!"),
                                new SubcommandData("leaderboard", "See the leaderboard of the highest WPM scores achieved in this server!")
                        )
        );

        commandData.add(
                Commands.slash("gd", "Geometry Dash-related commands")
                        .addSubcommands(
                                new SubcommandData("submitrecord", "Submit Geometry Dash completion record")
                                        .addOptions(
                                                (new OptionData(OptionType.INTEGER, "id", "Level ID to submit record of", true)),
                                                (new OptionData(OptionType.INTEGER, "attempts", "Attempts it took you to complete")
                                                        .setRequired(true))
                                        ),
                                                new SubcommandData("editrecord", "Edit a previously submitted Geometry Dash completion record")
                                        .addOptions(
                                                (new OptionData(OptionType.STRING, "name", "Name of the level to edit record of", true)),
                                                (new OptionData(OptionType.STRING, "creator", "Creator of the level to edit record of", true)),
                                                (new OptionData(OptionType.STRING, "type", "Type of level")
                                                        .addChoice("Classic", "classic")
                                                        .addChoice("Platformer", "platformer")
                                                        .setRequired(true)),
                                                (new OptionData(OptionType.INTEGER, "attempts", "Attempts it took you to complete", false)),
                                                (new OptionData(OptionType.STRING, "difficulty", "Difficulty of the level")
                                                        .addChoice("Underrated", "underrated")
                                                        .addChoice("Overrated", "overrated")
                                                        .setRequired(false)),
                                                (new OptionData(OptionType.USER, "member", "[Moderator only] Member whose completion is being edited", false))
                                        ),
                                                new SubcommandData("deleterecord", "Delete a previously submitted Geometry Dash completion record")
                                        .addOptions(
                                                (new OptionData(OptionType.STRING, "name", "Name of the level to delete", true)),
                                                (new OptionData(OptionType.STRING, "creator", "Creator of the level to delete", true)),
                                                (new OptionData(OptionType.STRING, "type", "Type of level")
                                                        .addChoice("Classic", "classic")
                                                        .addChoice("Platformer", "platformer")
                                                        .setRequired(true)),
                                                (new OptionData(OptionType.USER, "member", "[Moderator only] Member whose completion is being deleted", false))
                                        ),

                                                new SubcommandData("profile", "View hardest levels beaten by a server member")
                                        .addOptions(
                                                (new OptionData(OptionType.USER, "member", "Member to get profile of", false)),
                                                (new OptionData(OptionType.BOOLEAN, "platformer", "Show platformer completions rather than classic ones", false))
                                        ),
                                                new SubcommandData("stats", "View stats about a server member")
                                        .addOptions(
                                                (new OptionData(OptionType.USER, "member", "Member to get stats of", false))
                                        ),
                                                new SubcommandData("leaderboard", "View hardest levels beaten in the entire server")
                                        .addOptions(
                                                (new OptionData(OptionType.BOOLEAN, "platformer", "Show platformer completions rather than classic ones", false))
                                        ),
                                                new SubcommandData("review", "[Moderator only] Review and accept/deny Extreme Demon completions"),
                                                new SubcommandData("refresh", "Refresh in-game and GDDL ratings for all submitted levels"),
                                                new SubcommandData("level", "View statistics about a specific level in this server")
                                        .addOptions(
                                                (new OptionData(OptionType.STRING, "name", "Name of the level in question", true)),
                                                (new OptionData(OptionType.STRING, "creator", "Creator of the level in question", true))
                                        )
                        )
        );

        commandData.add(Commands.slash("vocab", "Study Spanish or French vocabulary")
                .addOptions(
                        (new OptionData(OptionType.STRING, "language", "Choose a language to study vocab from")
                                .addChoice("Spanish", "Spanish")
                                .addChoice("French", "French")
                                .setRequired(true)),
                        (new OptionData(OptionType.BOOLEAN, "onlyreview", "Guarantee that the vocab word you pick is a review word")
                                .setRequired(false))
                )
        );
        commandData.add(Commands.slash("counter-stats", "Show data about the penis counter in this server (if it is enabled)"));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        System.out.println("Command executed by user " + event.getUser().getGlobalName() + ": /" + event.getFullCommandName());
        if (RambleBot.maintenanceMode() && !Ramble21.isBotOwner(event.getUser())) {
            event.reply("Cannot run command, bot is currently in maintenance. Sorry!").queue();
        }
        else {
            CommandManager commandManager = new CommandManager();
            try {
                commandManager.executeCommand(event.getName(), event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
