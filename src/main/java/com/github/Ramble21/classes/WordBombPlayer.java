package com.github.Ramble21.classes;

import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;
import java.util.Objects;

public class WordBombPlayer {
    public User user;
    public int numLives;
    public boolean[] remainingCharacters; // true -> that character still needs to be used
    public WordBombPlayer(User u, int startingLives) {
        this.user = u;
        this.numLives = startingLives;
        remainingCharacters = new boolean[26];
        Arrays.fill(remainingCharacters, true);
    }
    public boolean removeLife() { // returns true if no more lives
        return --numLives == 0;
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
        return true;
    }
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
    public int hashCode() {
        return Objects.hash(user.getId());
    }
}