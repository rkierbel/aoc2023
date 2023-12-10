package com.play.day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public class DayFive {

    private static final Pattern MAP_LINE = Pattern.compile("(\\d{1,3} ){3}");
    private static int minLocationBySeeds() throws IOException {
        List<String> almanac = Files.readAllLines(Paths.get("src/main/resources/shortDayFive.txt"));

    }
}
