package com.phuc.tictactoe.online.must.server.player;

import com.phuc.tictactoe.online.must.board.Board;
import com.phuc.tictactoe.online.must.exception.GameQuitException;

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
    public abstract int makeMove(Board board) throws GameQuitException;

    public String getName() {
        return name;
    }

}
