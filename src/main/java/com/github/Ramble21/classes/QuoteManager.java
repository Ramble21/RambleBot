package com.github.Ramble21.classes;

import java.util.ArrayList;
import java.util.List;

public class QuoteManager {
    private static List<Quote> objects = new ArrayList<>();

    public static void addObject(Quote obj) {
        objects.add(obj);
    }
    public static void deleteObject(Quote obj) {
        objects.remove(obj);
    }
    public static Quote findById(int id) {

        for (Quote obj : objects) {
            if (obj.getId() == id){
                return obj;
            }
        }
        return null;
    }
}

