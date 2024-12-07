package com.github.Ramble21.commands;

import com.github.Ramble21.classes.Ramble21;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Ghostping {

    private final MessageReceivedEvent event;
    private static final HashSet<Integer> dailyIds = new HashSet<>();

    public Ghostping(MessageReceivedEvent event){
        this.event = event;
    }

    public void ghostping(Boolean isCabezaRot){

        String[] parts = event.getMessage().getContentDisplay().split(" ");
        if (parts.length < 2) {
            return;
        }

        String tag = parts[1];
        Guild guild = event.getGuild();

        String userId = "bug";
        List<Member> members = guild.getMembersByName(tag, true);
        if (!members.isEmpty()) {
            userId = members.get(0).getId();
        }
        else {
            System.out.println("glistermelon is fat");
        }

        Member member = guild.getMemberById(userId);

        try {
            if (member != null){
                String pingee1 = "<@" + member.getId() + ">ㅤㅤ";

                if (isCabezaRot){

                    int seed = Ramble21.generateDailySeed(member.getId());
                    System.out.println(dailyIds);
                    System.out.println("Seed: " + seed);

                    if (dailyIds.contains(seed) && !event.getAuthor().getId().equalsIgnoreCase("739978476651544607")){
                        event.getMessage().reply("You can only rot cerebros once per day, nice try").queue();
                        return;
                    }
                    dailyIds.add(seed);

                    event.getMessage().delete().queue();
                    ArrayList<MessageChannel> messageChannels = new ArrayList<>();
                    guild.getChannels().forEach(channel -> {
                        if (channel instanceof MessageChannel messageChannel) {
                            if (guild.getSelfMember().hasPermission((GuildChannel) channel, Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND)) {
                                messageChannels.add(messageChannel);
                            }
                        }
                    });
                    for (MessageChannel channel : messageChannels){
                        for (int i = 0; i < 3; i++){
                            channel.sendMessage(pingee1).queue();
                        }
                    }
                }
                else{
                    event.getChannel().asTextChannel().sendMessage(pingee1).queue();
                    event.getChannel().asTextChannel().sendMessage(pingee1).queue();
                    event.getChannel().asTextChannel().sendMessage(pingee1).queue();
                    event.getChannel().asTextChannel().sendMessage(pingee1).queue();
                    event.getChannel().asTextChannel().sendMessage(pingee1).queue();
                }
            }
            else{
                System.out.println("member is null");
            }
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException();
        }
    }
}
