package com.github.Ramble21.classes;

import java.text.Normalizer;

public class Diacritics {
    public static boolean equalsIgnoreDiacritics(String str1, String str2) {
        String normalizedStr1 = Normalizer.normalize(str1, Normalizer.Form.NFD);
        String normalizedStr2 = Normalizer.normalize(str2, Normalizer.Form.NFD);
        String strippedStr1 = normalizedStr1.replaceAll("\\p{M}", "");
        String strippedStr2 = normalizedStr2.replaceAll("\\p{M}", "");
        return strippedStr1.equals(strippedStr2);
    }
    public static String removeDiacritics(String input) {
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
}
