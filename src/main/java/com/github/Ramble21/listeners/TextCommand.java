package com.github.Ramble21.listeners;

import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.commands.Ghostping;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static com.github.Ramble21.classes.Ramble21.isRambleBot;


public class TextCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();
        final Dotenv config;
        config = Dotenv.configure().load();
        String string1 = config.get("STRING_ONE");
        String string2 = config.get("STRING_TWO");

        if ((message.length() > 12) && Ramble21.isBrainrotServer(event.getGuild()) && (message.startsWith("r!ghostping"))){
            Ghostping ghostping = new Ghostping(event);
            ghostping.ghostping(false);
        }
        else if ((message.length() > 14) && Ramble21.isBrainrotServer(event.getGuild()) && (message.startsWith("r!rotcerebros"))
                && !user.getId().equals("840216337119969301")
                && !user.getId().equals("710503097343934494")
                && !user.getId().equals("870078781308674098")
                && !user.getId().equals("1135014520964784128")){
            Ghostping ghostping = new Ghostping(event);
            ghostping.ghostping(true);
            System.out.println("1");
        }
        if (message.contains("sigma sigma on the wall")){
            event.getChannel().sendMessage("who's the skibidiest of them all").queue();
        }
        if (Diacritics.removeDiacritics(message.toLowerCase()).contains(string1) && Ramble21.isBrainrotServer(event.getGuild()) && !(event.getAuthor().isBot())){
            event.getChannel().asTextChannel().sendMessage(string2).queue();
        }
        // Needed in order to make ghostping messages deleted successfully
        if (message.contains("ㅤㅤ") && isRambleBot(user)){
            event.getMessage().delete().queue();
        }

        // this code doesn't always work and i only needed it for one specific time so its commented out cause im too lazy to fix it

//        VoiceChannel voiceChannel = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(event.getMember()).getVoiceState()).getChannel()).asVoiceChannel();
//        AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
//
//        if (message.equalsIgnoreCase("r!sendtovc")){
//            audioManager.openAudioConnection(voiceChannel);
//        }
//        else if (message.equalsIgnoreCase("r!kickfromvc")){
//            audioManager.closeAudioConnection();
//        }
    }
}
