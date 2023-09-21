package com.nagarro.filmfrenzy;

import com.nagarro.filmfrenzy.fileReader.FileReader;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try{
            List<String> movies =  new FileReader().readMovies();
            System.out.println(movies);
        }catch (Exception e){

        }
    }
}

