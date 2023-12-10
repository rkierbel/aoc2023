package com.play;

import com.play.day3.Day3;
import com.play.day3.DayThree;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DayThreeTest {

    @Test
    void parseSchematics() throws IOException {
        //
        // System.out.println(new Day3().partTwo());
        System.out.println("--------");
        System.out.println(DayThree.parseSchematics());
    }
}