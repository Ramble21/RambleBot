package com.github.Ramble21.classes.geometrydash;

import java.util.ArrayList;
import java.util.HashMap;

public class GDGuildLB {
    private final ArrayList<GDLevel> levels;
    private final HashMap<GDLevel, Integer> levelsToAvgAttempts;
    private final long guildID;
    public GDGuildLB(long guildID, boolean platformer) {
        this.levels = GDDatabase.getGuildLevels(guildID, platformer);
        this.levelsToAvgAttempts = new HashMap<>();
        this.guildID = guildID;
        for (GDLevel l : levels) {
            ArrayList<GDRecord> levelRecords = GDDatabase.getLevelRecords(l.getId(), guildID);
            int avgAttempts = GDMisc.getAverageAttempts(levelRecords);
            levelsToAvgAttempts.put(l, avgAttempts);
        }
        GDMisc.sortGuildLevelsByDiff(levels, levelsToAvgAttempts);
    }
    public ArrayList<GDLevel> levels() {
        return levels;
    }
    public HashMap<GDLevel, Integer> levelsToAvgAttempts() {
        return levelsToAvgAttempts;
    }
    public long guildID() {
        return guildID;
    }
    public int lbPositionOf(GDLevel level){
        for (int i = 0; i < levels().size(); i++) {
            if (levels().get(i).equals(level)) {
                return i + 1;
            }
        }
        throw new RuntimeException("Level " + level.getName() + " does not appear on the leaderboard for guild ID " + guildID);
    }
}
