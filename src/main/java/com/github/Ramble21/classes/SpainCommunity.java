package com.github.Ramble21.classes;

public class SpainCommunity {

    private static String[] names = {
            "Valencia", "Madrid", "Catalonia", "Aragon", "Murcia",
            "Andalusia", "Extremadura", "Castile and Leon", "Navarre",
            "Asturias", "Galicia", "Canary Islands", "Ceuta", "Melilla",
            "Cantabria", "La Rioja", "Castilla La Mancha", "Balearic Islands",
            "Basque Country"
    };
    private static String[] spanishNames = {
            "Valencia", "Madrid", "Cataluña", "Aragon", "Murcia",
            "Andalucía", "Extremadura", "Castilla y León", "Navarra",
            "Asturias", "Galicia", "Islas Canarias", "Ceuta", "Melilla",
            "Cantabria", "La Rioja", "Castilla La Mancha", "Islas Baleares",
            "País Vasco"
    };
    private static String[] namesSnakeCase = {
            "valencia", "madrid", "catalonia", "aragon", "murcia",
            "andalusia", "extremadura", "castile_and_leon", "navarre",
            "asturias", "galicia", "canary_islands", "ceuta", "melilla",
            "cantabria", "la_rioja", "castilla_la_mancha", "balearic_islands",
            "basque_country"
    };

    private final String name;
    private final String nameSnakeCase;
    private final String spanishName;

    public SpainCommunity(){
        int randomNo = (int)(Math.random()*names.length);
        name = names[randomNo];
        nameSnakeCase = namesSnakeCase[randomNo];
        spanishName = spanishNames[randomNo];
    }
    public String getName(){
        return name;
    }
    public String getNameSnakeCase(){
        return nameSnakeCase;
    }
    public String getSpanishName(){
        return spanishName;
    }
}
