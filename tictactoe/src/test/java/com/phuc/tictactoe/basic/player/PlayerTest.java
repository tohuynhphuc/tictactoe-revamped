package com.phuc.tictactoe.basic.player;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.phuc.tictactoe.basic.board.Board;

public class PlayerTest {

    private Player computer;
    private Player human;
    private Board board;

    @BeforeEach
    public void setup() {
        computer = new Computer("Computer");
        human = new Human("Human", new Scanner(System.in));
        board = new Board();
    }

    // Helper method to create human player with mock input
    private Human createHumanWithInput(String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        return new Human("Human", scanner);
    }

    @Test
    public void shouldReturnCorrectName() {
        assertEquals("Computer", computer.getName(), "Computer name should be 'Computer'");
        assertEquals("Human", human.getName(), "Human name should be 'Human'");
    }

    @Test
    public void shouldSelectFirstAvailableCell() {
        int move = computer.makeMove(board);
        assertEquals(1, move, "Computer should select cell 1 (first available)");
    }

    @Test
    public void shouldSkipOccupiedCells() {
        board.setCell(1, 1);
        board.setCell(2, 2);
        int move = computer.makeMove(board);
        assertEquals(3, move, "Computer should skip occupied cells 1 and 2, select 3");
    }

    @Test
    public void shouldSelectNextAvailableCellWhenMultipleFilled() {
        board.setCell(1, 1);
        board.setCell(2, 1);
        board.setCell(3, 1);
        board.setCell(4, 2);
        int move = computer.makeMove(board);
        assertEquals(5, move, "Computer should select cell 5");
    }

    @Test
    public void shouldReturnNegativeOneWhenNoMovesAvailable() {
        for (int i = 1; i <= 9; i++) {
            board.setCell(i, 1);
        }
        int move = computer.makeMove(board);
        assertEquals(-1, move, "Computer should return -1 when board is full");
    }

    @Test
    public void shouldSelectLastCellWhenOnlyOneAvailable() {
        for (int i = 1; i <= 8; i++) {
            board.setCell(i, 1);
        }
        int move = computer.makeMove(board);
        assertEquals(9, move, "Computer should select cell 9 (last available)");
    }

    @Test
    public void shouldAcceptValidMove() {
        human = createHumanWithInput("1\n");
        int move = human.makeMove(board);
        assertEquals(1, move, "Human should accept valid move 1");
    }

    @Test
    public void shouldRetryOnNonNumericInput() {
        human = createHumanWithInput("abc\n5\n");
        int move = human.makeMove(board);
        assertEquals(5, move, "Human should retry and accept 5 after invalid input");
    }

    @Test
    public void shouldRetryOnOutOfRangeInput() {
        human = createHumanWithInput("10\n7\n");
        int move = human.makeMove(board);
        assertEquals(7, move, "Human should retry and accept 7 after out-of-range input");
    }

    @Test
    public void shouldRetryOnNegativeInput() {
        human = createHumanWithInput("-1\n3\n");
        int move = human.makeMove(board);
        assertEquals(3, move, "Human should retry and accept 3 after negative input");
    }

    @Test
    public void shouldRetryOnOccupiedCell() {
        board.setCell(5, 1);
        human = createHumanWithInput("5\n6\n");
        int move = human.makeMove(board);
        assertEquals(6, move, "Human should retry and accept 6 after occupied cell");
    }

    @Test
    public void shouldHandleMultipleRetries() {
        board.setCell(1, 1);
        human = createHumanWithInput("abc\n10\n-5\n1\n4\n");
        int move = human.makeMove(board);
        assertEquals(4, move, "Human should retry multiple times and eventually accept 4");
    }

    @Test
    public void shouldRejectAllInvalidCellNumbers() {
        String[] invalidInputs = { "0\n1\n", "10\n2\n", "100\n3\n", "-1\n4\n" };
        for (String input : invalidInputs) {
            Human testHuman = createHumanWithInput(input);
            int move = testHuman.makeMove(board);
            assertTrue(move >= 1 && move <= 9, "Human should reject invalid input and accept valid one");
        }
    }

}
