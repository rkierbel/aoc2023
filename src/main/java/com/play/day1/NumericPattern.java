package com.play.day1;

import java.util.Arrays;
import java.util.List;

public enum NumericPattern {
    oneight(18),
    twone(21),
    eightwo(82),
    one(1),
    two(2),
    three(3),
    four(4),
    five(5),
    six(6),
    seven(7),
    eight(8),
    nine(9);

    private final long numValue;

    NumericPattern(long numValue) {
        this.numValue = numValue;
    }


    static List<String> patterns() {
        return Arrays.stream(values())
                .map(String::valueOf)
                .toList();
    }

    static String matchAndReplace(String input, String pattern) {
        return input.replaceAll(pattern, "" + NumericPattern.valueOf(pattern).numValue);
    }
}
