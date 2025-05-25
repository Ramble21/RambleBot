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
        addSynonyms("US", "United States", "USA", "America", "United States of America");
        addSynonyms("VA", "Vatican City", "Holy See", "Vatican");
        addSynonyms("KR", "South Korea", "ROK", "Republic of Korea");
        addSynonyms("KP", "North Korea", "DPRK", "Democratic People's Republic of Korea");
        addSynonyms("CD", "DRC", "Democratic Republic of the Congo", "Congo-Kinshasa");
        addSynonyms("CG", "Republic of the Congo", "Republic of Congo", "Congo-Brazzaville");
        addSynonyms("EH", "Western Sahara", "Sahrawi Arab Democratic Republic");
        addSynonyms("XK", "Kosovo", "Republic of Kosovo");
        addSynonyms("TC", "Turks and Caicos", "Turks and Caicos Islands");
        addSynonyms("CV", "Cape Verde", "Cabo Verde");
        addSynonyms("VI", "U.S. Virgin Islands", "US Virgin Islands", "Virgin Islands");
        addSynonyms("VN", "Vietnam", "Viet Nam");
        addSynonyms("TW", "Taiwan", "Republic of China", "-1000 Social Credit");
        addSynonyms("MK", "North Macedonia", "Macedonia", "Former Yugoslav Republic of Macedonia", "FYROM");
        addSynonyms("PS", "Palestine", "State of Palestine", "Palestinian Territory");
        addSynonyms("MF", "Saint Martin", "St. Martin", "St Martin", "Sint Maarten", "Saint-Martin");
        addSynonyms("BL", "St. Barthelemy", "St Barthelemy", "Saint Barthelemy");
        addSynonyms("PM", "Saint Pierre and Miquelon", "St. Pierre and Miquelon", "St Pierre and Miquelon");
        addSynonyms("VC", "Saint Vincent and the Grenadines", "St. Vincent and the Grenadines", "St Vincent and the Grenadines");
        addSynonyms("KN", "Saint Kitts and Nevis", "St. Kitts and Nevis", "St Kitts and Nevis");
        addSynonyms("LC", "Saint Lucia", "St. Lucia", "St Lucia");
        addSynonyms("SH", "Saint Helena", "St. Helena", "St Helena");
        addSynonyms("FM", "Micronesia", "Federated States of Micronesia");
        addSynonyms("CC", "Cocos Islands", "Cocos (Keeling) Islands");
        addSynonyms("BV", "Bouvet Island");
        addSynonyms("SJ", "Svalbard and Jan Mayen", "Svalbard & Jan Mayen");
        addSynonyms("HM", "Heard Island and McDonald Islands", "Territory of Heard Island and McDonald Islands");
        addSynonyms("UM", "U.S. Minor Outlying Islands", "United States Minor Outlying Islands");
        addSynonyms("IO", "British Indian Ocean Territory", "Chagos Archipelago");
        addSynonyms("GS", "South Georgia and the South Sandwich Islands", "South Georgia & South Sandwich Islands");
        // these were generated with ai i might add more if people complain while actually playing

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
