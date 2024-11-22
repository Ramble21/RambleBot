package com.github.Ramble21.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Ghostping {

    private final MessageReceivedEvent event;

    public Ghostping(MessageReceivedEvent event){
        this.event = event;
    }

    public void ghostping(){

        String[] parts = event.getMessage().getContentDisplay().split(" ");
        if (parts.length < 2) {
            return;
        }

        String tag = parts[1];

        System.out.println(tag);
        Guild guild = event.getGuild();

        String userId = "bug";
        List<Member> members = guild.getMembersByName(tag, true);
        if (!members.isEmpty()) {
            userId = members.get(0).getId();
            System.out.println(userId);
        }
        else {
            System.out.println("glistermelon is fat");
        }

        Member member = guild.getMemberById(userId);

        try {
            if (member != null){
                String pingee1 = "<@" + member.getId() + ">ㅤㅤ";
                event.getMessage().delete().queue();
                for (int i =0; i < 400; i++) {
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
            event.getChannel().sendMessage("<@739978476651544607> theres a bug in your code lol").queue();
        }
    }
}
