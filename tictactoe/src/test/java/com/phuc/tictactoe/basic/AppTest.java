package com.phuc.tictactoe.basic;

import java.io.BufferedReader;
import java.io.IOException;
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

    private BufferedReader scanner;

    @BeforeEach
    public void setUp() {
        originalOut = System.out;
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
    }

    @Test
    public void shouldWarnNoCLIArgument() throws IOException {
        App.main(new String[] {});
        String expectedOutput = "Please, input a valid option [1-2]";
        assertEquals(expectedOutput, scanner.readLine());
    }

    @Test
    public void shouldWarnInvalidCLIArgument() throws IOException {
        App.main(new String[] { "abc" });
        String expectedOutput = "Please, input a valid option [1-2]";
        assertEquals(expectedOutput, scanner.readLine());
    }

    @Test
    public void shouldWarnMultipleCLIArgument() throws IOException {
        App.main(new String[] { "1", "2" });
        String expectedOutput = "Please, input a valid option [1-2]";
        assertEquals(expectedOutput, scanner.readLine());
    }

    @Test
    public void shouldPassCorrectCLIArgument() throws IOException {
        App.main(new String[] { "1" });
        String expectedOutput = "Hello!";
        assertEquals(expectedOutput, scanner.readLine());
    }

}
