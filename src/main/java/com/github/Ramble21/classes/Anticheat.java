package com.github.Ramble21.classes;

public class Anticheat {
    public static String addAnticheatToSentence (String sentence){
        String newSentence = sentence;
        int max = sentence.length()-1;
        for (int i = 1; i <= 25; i++){

            int index = (int)(Math.random()*max);
            int typeOfZwsp = (int)(Math.random()*4);
            String zwsp = "";
            switch (typeOfZwsp){
                case(0): zwsp = "\u200b";
                case(1): zwsp = "\u200D";
                case(2): zwsp = "\u200C";
                case(3): zwsp = "\uFEFF";
            }
            newSentence = newSentence.substring(0, index) + zwsp + newSentence.substring(index);
            max++;
        }

        newSentence = Anticheat.replaceChars(newSentence);
        return newSentence;

    }
    public static boolean isCheated(String string){
        String[] bads = {"\u200b", "\u200D", "\u200C", "\uFEFF", "\u0430", "\u0441", "\u0501", "\u0435", "\u04bb", "\u0458", "\u04cf", "\u0578", "\u0440", "\u0445", "\u0443", "\u043e", "\u03bf"};
        for (String bad : bads) {
            if (string.contains(bad)) {
                return true;
            }
        }
        return false;
    }
    private static String replaceChars(String string){
        String newString = "";
        for (int i = 0; i < string.length(); i++){
            char c = string.charAt(i);
            String replacementChar = "";
            switch(c){
                case('a'): replacementChar = "\u0430";
                case('c'): replacementChar = "\u0441";
                case('d'): replacementChar = "\u0501";
                case('e'): replacementChar = "\u0435";
                case('h'): replacementChar = "\u04bb";
                case('j'): replacementChar = "\u0458";
                case('l'): replacementChar = "\u04cf";
                case('n'): replacementChar = "\u0578";
                case('p'): replacementChar = "\u0440";
                case('x'): replacementChar = "\u0445";
                case('y'): replacementChar = "\u0443";
                case('o'):
                    int rand = (int)(Math.random()*2);
                    if (rand == 0){
                        replacementChar = "\u043e";
                    }
                    else{
                        replacementChar = "\u03bf";
                    }
                default:
                    replacementChar = "" + c;
            }
            newString += replacementChar;
        }
        return newString;
    }
}
