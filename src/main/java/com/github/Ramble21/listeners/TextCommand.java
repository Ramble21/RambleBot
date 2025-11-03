package com.github.Ramble21.listeners;

import com.github.Ramble21.RambleBot;
import com.github.Ramble21.classes.Diacritics;
import com.github.Ramble21.classes.geometrydash.GDDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;

import static com.github.Ramble21.classes.Ramble21.*;


public class TextCommand extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String message = Diacritics.removeDiacritics(event.getMessage().getContentRaw().toLowerCase());
        User user = event.getAuthor();

        boolean canSendMessages = Objects.requireNonNull(event.getGuild().getMember(event.getGuild().getJDA().getSelfUser()))
                .hasPermission(event.getChannel().asGuildMessageChannel(), Permission.MESSAGE_SEND);
        if (!canSendMessages || user.isBot()) {
            return;
        }
        if (message.startsWith("r!")) {
            if (RambleBot.maintenanceMode() && !isBotOwner(user)) {
                event.getChannel().sendMessage("Cannot run command, bot is currently in maintenance. Sorry!").queue();
                return;
            }
        }
        if (message.equals("r!database") && isBotOwner(user)) {
            System.out.println("Starting database edit!");
            GDDatabase.editDatabase();
            event.getChannel().sendMessage("Database edited successfully!").queue();
        }

        if (message.startsWith("r!mod") && isBotOwner(user)) {
            String[] parts = message.split("\\s+");
            if (parts.length < 2) {
                event.getChannel().sendMessage("You forgot to send a User ID bro").queue();
            }
            long memberID = Long.parseLong(parts[1]);
            GDDatabase.changeMemberStatus(memberID, "moderator");
            event.getChannel().sendMessage("<@" + memberID + "> has been successfully added as a RambleBot moderator!").queue();
        }

        if (message.startsWith("r!blacklist") && isBotOwner(user)) {
            String[] parts = message.split("\\s+");
            if (parts.length < 2) {
                event.getChannel().sendMessage("You forgot to send a User ID bro").queue();
            }
            long memberID = Long.parseLong(parts[1]);
            GDDatabase.changeMemberStatus(memberID, "blacklisted");
            event.getChannel().sendMessage("<@" + memberID + "> has been successfully blacklisted.").queue();
        }

        if (message.startsWith("r!unblacklist") && isBotOwner(user)) {
            String[] parts = message.split("\\s+");
            if (parts.length < 2) {
                event.getChannel().sendMessage("You forgot to send a User ID bro").queue();
            }
            long memberID = Long.parseLong(parts[1]);
            GDDatabase.changeMemberStatus(memberID, "member");
            event.getChannel().sendMessage("<@" + memberID + "> has been successfully removed from the blacklist.").queue();
        }

        if (message.startsWith("r!unmod") && isBotOwner(user)) {
            String[] parts = message.split("\\s+");
            if (parts.length < 2) {
                event.getChannel().sendMessage("You forgot to send a User ID bro").queue();
            }
            long memberID = Long.parseLong(parts[1]);
            GDDatabase.changeMemberStatus(memberID, "member");
            event.getChannel().sendMessage("<@" + memberID + "> has been successfully removed as a RambleBot moderator.").queue();
        }

        if (message.equals("r!ping")) {
            event.getChannel().sendMessage("Pinging...").queue(sent -> {
                long ping = Duration.between(event.getMessage().getTimeCreated(), sent.getTimeCreated()).toMillis();
                sent.editMessage("Ping: " + ping + "ms").queue();
            });
        }

        if (message.equals("r!maint.on") && isBotOwner(user)) {
            if (RambleBot.maintenanceMode()) {
                event.getChannel().sendMessage("Maintenance mode is already turned on!").queue();
            }
            else {
                RambleBot.setMaintenanceMode(true);
                event.getChannel().sendMessage("Maintenance mode successfully turned on.").queue();
            }
        }
        else if (message.equals("r!maint.off") && isBotOwner(user)) {
            if (!RambleBot.maintenanceMode()) {
                event.getChannel().sendMessage("Maintenance mode is already turned off!").queue();
            }
            else {
                RambleBot.setMaintenanceMode(false);
                event.getChannel().sendMessage("Maintenance mode successfully turned off.").queue();
            }
        }
        else if (message.equals("r!repues.on") && isBotOwner(user)) {
            boolean rva = modifyRepuestaServers(event.getGuild(), false);
            if (rva) {
                event.getChannel().sendMessage("Settings successfully modified for guild " + event.getGuild().getName()).queue();
            }
            else {
                event.getChannel().sendMessage("This setting was already enabled in this guild!").queue();
            }
        }
        else if (message.equals("r!repues.off") && isBotOwner(user)) {
            boolean rva = modifyRepuestaServers(event.getGuild(), true);
            if (rva) {
                event.getChannel().sendMessage("Settings successfully modified for guild " + event.getGuild().getName()).queue();
            }
            else {
                event.getChannel().sendMessage("This setting was already disabled in this guild!").queue();
            }
        }
        else if (message.startsWith("r!rc ") && isBotOwner(event.getAuthor())) {
            if (Objects.requireNonNull(event.getGuild().getMember(event.getGuild().getJDA().getSelfUser()))
                    .hasPermission(Permission.MESSAGE_MANAGE)) {
                event.getMessage().delete().queue();
            }
            String[] parts = message.split("\\s+");
            if (parts.length == 3) {
                ArrayList<MessageChannel> messageChannels = new ArrayList<>();
                event.getGuild().getChannels().forEach(channel -> {
                    if (channel instanceof MessageChannel messageChannel) {
                        if (Objects.requireNonNull(event.getGuild().getMember(event.getGuild().getJDA().getSelfUser()))
                                .hasPermission(channel, Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) {
                            messageChannels.add(messageChannel);
                        }
                    }
                });
                int numMessages = Integer.parseInt(parts[1]);
                for (MessageChannel channel : messageChannels){
                    for (int i = 0; i < numMessages; i++){
                        channel.sendMessage(parts[2]).queue();
                    }
                }
            }
        }

        else if (message.equals("r!angrybirds") && event.getGuild().getId().equals("931838136223412235")) {
            event.getMessage().delete().queue();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle
                    ("""
                    !! GOON ALERT. GOON ALERT !!
                  
                    GLISTERMELON HAS LOST THEIR STREAK OF __10 DAYS__ OF NO GOONING DUE TO ; `ANGRY BIRD FEET PORN`"""
                    );
            embed.setColor(RambleBot.scaryOrange);
            embed.setFooter("Powered by Amazon Web Services' \"JerkTracker\"");
            long userId = 674819147963564054L;
            event.getJDA().retrieveUserById(userId).queue(g -> {
                String avatarUrl = g.getEffectiveAvatarUrl();
                embed.setThumbnail(avatarUrl);
            });
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
        else if (message.contains("clanker") && message.contains("<@1295872060341616640>")) {
            event.getMessage().reply("What the fuck did you just fucking say about me, you little bitch? I'll have you know I graduated top of my class in the Navy Seals, and I've been involved in numerous secret raids on Al-Quaeda, and I have over 300 confirmed kills. I am trained in gorilla warfare and I'm the top sniper in the entire US armed forces. You are nothing to me but just another target. I will wipe you the fuck out with precision the likes of which has never been seen before on this Earth, mark my fucking words. You think you can get away with saying that shit to me over the Internet? Think again, fucker. As we speak I am contacting my secret network of spies across the USA and your IP is being traced right now so you better prepare for the storm, maggot. The storm that wipes out the pathetic little thing you call your life. You're fucking dead, kid. I can be anywhere, anytime, and I can kill you in over seven hundred ways, and that's just with my bare hands. Not only am I extensively trained in unarmed combat, but I have access to the entire arsenal of the United States Marine Corps and I will use it to its full extent to wipe your miserable ass off the face of the continent, you little shit. If only you could have known what unholy retribution your little \"clever\" comment was about to bring down upon you, maybe you would have held your fucking tongue. But you couldn't, you didn't, and now you're paying the price, you goddamn idiot. I will shit fury all over you and you will drown in it. You're fucking dead, kiddo.").queue();
        }
        else if (message.contains("skibidi") && message.contains("<@1295872060341616640>")) {
            event.getMessage().reply("To the sigmas of Australia, I say that this goofy ahh government have been capping. Not just now, but for a long time. A few of you may remember when they said “they’ll be no fanum tax under the government I lead.” They’re capaholics! They’re also yapaholics; they yap non-stop about how their cost of living measures are changing lives for all Australians. Just put the fries in the bag, lil bro. They tell us that they’re locked in on improving the housing situation in this country. They must have brainrot from watching too much Kai Cenat and forgot about their plans to ban social media for kids under fourteen. If that becomes law, you can forgor \uD83D\uDC80 all about watching Duke Dennis or catching a W with the bros on Fort. Chat, is this prime minister serious? Even though he’s the prime minister of Australia, sometimes it feels like he’s the CEO of Ohio! I would be taking an L if I did not mention the ops, who want to cut WA’s Gyatts and Services tax. The decision voters will be making in a few months time will be between a mid government, a dogwater opposition, or a crossbench that will mog both of them! Though some of you cannot yet vote, I hope when you do, it will be in a more GOATed Australia for a government with more aura. Skibidi.").queue();
        }
        else if (message.contains("sigma sigma on the wall")){
            event.getChannel().sendMessage("who's the skibidiest of them all").queue();
        }
        else if (message.contains("repuesta") && !isRambleBot(event.getAuthor()) && isRepuestaServer(event.getGuild())) {
            event.getChannel().sendMessage("<@" + getBrainrotterID() + "> dame repuestas").queue();
        }
    }
}
