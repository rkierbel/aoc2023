package com.play.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class DayThree {

    private static final Pattern numRgx = Pattern.compile("\\d{1,3}");
    private static final Pattern symRgx = Pattern.compile("[*@=+\\-/%&#$]");

    public static int parseSchematics() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/dayThree.txt"));
        int maxLine = lines.size();
        return parseSchematicsV1(lines, maxLine);

    }

    public static int parseSchematicsV1(List<String> lines, int maxLine) {
        return IntStream.range(0, maxLine)
                .map(lineNumber -> isSurrounded(lineNumber, maxLine - 1) ?
                        sumForLine(
                                lines.get(lineNumber),
                                lines.get(lineNumber - 1), lines.get(lineNumber + 1)) :
                        sumForLine(
                                lines.get(lineNumber),
                                (lineNumber == 0 ? lines.get(1) : lines.get(maxLine - 2)))
                )
                .sum();
    }


    public static int parseSchematicsV2(List<String> lines, int maxLine) {
        return IntStream.range(0, maxLine)
                .map(line -> isSurrounded(line, maxLine - 1) ?
                        findGearRatio(
                                lines.get(line),
                                lines.get(line - 1), lines.get(line + 1)) :
                        findGearRatio(
                                lines.get(line),
                                (line == 0 ? lines.get(1) : lines.get(maxLine - 2)))
                )
                .sum();
    }

    public static boolean isSurrounded(Integer lineIndex, Integer maxLineIndex) {
        return lineIndex > 0 && lineIndex < maxLineIndex;
    }

    public static int sumForLine(String currentLine, String... surroundings) {
        Matcher matchInCurrentLine = numRgx.matcher(currentLine);
        int total = 0;
        int startIndex = 0;

        while (matchInCurrentLine.find()) {
            String currentNum = matchInCurrentLine.group();
            int[] searchRange = defineSearchZone(currentLine, startIndex, currentNum);

            if (hasPatternInSurroundings(
                    toSearchZone(searchRange, currentLine),
                    symRgx,
                    Arrays.stream(surroundings).map(line -> toSearchZone(searchRange, line)).toList())) {
                total += Integer.parseInt(currentNum);
            }
            startIndex = currentLine.indexOf(currentNum) + currentNum.length();
        }
        return total;
    }


    private static int findGearRatio(String currentLine, String... surroundings) {

    }

    private static int[] defineSearchZone(String currentLine, int startIndex, String currentNum) {
        int searchZoneStart = Math.max(0, currentLine.indexOf(currentNum, startIndex) - 1);
        int searchZoneEnd = Math.min(
                currentLine.indexOf(currentNum, startIndex) + currentNum.length() + 1,
                currentLine.length());

        return new int[]{searchZoneStart, searchZoneEnd};
    }

    private static String toSearchZone(int[] searchZone, String raw) {
        return raw.substring(searchZone[0], searchZone[1]);
    }

    private static boolean hasPatternInSurroundings(String currentLine,
                                                    Pattern pattern,
                                                    List<String> surroundings) {
        return pattern.matcher(currentLine).find() ||
                surroundings.stream().anyMatch(s -> pattern.matcher(s).find());
    }
}
