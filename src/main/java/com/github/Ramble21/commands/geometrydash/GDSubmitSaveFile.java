package com.github.Ramble21.commands.geometrydash;

import com.github.Ramble21.classes.geometrydash.savefile.CCParseProgress;
import com.github.Ramble21.command.Command;
import com.github.Ramble21.classes.geometrydash.savefile.CCDecryptUtils;
import com.github.Ramble21.classes.geometrydash.savefile.CCLevelParser;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Objects;


public class GDSubmitSaveFile implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event){
        event.deferReply().queue();
        InteractionHook hook = event.getHook();
        Message.Attachment saveFile = Objects.requireNonNull(event.getOption("savefile")).getAsAttachment();
        String fileName = saveFile.getFileName();
        if (!fileName.equals("CCGameManager.dat")) {
            hook.sendMessage("Invalid save file! Upload your raw **CCGameManager.dat** file as an attachment\n(Windows: `%localappdata%/GeometryDash/CCGameManager.dat`)\n(Mac: `~/Library/Application Support/GeometryDash/CCGameManager.dat`)").setEphemeral(true).queue();
            return;
        }
        String decrypted;
        try {
            decrypted = CCDecryptUtils.decryptFile(saveFile);
        } catch (Exception e) {
            event.reply("An error occurred while trying to decrypt & decompress your save file: `" + e.getMessage() + "`\n\nDouble check that you uploaded the right file and that your save isn't corrupted!").setEphemeral(true).queue();
            throw new RuntimeException(e);
        }
        hook.sendMessage("Save file successfully decrypted, scanning all levels. This may take a long time.").queue();

        CCParseProgress parseProgress = CCLevelParser.parseOnlineLevels(decrypted, event.getMember());
        if (!parseProgress.isComplete()) {
            hook.sendMessage("Safe file partially scanned, rate limit reached. Please try again later.").queue();
            return;
        }
        hook.sendMessage("Save file successfully scanned! " + parseProgress.completedCount() + " new demons were added to your RambleBot profile!").queue();
    }
}