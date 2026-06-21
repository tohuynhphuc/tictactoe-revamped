package com.phuc.tictactoe.online.must.server.player;

import com.phuc.tictactoe.online.must.board.Board;

public class Computer extends Player {

    public Computer(String name) {
        super(name);
    }

    @Override
    public int makeMove(Board board) {
        for (int i = 1; i <= board.getNumCells(); i++) {
            if (board.isValidMove(i)) {
                return i;
            }
        }
        return -1;
    }

}
