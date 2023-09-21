package com.nagarro.filmfrenzy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.min;

public class Main {
    private static String filePath = "src/com/nagarro/filmfrenzy/resources/movies.txt";

    public static int randomIndex(int len) {
        return (int) (Math.random() * ((len) + 1));
    }

    public static List<String> readMovies() throws IOException {
        List<String> movies = new ArrayList<>();
        try (java.io.FileReader fileReader = new java.io.FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                movies.add(line.toUpperCase());
            }
            return movies;
        } catch (IOException e) {
            throw e;
        }
    }

    public static Map<Integer, String> replaceAndMap(String movieName, Movie mov) {
        Random random = new Random();
        StringBuilder maskedName = new StringBuilder();
        Map<Integer, String> replacementMap = new HashMap<>();

        for (int i = 0; i < movieName.length(); i++) {
            char currentChar = movieName.charAt(i);

            if (Character.isLetter(currentChar)) {
                if (random.nextBoolean()) {
                    maskedName.append('_');
                    replacementMap.put(i, String.valueOf(currentChar));
                } else {
                    maskedName.append(currentChar);
                }
            } else {
                maskedName.append(currentChar);
            }
        }
        mov.movieNameWithBlanks = maskedName.toString();
        return replacementMap;
    }

    public static void main(String[] args) {
        Player player1 = new Player(), player2 = new Player();
        GameLifes gameLifes = new GameLifes();

        boolean isPlayer1Turn = true;
        int currentBlankIndex;

        Scanner sc = new Scanner(System.in);
        try {
            List<String> movies = readMovies();
            System.out.print("Enter Player 1 name: ");
            player1.name = sc.next();

            System.out.print("Enter Player 2 name: ");
            player2.name = sc.next();

            System.out.println("Let's start the game " + player1.name + " and " + player2.name);
            Movie movie = new Movie();
            movie.movieName = movies.get(randomIndex(movies.size()));
            Map<Integer, String> movieBlankData = replaceAndMap(movie.movieName, movie);
            currentBlankIndex = min(movieBlankData.keySet());
            do {
                System.out.println("Player " + (isPlayer1Turn ? "1" : "2") + " turn");
                System.out.println("-----------------");
                System.out.println(movieBlankData);
                System.out.println(currentBlankIndex);
                System.out.println("Guess the movie: " + movie.movieNameWithBlanks);
                System.out.print("Enter a Character for first Blank: ");
                String guessedChar = sc.next();
                if (movieBlankData.get(currentBlankIndex).equalsIgnoreCase(guessedChar)) {
                    movie.movieNameWithBlanks = movie.movieNameWithBlanks.substring(0, currentBlankIndex) + guessedChar.toUpperCase()
                            + movie.movieNameWithBlanks.substring(currentBlankIndex + 1);
                    if (isPlayer1Turn) {
                        player1.score++;
                    } else {
                        player2.score++;
                    }
                    movieBlankData.remove(currentBlankIndex);
                    currentBlankIndex = movieBlankData.isEmpty() ? -1 : min(movieBlankData.keySet());
                } else {
                    gameLifes.chances--;
                }
                isPlayer1Turn = !isPlayer1Turn;
            } while (gameLifes.chances > 0 && !movieBlankData.isEmpty());
            System.out.println("Movie: " + movie.movieName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Unexpected Error Occured, Restart the game");
        }
        System.out.println("---------------------");
        System.out.println("Game over");
        System.out.println("---------------------");
        System.out.println("Player 1 score: " + player1.score);
        System.out.println("Player 2 score: " + player2.score);
        System.out.println("---------------------");
    }
}

class Movie {
    public String movieName;
    public String movieNameWithBlanks;
}

class GameLifes {
    public int chances = 10;
    public int lifes = 3;
}

class Player {
    public String name;
    public int score = 0;
}