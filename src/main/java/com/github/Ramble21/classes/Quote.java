package com.github.Ramble21.classes;

public class Quote {

    private final String quote;
    private final String author;
    private final int id;
    private static int totalQuotes = 0;

    public Quote(String q, String a, int i){
        quote = q;
        author = a;
        id = i;
        QuoteManager.addObject(this);
    }
    public String getQuote(){
        return quote;
    }
    public String getAuthor(){
        return author;
    }
    public int getId(){
        return id;
    }
    public static int getTotalQuotes(){
        return totalQuotes;
    }
    public static void increaseTotalQuotes(){
        totalQuotes++;
    }
    public static void decreaseTotalQuotes(){
        totalQuotes--;
    }

}
