package com.github.Ramble21.classes;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.dv8tion.jda.api.entities.User;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VocabWord {

    public static HashMap<String, String> hashMap = new HashMap<>();
    public static ArrayList<String> mapKeys = new ArrayList<>();

    public static HashMap<String, String> frenchHashMap = new HashMap<>();
    public static ArrayList<String> frenchMapKeys = new ArrayList<>();

    public String word;
    public String[] englishTranslations;
    public int masteryLevel = 0;

    public VocabWord(String flagName, User user, boolean isFromPersonal){
        if (hashMap.isEmpty()){
            initializeVocabHashMap();
        }
        if (frenchHashMap.isEmpty()){
            initializeFrenchVocabHashMap();
        }
        if (!isFromPersonal){
            if (flagName.equals("spanish.png")){
                int line = (int)(Math.random()*hashMap.size());
                word = mapKeys.get(line);
            }
            else{
                int line = (int)(Math.random()*frenchHashMap.size());
                word = frenchMapKeys.get(line);
            }
        }
        else{
            if (flagName.equals("spanish.png")){
                ArrayList<VocabWord> personal = getPersonalJsonList(user, "spanish");
                ArrayList<String> possibleVocabWords = new ArrayList<>();

                assert personal != null;
                for (VocabWord vocabWord : personal){
                    possibleVocabWords.add(vocabWord.getVocabWord());
                }

                int line = (int)(Math.random()* possibleVocabWords.size());
                word = possibleVocabWords.get(line);
            }
            else{
                ArrayList<VocabWord> personal = getPersonalJsonList(user, "french");
                ArrayList<String> possibleVocabWords = new ArrayList<>();

                assert personal != null;
                for (VocabWord vocabWord : personal){
                    possibleVocabWords.add(vocabWord.getVocabWord());
                }

                int line = (int)(Math.random()* possibleVocabWords.size());
                word = possibleVocabWords.get(line);
            }
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
        String value = generateContentRaw(key, flagName);

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
    public static String generateContentRaw(String key, String flagName){
        String value;
        if (flagName.equals("spanish.png")){
            value = hashMap.get(key);
        }
        else{
            value = frenchHashMap.get(key);
        }
        return value;
    }
    public String getVocabWord(){
        return word;
    }
    public String[] getEnglishTranslations(){
        return englishTranslations;
    }
    public int getMasteryLevel() {
        return masteryLevel;
    }

    public void writeToPersonalJson(User user, String language){

        try {
            for (String pathStr : new String[]{
                    "data",
                    "data/json",
                    "data/json/personalvocab",
                    "data/json/personalvocab/" + language
            }) {
                Path path = Paths.get(pathStr);
                if (!Files.exists(path)) Files.createDirectory(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String userId = user.getId();

        List<VocabWord> vocabWordList;

        try (FileReader reader = new FileReader("data/json/personalvocab/" + language + "/" + user.getId() + ".json")) {
            Type listType = new TypeToken<ArrayList<VocabWord>>() {}.getType();
            vocabWordList = gson.fromJson(reader, listType);

            if (vocabWordList == null) {
                vocabWordList = new ArrayList<>();
            }

        } catch (IOException e) {
            vocabWordList = new ArrayList<>();
        }

        vocabWordList.add(this);

        try (FileWriter writer = new FileWriter("data/json/personalvocab/" + language + "/" + user.getId() + ".json")){
            gson.toJson(vocabWordList,writer);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<VocabWord> getPersonalJsonList(User user, String language){
        Gson gson = new Gson();
        String personalJson = "data/json/personalvocab/" + language + "/" + user.getId() + ".json";
        Type type = new TypeToken<ArrayList<VocabWord>>() {}.getType();

        try (FileReader reader = new FileReader(personalJson)) {
            return gson.fromJson(reader, type);
        }
        catch (IOException e) {
            return null;
        }
    }
    public String toString(){
        return "VocabWord{" + "vocabWord='" + word + "\\'" + ", englishTranslations=" + Arrays.toString(englishTranslations) + "}";
    }
}
