package com.github.Ramble21.classes.geometrydash;

import java.util.ArrayList;

public class GDGuildLB {
    private final ArrayList<GDLevel> levels;
    private final long guildID;
    public GDGuildLB(long guildID, boolean platformer) {
        this.levels = GDDatabase.getGuildLevels(guildID, platformer);
        this.guildID = guildID;
        GDMisc.sortGuildLevelsByDiff(levels);
    }
    public ArrayList<GDLevel> levels() {
        return levels;
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
