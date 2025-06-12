package com.github.Ramble21;

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
import java.awt.*;
import java.io.File;

public class RambleBot {

    private static ShardManager shardManager;
    public static final Color killbotEnjoyer = new Color(174, 221, 0);
    public static final Color scaryOrange = new Color(240, 76, 0);
    private static boolean maintenanceMode;
    public static boolean validateToken(String token) {
        try {
            JDA testJda = JDABuilder.createDefault(token).build();
            testJda.awaitReady();
            System.out.println("Token successfully validated: " + testJda.getSelfUser().getAsTag());
            testJda.shutdown();
            return true;

        } catch (Exception e) {
            System.out.println("Invalid token or failed to connect: " + e.getMessage());
            return false;
        }
    }
    public static void main(String[] args) {

        // Load bot token
        Dotenv config = Dotenv.configure().load();
        String token = config.get("TOKEN");
        if (!validateToken(token)) {
            return;
        }

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
        builder.addEventListeners(
            new CommandListener(),
            new TextCommand(),
            new mewhen2(),
            new Test(),
            new HotLavaAndChicken(),
            new WordCensorListener(),
            new TheCounter()
        );

        shardManager = builder.build();

        // Declare global variables
        maintenanceMode = new File("local.flag").exists();
        if (maintenanceMode) {
            System.out.println("Bot turned on locally, maintenance mode automatically activated");
        }
    }

    public static JDA getJda() {
        return shardManager.getShardById(0);
    }
    public static void setMaintenanceMode(boolean b) {
        maintenanceMode = b;
    }
    public static boolean maintenanceMode() {
        return maintenanceMode;
    }

}
