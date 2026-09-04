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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class MGPlay implements Command {

    // ConcurrentHashMap/newKeySet since these static caches can be hit by multiple slash command invocations on different event threads at once
    private static final Map<Long, MGUser> validUserCache = new ConcurrentHashMap<>();
    private static final Set<Long> invalidUserIds = ConcurrentHashMap.newKeySet();
    private static final AtomicBoolean cacheLoaded = new AtomicBoolean(false);
    private static final AtomicBoolean cacheDirty = new AtomicBoolean(false);

    private void loadPersistedCache(long serverId) {
        if (!cacheLoaded.compareAndSet(false, true)) return;
        MessageGuessr.ClassificationCache cache = MessageGuessr.loadClassificationCache(serverId);
        invalidUserIds.addAll(cache.invalidIds());
    }

    private void preloadDeletedUsers(long serverId, ArrayList<MGUser> deletedUsers) {
        loadPersistedCache(serverId);
        for (MGUser user : deletedUsers) {
            validUserCache.put(user.idLong(), user);
        }
    }

    private void persistCacheIfDirty(long serverId) {
        if (!cacheDirty.compareAndSet(true, false)) return;
        MessageGuessr.saveClassificationCache(serverId,
                new MessageGuessr.ClassificationCache(new HashSet<>(invalidUserIds)));
    }

    private MGUser classify(JDA jda, long serverId, long id) {
        loadPersistedCache(serverId);

        if (validUserCache.containsKey(id)) return validUserCache.get(id);
        if (invalidUserIds.contains(id)) return null;

        try {
            User user = jda.retrieveUserById(id).complete();
            if (user.isBot()) {
                invalidUserIds.add(id);
                cacheDirty.set(true);
                return null;
            }
            MGUser mgUser = new MGUser(user.getIdLong(), user.getName(), user.getEffectiveName(), user.getGlobalName());
            validUserCache.put(id, mgUser);
            cacheDirty.set(true);
            return mgUser;
        } catch (ErrorResponseException e) {
            invalidUserIds.add(id);
            cacheDirty.set(true);
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

        ArrayList<Long> uniqueUserIds = MessageGuessrDB.getUniqueUserIds(serverId, 1000);
        ArrayList<MGUser> deletedUsers = MessageGuessr.getManualDeletedUsers();
        for (MGUser user : deletedUsers) {
            uniqueUserIds.add(user.idLong());
        }
        preloadDeletedUsers(serverId, deletedUsers);

        Map<Long, Long> mainAccounts = MessageGuessr.getMainAccounts(uniqueUserIds);

        ArrayList<Long> goodUserIds = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : mainAccounts.entrySet()) {
            long rawUserId = entry.getKey();
            long mainId = entry.getValue();
            if (badUserIds.contains(rawUserId)) continue;
            if (classify(event.getJDA(), serverId, mainId) != null) {
                goodUserIds.add(rawUserId);
            }
        }
        persistCacheIfDirty(serverId);

        Message toGuess = MessageGuessrDB.getMessage(serverId, goodUserIds, badChannelIds);
        if (toGuess == null) {
            event.getHook().sendMessage("Message database is empty!").queue();
            return;
        }

        long correctUserId = mainAccounts.getOrDefault(toGuess.userId(), toGuess.userId());
        MGUser correctUser = classify(event.getJDA(), serverId, correctUserId);

        Set<Long> candidatePool = new HashSet<>(mainAccounts.values());
        candidatePool.remove(correctUserId);
        ArrayList<Long> shuffledPool = new ArrayList<>(candidatePool);
        Collections.shuffle(shuffledPool);

        ArrayList<Long> finalAnswerIds = new ArrayList<>();
        Map<Long, MGUser> resolvedUsers = new HashMap<>();
        finalAnswerIds.add(correctUserId);
        resolvedUsers.put(correctUserId, correctUser);

        for (long candidate : shuffledPool) {
            if (finalAnswerIds.size() >= options.getNumWrongAnswers() + 1) break;
            MGUser user = classify(event.getJDA(), serverId, candidate);
            if (user == null) {
                continue;
            }
            finalAnswerIds.add(candidate);
            resolvedUsers.put(candidate, user);
        }
        persistCacheIfDirty(serverId);

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
            MGUser user = resolvedUsers.get(answer);
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

    private String getLabel(MGUser user, MessageGuessrOptions options) {
        if (user == null) {
            return "Unknown User";
        } else if (options.usesNicknames()) {
            return user.effectiveName() + " (" + user.username() + ")";
        } else {
            return user.globalName() + " (" + user.username() + ")";
        }
    }
}