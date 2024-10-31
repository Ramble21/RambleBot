package com.github.Ramble21.classes;

import com.github.Ramble21.RambleBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Sentence {

    private final String url = "dictionary.txt";
    private final int wordLimit = 1000;
    private final int characterCount;

    private final String[] words;
    private final Random random;

    private final String textRaw;
    private final String textZwsp;

    private final int noOfWords = 20;

    public Sentence() throws IOException {

        final var dictInputStream = RambleBot.class.getResourceAsStream(url);
        String[] allWords = new BufferedReader(new InputStreamReader(dictInputStream)).lines().toArray(String[]::new);
        words = new String[Math.min(wordLimit, allWords.length)];
        System.arraycopy(allWords, 0, words, 0, words.length);

        random = new Random();

        String[] sigma = getRandomWords(noOfWords);
        textZwsp = createSentence(sigma, true);
        textRaw = createSentence(sigma, false);
        characterCount = Sentence.generateCharacterCount(textRaw);
    }

    public String getRandomWord() {
        return words[random.nextInt(words.length)];
    }
    public String[] getRandomWords(int count) {
        String[] randomWords = new String[count];
        for (int i = 0; i < count; i++) {
            randomWords[i] = getRandomWord();
        }
        return randomWords;
    }
    public String createSentence(String[] words, boolean withZwsp){
        String gyatt = "";
        for (int i = 0; i < words.length; i++){
            gyatt += words[i];
            if (i != words.length-1){
                gyatt += " ";
            }
            if (withZwsp){
                gyatt += "\u200B"; // zwsp to prevent cheating
            }
        }
        return gyatt;
    }
    public String getTextRaw(){
        return textRaw;
    }
    public String getTextZwsp(){
        return textZwsp;
    }
    private static int generateCharacterCount(String textRaw){
        return textRaw.length();
    }
    public int getCharacterCount(){
        return characterCount;
    }
}
