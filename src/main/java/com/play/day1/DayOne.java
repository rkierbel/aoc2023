package com.play.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DayOne {

    public static long getCalibrationSum() throws IOException {
        return Files
                .lines(Paths.get("src/main/resources/dayOne.txt"))
                .mapToLong(DayOne::extractAsLong).sum();
    }

    public static long getAlphaNumericCalibrationSum() throws IOException {
        return Files
                .lines(Paths.get("src/main/resources/dayOne.txt"))
                .mapToLong(DayOne::extractResultFromAlphaNumeric).sum();
    }

    private static long extractResultFromAlphaNumeric(String literal) {
        String formatted = NumericPattern.patterns().stream().reduce(literal, NumericPattern::matchAndReplace);

        return extractAsLong(formatted);
    }


    private static long extractAsLong(String literal) {
        String numeric = literal.replaceAll("\\D", "");

        return numeric.length() == 1 ?
                Long.parseLong(numeric + numeric) :
                Long.parseLong(numeric.charAt(0) + numeric.substring(numeric.length() - 1));
    }
}
