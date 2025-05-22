package com.github.Ramble21.classes;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class WpmScore {

    private final int wpm;
    private final String userId;
    private final String guildId;

    public WpmScore(int wpm, User user, Guild guild){
        this.wpm = wpm;
        this.userId = user.getId();
        this.guildId = guild.getId();
    }
    public int getWpm(){
        return wpm;
    }
    public String getUserId(){
        return userId;
    }
    public String getGuildId(){
        return guildId;
    }

    public String toString(){
        return "WpmScore{" + "wpm='" + wpm + "\\'" + ", user=" + userId + "\\'" + ", guild=" + guildId + "}";
    }
}
