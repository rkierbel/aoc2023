package com.play.day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Day3 {
    private static final Pattern isSymbol = Pattern.compile("[@#$%^&*\\-=+/]");


    private int findNumber(String[][] grid, int x, int y) {
        // go all the way left until we hit a ., x=0, or a symbol knowing we are past the left end of the number
        while (x >= 0 && grid[y][x].charAt(0) != '.' && !isSymbol.matcher(grid[y][x]).matches()) {
            x--;
        }

        // go back to the first digit of the number
        x++;

        StringBuilder number = new StringBuilder();

        // start moving right to read the entire number, stopping at a . or if we hit the right end of the grid
        while (x < grid[y].length && grid[y][x].charAt(0) != '.' && !isSymbol.matcher(grid[y][x]).matches()) {
            number.append(grid[y][x]);
            x++;
        }

        return Integer.parseInt(number.toString());
    }

    private Set<Integer> findNumbersAdjacentTo(String[][] grid, Symbol symbol) {
        int[][] directions = symbol.getAdjacentPositions();

        Set<Integer> numbers = new HashSet<>();

        for (int[] direction : directions) {
            int x = direction[0];
            int y = direction[1];

            String atPosition = grid[y][x];
            if (Character.isDigit(atPosition.charAt(0))) {
                numbers.add(findNumber(grid, x, y));
            }
        }

        return numbers;
    }

    private String[][] getGrid() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/dayThree.txt")).stream()
                .filter(line -> !line.isBlank()).toList();

        String[][] grid = new String[140][140];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                grid[i][j] = line.substring(j, j + 1);
            }
        }

        return grid;
    }

    private List<Symbol> getSymbols(String[][] grid) {
        List<Symbol> symbols = new ArrayList<>();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                String current = grid[y][x];
                if (isSymbol.matcher(current).matches()) {
                    symbols.add(new Symbol(current, x, y));
                }
            }
        }

        return symbols;
    }

    public String partOne() throws IOException {
        String[][] grid = getGrid();
        List<Symbol> symbols = getSymbols(grid);

        return String.valueOf(symbols.stream()
                .flatMap(symbol -> findNumbersAdjacentTo(grid, symbol).stream())
                .reduce(0, Integer::sum));
    }

    public String partTwo() throws IOException {
        String[][] grid = getGrid();
        List<Symbol> gears = getSymbols(grid).stream().filter(symbol -> symbol.symbol.equals("*")).toList();

        return String.valueOf(gears.stream()
                .map(symbol -> findNumbersAdjacentTo(grid, symbol))
                .filter(set -> set.size() == 2)
                .map(set -> {
                    List<Integer> nums = set.stream().toList();
                    int res =  nums.get(0) * nums.get(1);
                    System.out.println("for gear -> " + res);
                    return res;
                })
                .reduce(0, Integer::sum));
    }

    record Symbol(String symbol, int x, int y) {
        int[][] getAdjacentPositions() {
            return new int[][] {
                    {x - 1, y - 1},
                    {x, y - 1},
                    {x + 1, y - 1},
                    {x - 1, y},
                    {x + 1, y},
                    {x - 1, y + 1},
                    {x, y + 1},
                    {x + 1, y + 1}
            };
        }
    }
}