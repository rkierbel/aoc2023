package com.play.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class DayTwo {

    public static long sumIdsOfPossibleGames() throws IOException {
        return Files
                .lines(Paths.get("src/main/resources/dayTwo.txt"))
                .mapToLong(line -> extractIdIfPossibleGame(line) ?
                        Long.parseLong(line.substring(5, 8).replaceAll("\\D", "").trim()) : 0)
                .sum();
    }

    public static long sumCubeOfMinPossibleGame() throws IOException {
        return Files.lines(Paths.get("src/main/resources/dayTwo.txt"))
                .mapToLong(DayTwo::extractProductOfMaxPerColor)
                .sum();
    }

    private static boolean extractIdIfPossibleGame(String game) {
        HashMap<String, Long> maxToColor = new HashMap<>();
        maxToColor.put("blue", 0L);
        maxToColor.put("green", 0L);
        maxToColor.put("red", 0L);

        Arrays.stream(gameStringToArray(game))
                .map(a -> a.split(","))
                .forEach(subset -> Arrays.stream(subset).forEach(color -> {
                    String key = extractColor(color);
                    Long value = extractNumber(color);
                    if (value > maxToColor.get(key))
                        maxToColor.put(key, value);
                }));

        return maxToColor.get("blue") <= 14 && maxToColor.get("green") <= 13 && maxToColor.get("red") <= 12;
    }

    private static long extractProductOfMaxPerColor(String game) {
        List<String> colors = List.of("blue", "green", "red");

        return colors.stream()
                .map(color -> Arrays.stream(gameStringToArray(game))
                        .mapToLong(subset -> extractMaxPerColor(subset, color))
                        .max()
                        .orElse(1L))
                .reduce((next, total) -> next * total).orElse(0L);
    }

    private static String[] gameStringToArray(String game) {
        return game.substring(game.indexOf(":") + 1).trim().split(";");
    }

    private static long extractMaxPerColor(String subset, String color) {
        return Arrays.stream(subset.split(","))
                .filter(s -> s.contains(color))
                .mapToLong(DayTwo::extractNumber)
                .findFirst()
                .orElse(0L);
    }

    private static String extractColor(String color) {
        return Pattern.compile("[^a-zA-Z)]").matcher(color).replaceAll("");
    }

    private static long extractNumber(String color) {
        return Long.parseLong(Pattern.compile("\\D").matcher(color).replaceAll(""));
    }
}
