package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.Ramble21;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class HotLavaAndChicken extends ListenerAdapter {
    private static final Pattern[] regexes = {
            Pattern.compile("^la?[\\s\\-]*la?[\\s\\-]*la?[\\s\\-]*lava+$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^chi?[\\s\\-]*chi?[\\s\\-]*chi?[\\s\\-]*chicken+$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^steve['’ʼ‘`´]?s\\s+lava\\s+chicken,?\\s+yea?h?,?\\s+it['’ʼ‘`´]?s\\s+tasty\\s+as\\s+hell+$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^o*oh,?\\s+mamacita,?\\s+now\\s+(you['’ʼ‘`´]?re|your|ur)\\s+ringin['`´g]?\\s+(the|a)\\s+bell+$", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^crispy\\s+and\\s+juicy,?\\s+now\\s+(you['’ʼ‘`´]?re|your|ur)\\s+havin['’ʼ‘`´g]?\\s+a\\s+(snac|stac)k+$", Pattern.CASE_INSENSITIVE),
    };
    private static final Pattern minecraft = Pattern.compile("first\\s+we\\s+mine,?\\s+then\\s+we\\s+craft.?", Pattern.CASE_INSENSITIVE);
    private static final String[] originalLyrics = {
            "La-la-la-lava",
            "Chi-chi-chi-chicken",
            "Steve's Lava Chicken, yeah, it's tasty as hell",
            "Ooh, mamacita, now you're ringin' the bell",
            "Crispy and juicy, now you're havin' a stack",
            "Ooh, super spicy, it's a lava attackkkkk"
    };
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (Ramble21.isRambleBot(event.getAuthor())) {
            return;
        }
        boolean canSendMessages = event.getGuild().getSelfMember().hasPermission(event.getChannel().asGuildMessageChannel(), Permission.MESSAGE_SEND);
        if (!canSendMessages) {
            return;
        }
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw().toLowerCase());
        boolean allCaps = message.toUpperCase().equals(Diacritics.removeDiacritics(event.getMessage().getContentRaw()));
        boolean allLower = message.toLowerCase().equals(Diacritics.removeDiacritics(event.getMessage().getContentRaw()));
        if (minecraft.matcher(message).matches()) {
            event.getChannel().sendMessage("LET'S MINECRAFT!").queue();
            return;
        }
        for (int i = 0; i < regexes.length; i++) {
            if (regexes[i].matcher(message).matches()) {
                if (allCaps) {
                    event.getChannel().sendMessage(originalLyrics[i+1].toUpperCase()).queue();
                }
                else if (allLower) {
                    event.getChannel().sendMessage(originalLyrics[i+1].toLowerCase()).queue();
                }
                else {
                    event.getChannel().sendMessage(originalLyrics[i+1]).queue();
                }
                return;
            }
        }
    }
}
