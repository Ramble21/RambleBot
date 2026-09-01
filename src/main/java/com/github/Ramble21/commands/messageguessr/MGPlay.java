package com.github.Ramble21.commands.messageguessr;

import com.github.Ramble21.command.Command;
import com.github.Ramble21.command.MessageGuessrManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.io.IOException;
import java.util.*;

public class MGPlay implements Command {

    private static final Map<Long, Map<Long, User>> validUserCache = new HashMap<>();
    private static final Map<Long, Set<Long>> invalidUserCache = new HashMap<>();
    private static final Set<Long> loadedServers = new HashSet<>();

    private void loadPersistedCache(long serverId) {
        if (loadedServers.contains(serverId)) return;
        loadedServers.add(serverId);

        MessageGuessr.ClassificationCache cache = MessageGuessr.loadClassificationCache(serverId);
        invalidUserCache.computeIfAbsent(serverId, k -> new HashSet<>()).addAll(cache.invalidIds());
        // validIds are loaded as known-valid, but we still need live User objects,
        // so they'll be re-fetched (fast, since we skip the isBot() classification logic)
        // the first time each is needed -- saves nothing on the User fetch itself,
        // but permanently skips ever re-checking bot/deleted status for them again.
    }

    private User classify(JDA jda, long serverId, long id) {
        loadPersistedCache(serverId);

        Map<Long, User> validMap = validUserCache.computeIfAbsent(serverId, k -> new HashMap<>());
        Set<Long> invalidSet = invalidUserCache.computeIfAbsent(serverId, k -> new HashSet<>());

        if (validMap.containsKey(id)) return validMap.get(id);
        if (invalidSet.contains(id)) return null;

        try {
            User user = jda.retrieveUserById(id).complete();
            if (user.isBot()) {
                invalidSet.add(id);
                MessageGuessr.saveClassificationCache(serverId,
                        new MessageGuessr.ClassificationCache(new HashSet<>(validMap.keySet()), new HashSet<>(invalidSet)));
                return null;
            }
            validMap.put(id, user);
            MessageGuessr.saveClassificationCache(serverId,
                    new MessageGuessr.ClassificationCache(new HashSet<>(validMap.keySet()), new HashSet<>(invalidSet)));
            return user;
        } catch (ErrorResponseException e) {
            invalidSet.add(id);
            MessageGuessr.saveClassificationCache(serverId,
                    new MessageGuessr.ClassificationCache(new HashSet<>(validMap.keySet()), new HashSet<>(invalidSet)));
            return null;
        }
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) throws IOException {
        if (!Objects.requireNonNull(event.getGuild()).getId().equals(MessageGuessrManager.mgServer)) {
            event.reply("This command is not supported in this server. Sorry!").queue();
            return;
        }

        event.deferReply().queue();

        Member commandMember = Objects.requireNonNull(event.getMember());
        long serverId = Objects.requireNonNull(event.getGuild()).getIdLong();
        MessageGuessrOptions options = MessageGuessr.getMiscOptions();
        ArrayList<Long> badChannelIds = new ArrayList<>(MessageGuessr.getBlacklistedChannels());
        ArrayList<Long> badUserIds = MessageGuessr.getBadIds(event.getGuild(), options.hidesOldMembers());

        Map<Long, Long> mainAccounts = MessageGuessr.getMainAccounts(MessageGuessrDB.getUniqueUserIds(serverId, 1000));

        ArrayList<Long> goodUserIds = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : mainAccounts.entrySet()) {
            long rawUserId = entry.getKey();
            long mainId = entry.getValue();
            if (badUserIds.contains(rawUserId)) continue;
            if (classify(event.getJDA(), serverId, mainId) != null) {
                goodUserIds.add(rawUserId);
            }
        }

        Message toGuess = MessageGuessrDB.getMessage(serverId, goodUserIds, badChannelIds);
        if (toGuess == null) {
            event.getHook().sendMessage("Message database is empty!").queue();
            return;
        }

        long correctUserId = mainAccounts.getOrDefault(toGuess.userId(), toGuess.userId());
        User correctUser = classify(event.getJDA(), serverId, correctUserId);

        Set<Long> candidatePool = new HashSet<>(mainAccounts.values());
        candidatePool.remove(correctUserId);
        ArrayList<Long> shuffledPool = new ArrayList<>(candidatePool);
        Collections.shuffle(shuffledPool);

        ArrayList<Long> finalAnswerIds = new ArrayList<>();
        Map<Long, User> resolvedUsers = new HashMap<>();
        finalAnswerIds.add(correctUserId);
        resolvedUsers.put(correctUserId, correctUser);

        for (long candidate : shuffledPool) {
            if (finalAnswerIds.size() >= options.getNumWrongAnswers() + 1) break;
            User user = classify(event.getJDA(), serverId, candidate);
            if (user == null) {
                continue;
            }
            finalAnswerIds.add(candidate);
            resolvedUsers.put(candidate, user);
        }

        if (finalAnswerIds.size() < options.getNumWrongAnswers() + 1) {
            event.getHook().sendMessage("Not enough valid users to generate answer choices!").queue();
            return;
        }
        Collections.shuffle(finalAnswerIds);
        System.out.println("Answer choice IDs: " + finalAnswerIds);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("Who said this?");
        embed.setAuthor(commandMember.getEffectiveName(), null, commandMember.getEffectiveAvatarUrl());
        embed.setDescription(toGuess.content());

        ArrayList<Button> buttons = new ArrayList<>();
        for (long answer : finalAnswerIds) {
            User user = resolvedUsers.get(answer);
            String label = getLabel(user, options);
            String buttonId = "mg:" + commandMember.getIdLong() + ":" + correctUserId + ":" + answer;
            buttons.add(Button.secondary(buttonId, label));
        }

        ArrayList<ActionRow> rows = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i += 5) {
            rows.add(ActionRow.of(buttons.subList(i, Math.min(i + 5, buttons.size()))));
        }
        Button jumpButton = Button.link(toGuess.jumpUrl(), "Jump to Message").withEmoji(Emoji.fromUnicode("🔗")).asDisabled();
        rows.add(ActionRow.of(jumpButton));

        event.getHook().sendMessageEmbeds(embed.build()).addComponents(rows).queue();
    }

    private String getLabel(User user, MessageGuessrOptions options) {
        if (user == null) {
            return "Unknown User";
        } else if (options.usesNicknames()) {
            return user.getEffectiveName() + " (" + user.getName() + ")";
        } else {
            return user.getGlobalName() + " (" + user.getName() + ")";
        }
    }
}