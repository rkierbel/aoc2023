package com.play.day4;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class DayFour {

    public static int parsePileOfCards() throws IOException {
        return Files.lines(Paths.get("src/main/resources/shortDayFour.txt"))
                .mapToInt(line -> extractPointsPerCard(line.substring(line.indexOf(": "))))
                .map(ptsPerCard -> {
                   if (ptsPerCard > 1) {
                       return (int) Math.pow(2, ptsPerCard - 1);
                   }
                   return ptsPerCard;
                })
                .sum();
    }

    private static int extractPointsPerCard(String card) {
        String[] cardParts = card.split("\\|");

        return Arrays.stream(cardParts[0].split(" "))
                .mapToInt(num -> {
                    return Arrays.stream(cardParts[1].split(" "))
                            .mapToInt(candidate -> {
                                if (candidate.trim().equals(num.trim()) &&
                                        Pattern.compile("\\d{1,3}").matcher(candidate).matches()) {
                                    return 1;
                                }
                                return 0;
                            })
                            .sum();
                })
                .sum();
    }
}
