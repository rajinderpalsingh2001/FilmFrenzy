package com.nagarro.filmfrenzy;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        String filePath = "src/com/nagarro/filmfrenzy/resources/movies.txt";
        List<String> movies = new ArrayList<>();
        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                movies.add(line.toUpperCase());
            }
            System.out.println(movies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

