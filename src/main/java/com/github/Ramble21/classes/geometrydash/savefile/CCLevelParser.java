package com.github.Ramble21.classes.geometrydash.savefile;

import com.github.Ramble21.classes.geometrydash.GDLevel;
import com.github.Ramble21.commands.geometrydash.GeometryDashRecordSubmit;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCLevelParser {

    public static CCParseProgress parseOnlineLevels(String decryptedXml, Member member) {
        HashSet<String> processedLevels = new HashSet<>();
        HashSet<String> demonLevels = new HashSet<>();

        int gs9Index = decryptedXml.indexOf("<k>GS_9</k>");
        if (gs9Index != -1) {
            int dictStart = decryptedXml.indexOf("<d>", gs9Index);
            int dictEnd = findMatchingClosingTag(decryptedXml, dictStart);

            if (dictStart != -1 && dictEnd != -1) {
                String gs9Section = decryptedXml.substring(dictStart, dictEnd);
                Pattern pattern = Pattern.compile("<k>(\\d+)</k><s>10</s>");
                Matcher matcher = pattern.matcher(gs9Section);

                while (matcher.find()) {
                    demonLevels.add(matcher.group(1));
                }
            }
        }

        int glm03Index = decryptedXml.indexOf("<k>GLM_03</k>");
        if (glm03Index == -1) {
            return new CCParseProgress(0, processedLevels, true);
        }
        int dictStart = decryptedXml.indexOf("<d>", glm03Index);
        int dictEnd = findMatchingClosingTag(decryptedXml, dictStart);
        if (dictStart == -1 || dictEnd == -1) {
            return new CCParseProgress(0, processedLevels, true);
        }
        String glm03Section = decryptedXml.substring(dictStart, dictEnd);
        Pattern pattern = Pattern.compile("<k>(\\d{5,})</k><d>(.*?)</d>");
        Matcher matcher = pattern.matcher(glm03Section);

        int levelsCompleted = 0;
        while (matcher.find()) {
            String levelId = matcher.group(1);
            String levelData = matcher.group(2);

            if (processedLevels.contains(levelId)) {
                continue;
            }

            if (demonLevels.contains(levelId) && levelData.contains("<k>k19</k><i>100</i>")) {
                int attempts = 0;
                Pattern attemptsPattern = Pattern.compile("<k>k18</k><i>(\\d+)</i>");
                Matcher attemptsMatcher = attemptsPattern.matcher(levelData);
                if (attemptsMatcher.find()) {
                    attempts = Integer.parseInt(attemptsMatcher.group(1));
                }

                GDLevel level = GDLevel.fromID(Long.parseLong(levelId));
                if (level.getDifficulty() == null) {
                    // API error - return progress so far
                    System.out.println("API error detected at level " + levelId + ". Progress saved: " +
                            levelsCompleted + " new demons submitted this run.");
                    System.out.println("Already processed " + processedLevels.size() + " levels total.");
                    return new CCParseProgress(levelsCompleted, processedLevels, false);
                }
                if (level.getDifficultyAsInt() < 10) {
                    boolean levelAlrSubmitted = GeometryDashRecordSubmit.submitRecord(level, member, attempts);
                    if (!levelAlrSubmitted) {
                        levelsCompleted++;
                    }
                }
                processedLevels.add(levelId);
            }
        }

        System.out.println("Scan complete! New demons this run: " + levelsCompleted);
        System.out.println("Total processed levels: " + processedLevels.size());
        return new CCParseProgress(levelsCompleted, processedLevels, true);
    }

    private static int findMatchingClosingTag(String xml, int openTagPos) {
        int depth = 1;
        int pos = xml.indexOf(">", openTagPos) + 1;

        while (pos < xml.length() && depth > 0) {
            if (xml.startsWith("<d>", pos) || xml.startsWith("<d ", pos)) {
                depth++;
                pos += 3;
            } else if (xml.startsWith("</d>", pos)) {
                depth--;
                if (depth == 0) return pos + 4;
                pos += 4;
            } else {
                pos++;
            }
        }
        return -1;
    }
}
