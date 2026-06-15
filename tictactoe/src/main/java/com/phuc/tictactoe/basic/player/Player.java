package com.phuc.tictactoe.basic.player;

import com.phuc.tictactoe.basic.board.Board;

public abstract class Player {

    protected final String name;

    public Player(String name) {
        this.name = name;
    }

    /**
     * Makes a move on the board.
     * 
     * @param board the board
     * @return the move
     */
    public abstract int makeMove(Board board);

    public String getName() {
        return name;
    }

}
