package com.github.Ramble21.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class VocabWord {

    public static HashMap<String, String> hashMap = new HashMap<>();
    public static ArrayList<String> mapKeys = new ArrayList<>();

    public static HashMap<String, String> frenchHashMap = new HashMap<>();
    public static ArrayList<String> frenchMapKeys = new ArrayList<>();

    public String word;
    public String[] englishTranslations;

    public VocabWord(String flagName){
        if (hashMap.isEmpty()){
            initializeVocabHashMap();
        }
        if (frenchHashMap.isEmpty()){
            initializeFrenchVocabHashMap();
        }

        if (flagName.equals("spanish.png")){
            int line = (int)(Math.random()*hashMap.size());
            word = mapKeys.get(line);
        }
        else{
            int line = (int)(Math.random()*frenchHashMap.size());
            word = frenchMapKeys.get(line);
        }

        englishTranslations = generateEnglishTranslations(word, flagName);
        System.out.println("Word: " + word);
        System.out.println("English Translations: " + Arrays.toString(englishTranslations));
    }
    public VocabWord(String key, String flagName){
        if (hashMap.isEmpty() ) {
            initializeVocabHashMap();
        }
        word = key;
        englishTranslations = generateEnglishTranslations(word, flagName);

        System.out.println("Word: " + word);
        System.out.println("English Translations: " + Arrays.toString(englishTranslations));
    }

    public static void initializeVocabHashMap(){
        Gson gson = new Gson();
        InputStream inputStream = VocabWord.class.getResourceAsStream("/com/github/Ramble21/vocab/verbs.json");

        assert inputStream != null;
        InputStreamReader reader = new InputStreamReader(inputStream);
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        hashMap = gson.fromJson(reader, type);
        mapKeys.addAll(hashMap.keySet());
    }
    public static void initializeFrenchVocabHashMap(){
        Gson gson = new Gson();
        InputStream inputStream = VocabWord.class.getResourceAsStream("/com/github/Ramble21/vocab/frenchverbs.json");

        assert inputStream != null;
        InputStreamReader reader = new InputStreamReader(inputStream);
        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
        frenchHashMap = gson.fromJson(reader, type);
        frenchMapKeys.addAll(frenchHashMap.keySet());
    }
    public static String[] generateEnglishTranslations(String key, String flagName){
        String value;
        if (flagName.equals("spanish.png")){
            value = hashMap.get(key);
        }
        else{
            value = frenchHashMap.get(key);
        }
        int firstCommaIndex = -1;
        for (int i = 0; i < value.length()-1; i++){
            if ((value.charAt(i) == ',') && firstCommaIndex == -1){
                String[] valueArray = new String[2];
                valueArray[0] = value.substring(0, i);
                valueArray[1] = ("to" + value.substring(i+1));
                firstCommaIndex = i;
                if (!valueArray[1].contains(",")){
                    return valueArray;
                }
            }
            else if ((value.charAt(i) == ',') && firstCommaIndex != 1){
                String[] valueArray = new String[3];
                valueArray[0] = value.substring(0, firstCommaIndex);
                valueArray[1] = ("to" + value.substring(firstCommaIndex+1, i));
                valueArray[2] = ("to" + value.substring(i+1));
                return valueArray;
            }
        }
        return new String[]{value};
    }

    public String getVocabWord(){
        return word;
    }
    public String[] getEnglishTranslations(){
        return englishTranslations;
    }
}
