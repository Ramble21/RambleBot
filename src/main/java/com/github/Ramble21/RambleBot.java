package com.github.Ramble21;

import com.github.Ramble21.command.CommandListener;
import com.github.Ramble21.listeners.TextCommand;
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

public class RambleBot {

    private final ShardManager shardManager;
    private final Dotenv config;
    private static JDA jda = null;

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
        // builder.enableCache(CacheFlag.VOICE_STATE);

        // Gateway Intents
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Register listeners
        builder.addEventListeners(new CommandListener(), new TextCommand());

        // Add jda variable
        jda = JDABuilder.createDefault(token).build();
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Build shard manager
        shardManager = builder.build();
    }

    public ShardManager getShardManager() {
        return shardManager;
    }
    public Dotenv getConfig(){
        return config;
    }
    public static JDA getJda() { return jda; }

    public static void main(String[] args) {


        try {
            RambleBot bot = new RambleBot();
        }
        catch (LoginException e) {
            System.out.println("ERROR: Provided bot token is invalid");
        }
    }
}
