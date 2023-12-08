package com.play;


import com.play.day1.DayOne;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class DayOneTest {

    @Test
    void testDayOne() throws IOException {
        long res = DayOne.getAlphaNumericCalibrationSum();
        System.out.println(res);
    }
}
