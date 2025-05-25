package com.github.Ramble21.classes;

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
        addSynonyms("VA", "Vatican City", "Holy See");
        addSynonyms("RU", "Russia", "Russian Federation");
        addSynonyms("KR", "South Korea", "Republic of Korea", "ROK");
        addSynonyms("KP", "North Korea", "Democratic People's Republic of Korea", "DPRK");
        addSynonyms("VE", "Venezuela", "Bolivarian Republic of Venezuela");
        addSynonyms("IR", "Iran", "Islamic Republic of Iran");
        addSynonyms("SY", "Syria", "Syrian Arab Republic");
        addSynonyms("TZ", "Tanzania", "United Republic of Tanzania");
        addSynonyms("LA", "Laos", "Lao People's Democratic Republic");
        addSynonyms("CD", "DRC", "Democratic Republic of the Congo");
        addSynonyms("CG", "Republic of the Congo");
        addSynonyms("EH", "Western Sahara", "Sahrawi Arab Democratic Republic");
        addSynonyms("BQ", "Bonaire, Sint Eustatius and Saba");
        addSynonyms("MF", "Saint Martin");
        addSynonyms("SX", "Sint Maarten");
        addSynonyms("XK", "Kosovo");

        COUNTRY_CODES.put("GB-ENG", List.of("England"));
        COUNTRY_CODES.put("GB-SCT", List.of("Scotland"));
        COUNTRY_CODES.put("GB-WLS", List.of("Wales"));
        COUNTRY_CODES.put("GB-NIR", List.of("Northern Ireland"));
        COUNTRY_CODES.put("EU",     List.of("European Union"));
    }
}
