package com.github.Ramble21.classes;

import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;

public class CounterMessage {
    public String authorMention;
    public String jumpURL;
    public String startDateTimeString;
    public String endDateTimeString;
    public long maxMinutes;
    public HashMap<String, Integer> userTriggers;
    public CounterMessage(Message m, HashMap<String, Integer> userTriggers) {
        this.authorMention = m.getAuthor().getAsMention();
        this.jumpURL = m.getJumpUrl();
        this.startDateTimeString = m.getTimeCreated().toString();
        this.endDateTimeString = startDateTimeString;
        this.maxMinutes = 0;
        this.userTriggers = userTriggers;
        userTriggers.put(m.getAuthor().getId(), userTriggers.getOrDefault(m.getAuthor().getId(), 0));
    }
    public void addUserTrigger(Message m){
        userTriggers.put(m.getAuthor().getId(), userTriggers.getOrDefault(m.getAuthor().getId(), 0) + 1);
    }
}
