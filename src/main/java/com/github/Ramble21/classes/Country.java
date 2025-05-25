package com.github.Ramble21.classes;

import java.io.InputStream;
import java.util.*;

public class Country {

    private static final HashMap<String, List<String>> COUNTRY_CODES;


    public static Map.Entry<String, List<String>> getRandomCountry() {
        ArrayList<Map.Entry<String, List<String>>> entries = new ArrayList<>(COUNTRY_CODES.entrySet());
        return entries.get((int)(Math.random() * entries.size()));
    }

    private static void addSynonyms(String code, String... names) {
        COUNTRY_CODES
                .computeIfAbsent(code, k -> new ArrayList<>())
                .addAll(Arrays.asList(names));
    }
    static {
        COUNTRY_CODES = new HashMap<>();

        for (String code : Locale.getISOCountries()) {
            String name = new Locale("", code).getDisplayCountry(Locale.ENGLISH);
            COUNTRY_CODES.put(code, new ArrayList<>(List.of(name)));
        }

        addSynonyms("AE", "United Arab Emirates", "UAE");
        addSynonyms("GB", "United Kingdom", "UK", "Britain");
        addSynonyms("US", "United States", "USA", "America");
        addSynonyms("VA", "Vatican City", "Holy See", "Vatican");
        addSynonyms("KR", "South Korea", "ROK");
        addSynonyms("KP", "North Korea", "DPRK");
        addSynonyms("CD", "DRC", "Democratic Republic of the Congo");
        addSynonyms("CG", "Republic of the Congo", "Republic of Congo");
        addSynonyms("EH", "Western Sahara", "Sahrawi Arab Democratic Republic");
        addSynonyms("XK", "Kosovo");

        COUNTRY_CODES.entrySet().removeIf(entry -> !fileExistsInResources(entry.getKey().toUpperCase() + ".png"));
    }
    private static boolean fileExistsInResources(String fileName) {
        String resourcePath = "com/github/Ramble21/images/flags/wo/" + fileName;
        try (InputStream inputStream = Country.class.getClassLoader().getResourceAsStream(resourcePath)) {
            return inputStream != null;
        } catch (Exception e) {
            return false;
        }
    }
}
