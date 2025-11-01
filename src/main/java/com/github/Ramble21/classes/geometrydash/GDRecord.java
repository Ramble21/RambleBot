package com.github.Ramble21.classes.geometrydash;

public class GDRecord {
    private String submitterID;
    private int attempts;
    private int biasLevel;
    private boolean recordAccepted;
    private int levelID;

    public GDRecord(String submitterID, int attempts, int levelID) {
        GDLevel level = GDLevel.getLevelFromID(levelID);
        this.submitterID = submitterID;
        this.attempts = attempts;
        this.levelID = levelID;
        this.biasLevel = 0;
        this.recordAccepted = level.getDifficultyAsInt() != 1;
    }
    public GDRecord(String submitterID, int attempts, int levelID, boolean recordAccepted, int biasLevel) {
        this.submitterID = submitterID;
        this.attempts = attempts;
        this.levelID = levelID;
        this.biasLevel = biasLevel;
        this.recordAccepted = recordAccepted;
    }

}
