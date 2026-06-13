package com.phuc.tictactoe.board;

public class Board {

    private final int NUM_CELLS = 9;

    private int[] boardData;

    public Board() {
        setupBoard();
    }

    private void setupBoard() {
        boardData = new int[NUM_CELLS];
        for (int i = 0; i < NUM_CELLS; i++) {
            boardData[i] = 0;
        }
    }

}
