package com.github.Ramble21.classes.chess;

import com.github.Ramble21.classes.Location;

import java.util.HashSet;

public abstract class Piece {
    private final int relativeValue;
    private final boolean white;
    private Location currentPosition;
    public Piece(int relativeValue, boolean white, Location currentPosition) {
        this.relativeValue = relativeValue;
        this.white = white;
        this.currentPosition = currentPosition;
    }
    public void move(Location l) {
        currentPosition = l;
    }
    public boolean isColor(boolean color) {
        return color == this.white;
    }
    public int getRelativeValue() {
        return relativeValue;
    }
    public boolean white() {
        return white;
    }
    public Location getCurrentPosition() {
        return currentPosition;
    }
    public abstract HashSet<Location> getLegalMoves(Board b);
}
