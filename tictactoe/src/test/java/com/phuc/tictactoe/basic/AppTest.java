package com.phuc.tictactoe.basic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AppTest {

    private PrintStream originalOut;
    private PipedOutputStream outputStream;

    private InputStream originalIn;

    private BufferedReader scanner;

    private final String INITIAL_BOARD_OUTPUT = getBoard(0, 0, 0, 0, 0, 0, 0, 0, 0);

    public static final String PLAYER_1_TURN = "Player#1's turn";
    public static final String PLAYER_2_TURN = "Player#2's turn";
    public static final String PLAYER_1_WON = "Player#1 won!";
    public static final String PLAYER_2_WON = "Player#2 won!";

    @BeforeEach
    public void setUp() {
        originalOut = System.out;
        originalIn = System.in;

        outputStream = new PipedOutputStream();

        try {
            PipedInputStream inputStream = new PipedInputStream(outputStream);
            scanner = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Logger.getLogger(AbstractPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void shouldWarnNoCLIArgument() throws IOException {
        App.main(new String[] {});
        assertEquals(Constants.INVALID_ARGUMENTS, readLines(1));
    }

    @Test
    public void shouldWarnInvalidCLIArgument() throws IOException {
        App.main(new String[] { "abc" });
        assertEquals(Constants.INVALID_ARGUMENTS, readLines(1));

        App.main(new String[] { "0" });
        assertEquals(Constants.INVALID_ARGUMENTS, readLines(1));

        App.main(new String[] { "3" });
        assertEquals(Constants.INVALID_ARGUMENTS, readLines(1));

        App.main(new String[] { "-1" });
        assertEquals(Constants.INVALID_ARGUMENTS, readLines(1));
    }

    @Test
    public void shouldWarnMultipleCLIArgument() throws IOException {
        App.main(new String[] { "1", "2" });
        assertEquals(Constants.INVALID_ARGUMENTS, readLines(1));
    }

    @Test
    public void shouldPassCorrectCLIArgument() throws IOException {
        App.main(new String[] { "1" });
        assertEquals(Constants.INITIAL_MESSAGE, readLines(1));
        assertEquals(INITIAL_BOARD_OUTPUT, readLines(3));
        assertEquals(PLAYER_1_TURN, readLines(1));
    }

    @Test
    public void shouldQEndGame() throws IOException {
        setInput("q");
        App.main(new String[] { "1" });

        readLines(5);
        assertEquals(Constants.GAME_END, readLines(1));
    }

    @Test
    public void shouldLetUserInputAgainInvalidInput() throws IOException {
        setInput("a", "xyz", "q");
        App.main(new String[] { "1" });

        readLines(5);
        assertEquals(Constants.CELL_INVALID, readLines(1));
        assertEquals(Constants.CELL_INVALID, readLines(1));
        assertEquals(Constants.GAME_END, readLines(1));
    }

    @Test
    public void shouldLetUserInputAgainOutOfRangeInput() throws IOException {
        setInput("10", "-1", "q");
        App.main(new String[] { "1" });

        readLines(5);
        assertEquals(Constants.CELL_INVALID, readLines(1));
        assertEquals(Constants.CELL_INVALID, readLines(1));
        assertEquals(Constants.GAME_END, readLines(1));
    }

    @Test
    public void shouldValidMoveUpdateBoard() throws IOException {
        setInput("2", "q");
        App.main(new String[] { "1" });

        readLines(5);
        assertEquals(getBoard(0, 1, 0, 0, 0, 0, 0, 0, 0), readLines(3));
    }

    @Test
    public void shouldComputerMoveNext() throws IOException {
        setInput("2", "q");
        App.main(new String[] { "1" });

        readLines(5 + 4);
        assertEquals(getBoard(2, 1, 0, 0, 0, 0, 0, 0, 0), readLines(3));
    }

    @Test
    public void shouldComputerMoveFirst() throws IOException {
        setInput("q");
        App.main(new String[] { "2" });

        readLines(4);
        assertEquals(PLAYER_2_TURN, readLines(1));
        assertEquals(getBoard(2, 0, 0, 0, 0, 0, 0, 0, 0), readLines(3));
    }

    @Test
    public void shouldCellIsOccupied() throws IOException {
        setInput("1", "q");
        App.main(new String[] { "2" });

        readLines(9);
        assertEquals(Constants.CELL_OCCUPIED, readLines(1));
    }

    @Test
    public void shouldUserWin() throws IOException {
        setInput("1", "4", "7", "q");
        App.main(new String[] { "1" });

        readLines(5 + 8 + 8 + 3);
        assertEquals(PLAYER_1_WON, readLines(1));
    }

    @Test
    public void shouldComputerWin() throws IOException {
        setInput("4", "7", "q");
        App.main(new String[] { "2" });

        readLines(5 + 4 + 8 + 4 + 3);
        assertEquals(PLAYER_2_WON, readLines(1));
    }

    @Test
    public void shouldDrawUserLastMove() throws IOException {
        setInput("5", "3", "4", "8", "9", "q");
        App.main(new String[] { "1" });

        readLines(5 + 8 + 8 + 8 + 8 + 3);
        assertEquals(Constants.DRAW_ANNOUNCEMENT, readLines(1));
    }

    @Test
    public void shouldDrawComputerLastMove() throws IOException {
        setInput("3", "4", "8", "9", "q");
        App.main(new String[] { "2" });

        readLines(5 + 4 + 8 + 8 + 8 + 7);
        assertEquals(Constants.DRAW_ANNOUNCEMENT, readLines(1));
    }

    @Test
    public void shouldPrintCorrectBoard() throws IOException {
        setInput("3", "4", "8", "9", "q");
        App.main(new String[] { "2" });

        readLines(5 + 4 + 8 + 8 + 8 + 4);
        assertEquals(getBoard(2, 2, 1, 1, 2, 2, 2, 1, 1), readLines(3));
    }

    private void setInput(String... inputs) {
        String simulatedInput = String.join(System.lineSeparator(), inputs) + System.lineSeparator();
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Helper function to read N lines
     * 
     * @param numLines the number of lines
     * @return the output of n lines
     * @throws IOException
     */
    private String readLines(int numLines) throws IOException {
        String output = "";
        for (int i = 1; i <= numLines; i++) {
            output += scanner.readLine() + (i != numLines ? "\r\n" : "");
        }
        return output;
    }

    private String getBoard(int c1, int c2, int c3, int c4, int c5, int c6, int c7, int c8, int c9) {
        return "| " + c1 + " | " + c2 + " | " + c3 + " |\r\n| " + c4 + " | " + c5 + " | " + c6 + " |\r\n| " + c7 + " | "
                + c8 + " | " + c9 + " |";
    }

}
