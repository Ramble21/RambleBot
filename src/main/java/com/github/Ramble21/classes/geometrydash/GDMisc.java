package com.github.Ramble21.classes.geometrydash;
import java.util.*;

public class GDMisc {

    public static void sortUserRecordsByDiff(ArrayList<GDRecord> records) {
        records.sort(
                Comparator
                        .comparingInt((GDRecord r) -> -r.level().getDifficultyAsInt())
                        .thenComparing((GDRecord r) -> -r.level().getGddlTier())
                        .thenComparing(GDRecord::biasLevel, Comparator.reverseOrder())
                        .thenComparing(GDRecord::attempts, Comparator.reverseOrder())
        );
    }

    public static void sortGuildLevelsByDiff(ArrayList<GDLevel> levels) {
        levels.sort(
                Comparator
                        .comparingInt((GDLevel l) -> -l.getDifficultyAsInt())
                        .thenComparing(GDLevel::getGddlTier, Comparator.reverseOrder())
        );
    }

    public static String getVictorsAsMention(ArrayList<GDRecord> records){
        int maximumVictorsListed = 20;
        StringBuilder victorsString = new StringBuilder("<@" + records.get(0).submitterID() + ">");
        for (int i = 1; i < records.size(); i++) {
            victorsString.append(", <@").append(records.get(i).submitterID()).append(">");
            if (i + 1 == maximumVictorsListed) {
                victorsString.append(", ").append(records.size() - maximumVictorsListed).append(" more");
                break;
            }
        }
        return victorsString.toString();
    }

    public static int getAverageAttempts(ArrayList<GDRecord> records){
        int totalAttempts = 0;
        for (GDRecord record : records) {
            totalAttempts += record.attempts();
        }
        return totalAttempts / records.size();
    }

    public static GDRecord getHardest(ArrayList<GDRecord> records) {
        sortUserRecordsByDiff(records);
        return records.get(0);
    }

    public static String getHardestsAsString(ArrayList<GDRecord> records){
        int maximumVictorsListed = 20;
        boolean platformer = records.get(0).level().isPlatformer();
        ArrayList<String> hardestVictors = new ArrayList<>();
        for (GDRecord r : records) {
            long submitterID = r.submitterID();
            ArrayList<GDRecord> submitterRecords = GDDatabase.getMemberRecords(submitterID, platformer);
            sortUserRecordsByDiff(submitterRecords);
            if (submitterRecords.get(0).levelID() == r.levelID()) {
                hardestVictors.add("<@" + submitterID + ">");
            }
        }
        if (hardestVictors.isEmpty()) {
            return "";
        }
        String levelType = platformer ? "platformer" : "classic";
        StringBuilder s = new StringBuilder("This is the hardest ").append(levelType).append(" level beaten by ").append(hardestVictors.get(0));
        for (int i = 1; i < hardestVictors.size(); i++) {
            s.append(hardestVictors.get(i));
            if (i + 1 == maximumVictorsListed) {
                s.append(", ").append(records.size() - maximumVictorsListed).append(" more");
                break;
            }
        }
        s.append("!");
        return s.toString();
    }

    public static GDRecord getAttemptMin(ArrayList<GDRecord> records, String difficulty) {
        int attemptMin = Integer.MAX_VALUE;
        GDRecord attemptMinRecord = null;
        for (GDRecord r : records) {
            if (r.level().getDifficulty().equals(difficulty) && (r.attempts() < attemptMin)) {
                attemptMinRecord = r;
                attemptMin = r.attempts();
            }
        }
        return attemptMinRecord;
    }

    public static GDRecord getAttemptMax(ArrayList<GDRecord> records, String difficulty) {
        int attemptMax = Integer.MIN_VALUE;
        GDRecord attemptMaxRecord = null;
        for (GDRecord r : records) {
            if (r.level().getDifficulty().equals(difficulty) && (r.attempts() > attemptMax)) {
                attemptMaxRecord = r;
                attemptMax = r.attempts();
            }
        }
        return attemptMaxRecord;
    }

    public static String makeExtremaString(GDRecord record){
        if (record == null){
            return "N/A\n";
        }
        else{
            return "**" + record.level().getName() + "** (" + record.attempts() + " atts)\n";
        }
    }
}

