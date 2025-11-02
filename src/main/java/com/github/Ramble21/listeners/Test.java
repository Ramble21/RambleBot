package com.github.Ramble21.listeners;
import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import com.github.Ramble21.classes.geometrydash.GDLevel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equals("r!test") && Ramble21.isBotOwner(event.getAuthor())){
            System.out.println("Hello world!");
        }
        if (event.getMessage().getContentRaw().startsWith("r!fill_db_test") && Ramble21.isBotOwner(event.getAuthor())) {
            String[] parts = event.getMessage().getContentRaw().split("\\s+");
            String channelID = parts[1];
            MessageChannel channel = RambleBot.getJda().getTextChannelById(channelID);
            skibidiDopDopDopYesYes(channel);
        }
    }

    private static final Pattern NAME_PATTERN = Pattern.compile("Name:\\s*\\*\\*(.*?)\\*\\*");
    private static final Pattern DIFF_PATTERN = Pattern.compile("Difficulty:\\s*\\*\\*(.*?)\\*\\*");
    private static final Pattern ATTS_PATTERN = Pattern.compile("Attempts:\\s*\\*\\*(.*?)\\*\\*");

    public static void skibidiDopDopDopYesYes(MessageChannel channel) {
        System.out.println("Starting search...");

        final String keyword = "successfully added";
        final OffsetDateTime cutoffDate = OffsetDateTime.parse("2024-11-15T00:00:00Z");

        MessageHistory history = new MessageHistory(channel);
        List<Message> retrieved = history.retrievePast(100).complete();

        while (!retrieved.isEmpty()) {
            boolean reachedCutoff = false;

            for (Message msg : retrieved) {

                if (msg.getTimeCreated().isBefore(cutoffDate)) {
                    reachedCutoff = true;
                    break;
                }
                boolean embedMatch = msg.getEmbeds().stream()
                        .anyMatch(e -> e.getTitle() != null &&
                                e.getTitle().toLowerCase().contains(keyword.toLowerCase()));

                if (embedMatch) {
                    for (MessageEmbed embed : msg.getEmbeds()) {
                        System.out.println("Embed found: " + msg.getJumpUrl());

                        String text = embed.getDescription();
                        if (text != null) {
                            Matcher nameMatcher = NAME_PATTERN.matcher(text);
                            Matcher diffMatcher = DIFF_PATTERN.matcher(text);
                            Matcher attsMatcher = ATTS_PATTERN.matcher(text);

                            if (nameMatcher.find() && diffMatcher.find() && attsMatcher.find()) {
                                String name = nameMatcher.group(1).trim();
                                String diff = diffMatcher.group(1).trim();
                                int atts = Integer.parseInt(attsMatcher.group(1).trim());
                                GDLevel level = GDLevel.fromNameAndDiff(name, diff);
                                long submitterID = Objects.requireNonNull(msg.getInteractionMetadata()).getUser().getIdLong();
                                System.out.println(level);

                                if (level.getStars() != -1) {
                                    GDDatabase.addRecord(submitterID, atts, 0, true, level);
                                }
                            }
                        }
                    }
                }
            }
            if (reachedCutoff) break;
            retrieved = history.retrievePast(100).complete();
        }
        System.out.println("Finished!");
    }
}
