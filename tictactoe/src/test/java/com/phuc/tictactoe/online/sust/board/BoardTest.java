package com.phuc.tictactoe.online.sust.board;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(new PrintWriter(System.out, true));
    }

    @Test
    public void shouldReturnOneLiner() {
        board.setCell(1, 1);
        board.setCell(5, 2);
        board.setCell(9, 1);
        assertEquals("{1,0,0,0,2,0,0,0,1}", board.oneLiner());
    }

}
