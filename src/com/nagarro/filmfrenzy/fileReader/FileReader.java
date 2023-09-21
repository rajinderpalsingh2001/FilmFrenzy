package com.nagarro.filmfrenzy.fileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    private String filePath = "src/com/nagarro/filmfrenzy/resources/movies.txt";

    public List<String> readMovies() throws IOException{
        List<String> movies = new ArrayList<>();
        try (java.io.FileReader fileReader = new java.io.FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                movies.add(line.toUpperCase());
            }
            return movies;
        } catch (IOException e) {
            throw e ;
        }
    }
}
