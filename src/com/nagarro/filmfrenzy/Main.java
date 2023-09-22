package com.nagarro.filmfrenzy;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import static java.util.Collections.min;
public class Main {
    private static String filePath = "src/com/nagarro/filmfrenzy/resources/movies.txt";
    private static Random random = new Random();
    public static Map<String, String> colors = new HashMap<>() {{
        put("reset", "\u001B[0m");
        put("red", "\u001B[31m");
        put("yellow", "\u001B[33m");
        put("cyan", "\u001B[36m");
        put("green", "\u001B[32m");
        put("blue","\u001B[34m");
    }};
    public static int randomIndex(int len) {
        return (int) (Math.random() * ((len - 1) + 1));
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
    public static Map<Integer, Character> replaceAndMap(String movieName, Movie mov) {
        StringBuilder maskedName = new StringBuilder();
        Map<Integer, Character> replacementMap = new HashMap<>();
        for (int i = 0; i < movieName.length(); i++) {
            char currentChar = movieName.charAt(i);
            if (Character.isLetter(currentChar)) {
                if (random.nextBoolean()) {
                    maskedName.append('_');
                    replacementMap.put(i, currentChar);
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
    public static void fillBlank(int index, Character character, Movie mov) {
        mov.movieNameWithBlanks = mov.movieNameWithBlanks.substring(0, index) + character.toString().toUpperCase() + mov.movieNameWithBlanks.substring(index + 1);
    }
    private static char getRandomChar() {
        return (char) ('A' + random.nextInt('Z' - 'A' + 1));
    }
    private static void shuffleList(List<Character> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }
    public static List<Character> showHint(Character actualChar) {
        List<Character> charList = new ArrayList<>();
        charList.add(actualChar);
        for (int i = 0; i < 2; i++) {
            char randomChar;
            do {
                randomChar = getRandomChar();
            } while (randomChar == actualChar || charList.contains(randomChar));
            charList.add(randomChar);
        }
        shuffleList(charList);
        return charList;
    }
    public static void main(String[] args) {
        Player player1 = new Player(), player2 = new Player();
        GameLifes gameLifes = new GameLifes();
        boolean isPlayer1Turn = true;
        int currentBlankIndex;
        Scanner sc = new Scanner(System.in);
        try {
            List<String> movies = readMovies();
            System.out.print(colors.get("yellow") + "Enter Player 1 name: ");
            player1.name = sc.next();
            System.out.print("Enter Player 2 name: " + colors.get("reset"));
            player2.name = sc.next();
            System.out.println("WELCOME " + player1.name + " and " + player2.name);
            System.out.println(colors.get("blue") + "=============================");
            System.out.println("LET'S START THE GAME!!!");
            System.out.println("=============================" + colors.get("reset"));
            Movie movie = new Movie();
            movie.movieName = movies.get(randomIndex(movies.size()));
            Map<Integer, Character> movieBlankData = replaceAndMap(movie.movieName, movie);
            currentBlankIndex = min(movieBlankData.keySet());
            do {
                System.out.println(colors.get("yellow") + "Player " + (isPlayer1Turn ? "1" : "2") + " turn" + colors.get("reset"));
                System.out.println(colors.get("cyan") + "-----------------");
                System.out.println("Chances left: " + gameLifes.chances);
                System.out.println("Lifes left: " + gameLifes.lifes + colors.get("reset"));
                System.out.println("Guess the movie: " + movie.movieNameWithBlanks);
                System.out.println(colors.get("cyan") + "-----------------");
                System.out.println("Type 'life' to use Life Line" + colors.get("reset"));
                System.out.print(colors.get("yellow") + "Enter a Character for first _ blank: " + colors.get("reset"));
                String guessedChar = sc.next();
                if (guessedChar.equalsIgnoreCase("life")) {
                    if (gameLifes.lifes < 0) {
                        System.out.println(colors.get("red") + "Sorry, No Life Available" + colors.get("reset"));
                    } else {
                        String optionsAvailable = "";
                        List<String> availableOptions = gameLifes.optionWithLife.keySet().stream().toList();
                        for (int i = 0; i < availableOptions.size(); i++) {
                            if (!gameLifes.usedOptions.contains((Character.getNumericValue(availableOptions.get(i).charAt(0))))) {
                                System.out.println(availableOptions.get(i));
                                optionsAvailable += availableOptions.get(i).charAt(0) + ", ";
                            }
                        }
                        if (optionsAvailable.length() > 0) {
                            optionsAvailable = optionsAvailable.substring(0, optionsAvailable.length() - 2);
                            if (optionsAvailable.length() != 1) {
                                optionsAvailable = optionsAvailable.substring(0, optionsAvailable.length() - 2) + " or" + optionsAvailable.substring(optionsAvailable.length() - 2);
                            }
                        }
                        optionsAvailable += " [-1 to SKIP]: ";
                        System.out.print(colors.get("yellow") + "Enter: " + optionsAvailable + colors.get("reset"));
                        int option = sc.nextInt();
                        if (gameLifes.usedOptions.contains(option)) {
                            System.out.println(colors.get("red") + "Can't use this option, life already taken" + colors.get("reset"));
                        } else {
                            switch (option) {
                                case 1:
                                    ChatGPTAPI chatGPTAPI = new ChatGPTAPI();
                                    System.out.println("Kindly wait we are fetching Description");
                                    chatGPTAPI.callChatGPT(movie.movieName)
                                            .thenAccept(response -> {
                                                int contentIndex = response.indexOf("\"content\":");
                                                if (contentIndex != -1) {
                                                    int startIndex = contentIndex + "\"content\":".length() + 2;
                                                    response = response.substring(startIndex);
                                                    int endIndex = response.indexOf("\"\n");
                                                    String content = response.substring(0, endIndex);
                                                    System.out.println(colors.get("green") + "================================");
                                                    System.out.println("Movie Description: ");
                                                    System.out.println(content.replaceAll(movie.movieName, "_"));
                                                    System.out.println("================================" + colors.get("reset"));
                                                    gameLifes.lifes -= 2;
                                                } else {
                                                    System.out.println(colors.get("red") + "Unable to find Movie Description" + colors.get("reset"));
                                                }
                                            })
                                            .join();
                                    break;
                                case 2:
                                    System.out.println(colors.get("cyan") + "One Blank Filled [1 life used]" + colors.get("reset"));
                                    fillBlank(currentBlankIndex, movieBlankData.get(currentBlankIndex), movie);
                                    movieBlankData.remove(currentBlankIndex);
                                    currentBlankIndex = movieBlankData.isEmpty() ? -1 : min(movieBlankData.keySet());
                                    if (isPlayer1Turn) {
                                        player1.score++;
                                    } else {
                                        player2.score++;
                                    }
                                    isPlayer1Turn = !isPlayer1Turn;
                                    gameLifes.lifes--;
                                    break;
                                case 3:
                                    List<Character> hints = showHint(movieBlankData.get(currentBlankIndex));
                                    System.out.println(colors.get("green") + "Hints: " + hints + colors.get("reset"));
                                    gameLifes.lifes--;
                                    break;
                                case -1:
                                    continue;
                                default:
                                    System.out.println(colors.get("red") + "Not a valid option" + colors.get("reset"));
                                    break;
                            }
                            gameLifes.usedOptions.add(option);
                        }
                    }
                } else if (guessedChar.length() == 1) {
                    if (movieBlankData.get(currentBlankIndex).toString().equalsIgnoreCase(guessedChar)) {
                        fillBlank(currentBlankIndex, movieBlankData.get(currentBlankIndex), movie);
                        System.out.println(colors.get("green") + "RIGHT ANSWER!!!");
                        if (isPlayer1Turn) {
                            player1.score++;
                            System.out.println("SCORE: " + player1.score + colors.get("reset"));
                        } else {
                            player2.score++;
                            System.out.println("SCORE: " + player2.score + colors.get("reset"));
                        }
                        System.out.println(colors.get("cyan")+"---------------------" + colors.get("reset"));
                        movieBlankData.remove(currentBlankIndex);
                        currentBlankIndex = movieBlankData.isEmpty() ? -1 : min(movieBlankData.keySet());
                    } else {
                        System.out.println(colors.get("red") + "OOPS!!! WRONG ANSWER");
                        gameLifes.chances--;
                        System.out.println("CHANCES LEFT: " + gameLifes.chances + colors.get("reset"));
                    }
                    isPlayer1Turn = !isPlayer1Turn;
                } else {
                    System.out.println(colors.get("red") + "Not a valid Input" + colors.get("reset"));
                }
            } while (gameLifes.chances > 0 && !movieBlankData.isEmpty());
            System.out.println("Movie: " + movie.movieName );
        } catch (Exception e) {
            System.out.println(colors.get("red") + "Unexpected Error Occurred, Restart the game" + colors.get("reset"));
        }
        System.out.println(colors.get("blue") + "======== GAME OVER!! ========");
        System.out.println("Player 1 score: " + player1.score);
        System.out.println("Player 2 score: " + player2.score);
        System.out.println("=============================" + colors.get("reset"));
    }
}
class Movie {
    public String movieName;
    public String movieNameWithBlanks;
}
class GameLifes {
    public int chances = 10;
    public int lifes = 3;
    public Map<String, Integer> optionWithLife = new LinkedHashMap<>() {{
        put("1. See Description of the Movie", 2);
        put("2. Fill one Blank", 1);
        put("3. Give Hint for recent blank", 1);
    }};
    public List<Integer> usedOptions = new ArrayList<>();
}
class Player {
    public String name;
    public int score = 0;
}
class ChatGPTAPI {
    private final String API_KEY = "";
    private final HttpClient httpClient;
    public ChatGPTAPI() {
        this.httpClient = HttpClient.newHttpClient();
    }
    public CompletableFuture<String> callChatGPT(String movieName) {
        String requestBody = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"system\",\"content\":\"Provide a summary of " + movieName + " movie. But be make sure not to add the movie name in the description, as I am playing a quiz and summary should be short\"}]}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }
}