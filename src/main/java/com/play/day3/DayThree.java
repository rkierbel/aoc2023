package com.play.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DayThree {

    private static final Pattern numRgx = Pattern.compile("\\d{1,3}");
    private static final Pattern symRgx = Pattern.compile("[*@=+\\-/%&#$]");

    public static int parseSchematics() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/shortDay3pt2.txt"));
        int maxLine = lines.size();
        return parseSchematicsV2(lines, maxLine);

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


    //TODO -> not iterating properly through the whole line for gear matching
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

    /**
     * def calculation method (product) <- gear index on currentLine, search zone
     * calculation method ->
     *  needs two matches for num pattern in search zone
     *      from gear, look for num patterns on same line ; extract to int ; incr product operands to 1
     *      look above, repeat
     *      look below, repeat
     *      product operands < 2 ret 0L else return product
     *  return sum of products for line
     */
    private static int findGearRatio(String currentLine, String... surroundings) {
        Matcher gearMatch = Pattern.compile("\\*").matcher(currentLine);
        int total = 0;
        int startIndex = 0;

        while (gearMatch.find()) {
            String currentGear = gearMatch.group();
            startIndex = currentLine.indexOf(currentGear, startIndex);
            int[] searchZone = defineLargeSearchZone(currentLine, startIndex);
            total += extractGearOperands(searchZone, currentLine, surroundings);
            startIndex = currentLine.indexOf(currentGear) + 1;
        }
        return total;
    }

    private static int extractGearOperands(int[] searchZone, String currentLine, String... surroundings) {
        List<String> lines = new ArrayList<>(Arrays.asList(surroundings));
        lines.add(currentLine);
        int gearIndex = currentLine.indexOf("*", searchZone[2]);
        int totalOperands = 0;
        int totalForGear = 1;
        for (var l : lines) {
            String zone = l.substring(searchZone[0], searchZone[1]);
            int totalForZone = calculateForZone(gearIndex, zone);
            if (totalForZone > 0) {
                totalForGear *= totalForZone;
                totalOperands++;
            }
            if (totalOperands == 2) break;
        }
        return totalForGear == 1 || totalOperands != 2 ? 0 : totalForGear;
    }

    private static int calculateForZone(int gearIndex, String zone) {
        int totalForZone = 0;
        Matcher zoneMatcher = numRgx.matcher(zone);
        while (zoneMatcher.find()) {
            String matchedNum = zoneMatcher.group();
            int lastIndexOfNum = zone.indexOf(matchedNum) + matchedNum.length() - 1;
            int firstIndexOfNum = zone.indexOf(matchedNum);
            if (lastIndexOfNum == gearIndex || lastIndexOfNum == gearIndex-1) {
                totalForZone += Integer.parseInt(matchedNum);
            } else if (firstIndexOfNum == gearIndex || lastIndexOfNum == gearIndex+1) {
                totalForZone += Integer.parseInt(matchedNum);
            }
        }
        return totalForZone;
    }

    private static int[] defineGearSubzone(int zoneCenter, String largerZone) {
        boolean isGearOnZone = largerZone.indexOf("*") == zoneCenter;
        int indexOfZoneCenter = largerZone.indexOf(largerZone.charAt(zoneCenter));
        int startZone = Math.max(0, isGearOnZone ? indexOfZoneCenter - 1 : indexOfZoneCenter);
        int endZone = isGearOnZone ? indexOfZoneCenter + 1 : indexOfZoneCenter;

        return new int[]{startZone, endZone};
    }

    private static int[] defineLargeSearchZone(String currentLine, int startIndex) {
        int searchZoneStart = Math.max(0, currentLine.indexOf("*", startIndex) - 3);
        int searchZoneEnd = Math.min(
                currentLine.indexOf("*", startIndex) + 4,
                currentLine.length());

        return new int[]{searchZoneStart, searchZoneEnd, startIndex};
    }

    private static int[] defineSearchZone(String currentLine, int startIndex, String zoneCenter) {
        int searchZoneStart = Math.max(0, currentLine.indexOf(zoneCenter, startIndex) - 1);
        int searchZoneEnd = Math.min(
                currentLine.indexOf(zoneCenter, startIndex) + zoneCenter.length() + 1,
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
