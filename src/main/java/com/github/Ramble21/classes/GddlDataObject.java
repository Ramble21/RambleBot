package com.github.Ramble21.classes;

public class GddlDataObject {
    private final int id;
    private final int gddlTier;
    public GddlDataObject(int id, int gddlTier){
        this.id = id;
        this.gddlTier = gddlTier;
    }
    public int getId() {
        return id;
    }

    public int getGddlTier() {
        return gddlTier;
    }
}
