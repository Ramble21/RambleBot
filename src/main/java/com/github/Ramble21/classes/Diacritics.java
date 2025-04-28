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
    public static boolean isOneAway(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        if (Math.abs(len1 - len2) > 1) {
            return false;
        }
        String shorter = len1 < len2 ? s1 : s2;
        String longer = len1 < len2 ? s2 : s1;
        int index1 = 0, index2 = 0;
        boolean foundDifference = false;

        while (index1 < shorter.length() && index2 < longer.length()) {
            if (shorter.charAt(index1) != longer.charAt(index2)) {
                if (foundDifference) {
                    return false;
                }
                foundDifference = true;
                if (len1 == len2) {
                    index1++;
                }
            } else {
                index1++;
            }
            index2++;
        }
        return true;
    }

    public static boolean containsOneAway(String big, String small) {
        if (big.contains(small)) {
            return true;
        }
        int smallLen = small.length();
        for (int i = 0; i <= big.length() - smallLen; i++) {
            String sub = big.substring(i, i + smallLen);
            if (isOneAway(sub, small)) {
                return true;
            }
        }
        if (smallLen + 1 <= big.length()) {
            for (int i = 0; i <= big.length() - (smallLen + 1); i++) {
                String sub = big.substring(i, i + smallLen + 1);
                if (isOneAway(sub, small)) {
                    return true;
                }
            }
        }
        return false;
    }
}
