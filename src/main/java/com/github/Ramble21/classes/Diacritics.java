package com.github.Ramble21.classes;

import java.text.Normalizer;

public class Diacritics {
    public static String removeDiacritics(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
