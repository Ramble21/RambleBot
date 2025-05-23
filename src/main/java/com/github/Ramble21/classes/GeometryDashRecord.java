package com.github.Ramble21.classes;

public class GeometryDashRecord {

    private GeometryDashLevel level;
    private int attempts;
    private String submitterID;

    public GeometryDashRecord(GeometryDashLevel level, int attempts, String submitterID) {
        this.level = level;
        this.attempts = attempts;
        this.submitterID = submitterID;
    }

}
