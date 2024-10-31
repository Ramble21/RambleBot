package com.github.Ramble21.classes;

public class State {
    private final String name;
    private final String nameSnakeCase;
    private final String capital;

    public State(){

        String[] states = {
                "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
                "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
                "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
                "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi",
                "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
                "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota",
                "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
                "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah",
                "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming"
        };

        String[] statesSnakeCase = {
                "alabama", "alaska", "arizona", "arkansas", "california", "colorado",
                "connecticut", "delaware", "florida", "georgia", "hawaii", "idaho",
                "illinois", "indiana", "iowa", "kansas", "kentucky", "louisiana",
                "maine", "maryland", "massachusetts", "michigan", "minnesota", "mississippi",
                "missouri", "montana", "nebraska", "nevada", "new_hampshire",
                "new_jersey", "new_mexico", "new_york", "north_carolina", "north_dakota",
                "ohio", "oklahoma", "oregon", "pennsylvania", "rhode_island",
                "south_carolina", "south_dakota", "tennessee", "texas", "utah",
                "vermont", "virginia", "washington", "west_virginia", "wisconsin", "wyoming"
        };


        String[] capitals = {
                "Montgomery", "Juneau", "Phoenix", "Little Rock", "Sacramento", "Denver", "Hartford",
                "Dover", "Tallahassee", "Atlanta", "Honolulu", "Boise", "Springfield", "Indianapolis",
                "Des Moines", "Topeka", "Frankfort", "Baton Rouge", "Augusta", "Annapolis", "Boston",
                "Lansing", "Saint Paul", "Jackson", "Jefferson City", "Helena", "Lincoln", "Carson City",
                "Concord", "Trenton", "Santa Fe", "Albany", "Raleigh", "Bismarck", "Columbus",
                "Oklahoma City", "Salem", "Harrisburg", "Providence", "Columbia", "Pierre", "Nashville",
                "Austin", "Salt Lake City", "Montpelier", "Richmond", "Olympia", "Charleston", "Madison",
                "Cheyenne"
        };

        int randomNo = (int)(Math.random()*50);
        name = states[randomNo];
        nameSnakeCase = statesSnakeCase[randomNo];
        capital = capitals[randomNo];
    }

    public String getName(){
        return name;
    }

    public String getNameSnakeCase(){
        return nameSnakeCase;
    }

    public String getCapital(){
        return capital;
    }
}
