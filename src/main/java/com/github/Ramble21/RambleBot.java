package com.github.Ramble21;

import com.github.Ramble21.classes.Ramble21;
import com.github.Ramble21.command.CommandListener;
import com.github.Ramble21.listeners.*;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;

public class RambleBot {

    private final ShardManager shardManager;
    private final Dotenv config;
    public static final Color killbotEnjoyer = new Color(174, 221, 0);
    public static final Color scaryOrange = new Color(240, 76, 0);
    private static JDA jda = null;

    private static boolean maintenanceMode;

    public RambleBot() throws LoginException {
        config = Dotenv.configure().load();
        String token = config.get("TOKEN");

        // Build shard manager
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.IDLE);
        builder.setActivity(Activity.playing("with fire"));

        // User Cache and Retrieval
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);

        // Gateway Intents
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Register listeners
        builder.addEventListeners(new CommandListener(), new TextCommand(), new mewhen2(), new Test(), new HotLavaAndChicken(), new WordCensorListener(), new TheCounter());

        // Add jda variable
        jda = JDABuilder.createDefault(token).build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Build shard manager
        shardManager = builder.build();

        // Declare global variables
        maintenanceMode = new File("local.flag").exists();
        if (maintenanceMode) {
            System.out.println("Bot turned on locally, maintenance mode automatically activated");
        }
    }

    public static JDA getJda() {
        return jda;
    }
    public static void setMaintenanceMode(boolean b) {
        maintenanceMode = b;
    }
    public static boolean maintenanceMode() {
        return maintenanceMode;
    }

    public static void main(String[] args) {
        try {
            new RambleBot();
        }
        catch (LoginException e) {
            System.out.println("ERROR: Provided bot token is invalid");
        }
    }
}
