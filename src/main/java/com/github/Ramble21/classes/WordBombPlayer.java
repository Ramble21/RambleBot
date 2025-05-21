package com.github.Ramble21.classes;

import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Objects;

public class WordBombPlayer {
    public User user;
    private int numLives;
    private final boolean[] remainingCharacters; // true -> that character still needs to be used
    public WordBombPlayer(User u, int startingLives) {
        this.user = u;
        this.numLives = startingLives;
        remainingCharacters = new boolean[26];
        Arrays.fill(remainingCharacters, true);
    }
    public int removeLife() { // returns num remaining lives after removal
        return --numLives;
    }
    public void addLife() {
        numLives++;
    }
    public boolean processTurn(String word) { // returns true if a new life is gained
        for (char c : word.toCharArray()) {
            int charValue = c - 'a';
            remainingCharacters[charValue] = false;
        }
        for (boolean c : remainingCharacters) {
            if (c) {
                return false;
            }
        }
        Arrays.fill(remainingCharacters, true);
        return true;
    }
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < remainingCharacters.length; i++) {
            String addition = remainingCharacters[i] ? ":regional_indicator_" + (char)('a' + i) + ":" : ":heavy_minus_sign:";
            s.append(addition);
            if (i == 12) {
                s.append("\n");
            }
        }
        return s.toString();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof WordBombPlayer other) {
            return user.getId().equals(other.user.getId());
        }
        if (obj instanceof User otherUser) {
            return user.getId().equals(otherUser.getId());
        }
        return false;
    }
    @Override
    public int hashCode() {
        return Objects.hash(user.getId());
    }
}