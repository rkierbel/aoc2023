package com.play.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class DayThree {

    private static final Pattern numRgx = Pattern.compile("\\d{1,3}");
    private static final Pattern symRgx = Pattern.compile("[*@=+\\-/%&#$]");

    public static int parseSchematics() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/dayThree.txt"));
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

    private static int findGearRatio(String currentLine, String... surroundings) {
        Matcher gearMatch = Pattern.compile("\\*").matcher(currentLine);
        int total = 0;
        int startIndex = 0;
        while (gearMatch.find()) {
            String currentGear = gearMatch.group();
            startIndex = currentLine.indexOf(currentGear, startIndex);
            int[] searchZone = defineLargeSearchZone(currentLine, startIndex);
            int forGear =  extractGearOperands(searchZone, currentLine, surroundings);
            if (forGear == 1140) {
                System.out.println(currentLine);
                System.out.println("for gear -> " + forGear);
            }
            total += forGear;
            startIndex += 1;
        }
        return total;
    }

    private static int extractGearOperands(int[] searchZone, String currentLine, String... surroundings) {
        List<String> lines = new ArrayList<>(Arrays.asList(surroundings));
        lines.add(currentLine);
        int totalOperands = 0;
        int totalForGear = 1;
        for (var l : lines) {
            String zone = l.substring(searchZone[0], searchZone[1]);
            int totalForLine = calculateForLine(zone);
            System.out.println(totalForLine);
            if (totalForLine > 999) {
                totalOperands += 2;
                totalForGear = totalForLine;
                break;
            }
            if (totalForLine > 0) {
                totalForGear *= totalForLine;
                totalOperands++;
            }
        }
        return totalForGear == 1 || totalOperands % 2 != 0 ? 0 : totalForGear;
    }

    private static int calculateForLine(String line) {
        int totalForZone = 0;
        int gearIndex = 3;
        int operandsOnSameLine = 0;
        Matcher zoneMatcher = numRgx.matcher(line);
        System.out.println("for zone -> " + line);
        while (zoneMatcher.find()) {
            String matchedNum = zoneMatcher.group();
            int lastIndexOfNum = line.indexOf(matchedNum) + matchedNum.length() - 1;
            int firstIndexOfNum = line.indexOf(matchedNum) ;

            // matches the last 3 as the first 3 in 380 -> gets it as an operand, resulting in returning 3 * 380 as a gear ratio
            if (lastIndexOfNum == gearIndex || lastIndexOfNum == gearIndex-1) {
                System.out.println("before for num " + matchedNum);
                if (operandsOnSameLine == 1) {
                    totalForZone *= Integer.parseInt(matchedNum);
                    break;
                }
                totalForZone += Integer.parseInt(matchedNum);
                operandsOnSameLine++;

            } else if (firstIndexOfNum == gearIndex || firstIndexOfNum == gearIndex+1) {
                System.out.println("after");
                if (operandsOnSameLine == 1) {
                    totalForZone *= Integer.parseInt(matchedNum);
                    break;
                }
                totalForZone += Integer.parseInt(matchedNum);
                operandsOnSameLine++;
            } else if (firstIndexOfNum >= gearIndex - 1 && lastIndexOfNum <= gearIndex + 1) {
                System.out.println("overlaps for num " + matchedNum);
                if (operandsOnSameLine == 1) {
                    totalForZone *= Integer.parseInt(matchedNum);
                    break;
                }
                totalForZone += Integer.parseInt(matchedNum);
                operandsOnSameLine++;
            }
        }
        return totalForZone;
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
