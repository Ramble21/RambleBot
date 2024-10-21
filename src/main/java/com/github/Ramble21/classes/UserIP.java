package com.github.Ramble21.classes;

import net.dv8tion.jda.api.entities.User;


public class UserIP {

    private final String ip;
    private final User user;

    public UserIP(User usador){
        user = usador;
        String rand1 = Integer.toString((int)(1+Math.random()*255));
        String rand2 = Integer.toString((int)(1+Math.random()*255));
        String rand3 = Integer.toString((int)(1+Math.random()*255));
        String rand4 = Integer.toString((int)(1+Math.random()*255));
        ip = rand1 + "." + rand2 + "." + rand3 + "." + rand4;
    }
    public String getIp(){
        return ip;
    }
    public String getUsername(){
        return user.getEffectiveName();
    }
}
