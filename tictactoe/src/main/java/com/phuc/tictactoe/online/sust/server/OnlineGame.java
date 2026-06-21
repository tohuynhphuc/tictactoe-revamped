package com.phuc.tictactoe.online.sust.server;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.phuc.tictactoe.online.sust.Constants;
import com.phuc.tictactoe.online.sust.server.board.Board;
import com.phuc.tictactoe.online.sust.server.player.Computer;

/**
 * The game instance.
 */
public class OnlineGame {

    private final Board board;
    private final Computer computer;
    private boolean isFinished;

    public OnlineGame() {
        board = new Board(new PrintWriter(OutputStream.nullOutputStream(), true));

        this.computer = new Computer("2");
        this.isFinished = false;
    }

    public String processRequest(String request) {
        if (request.equalsIgnoreCase("q")) {
            isFinished = true;
            return Constants.GAME_END;
        }

        int clientMove;

        try {
            clientMove = Integer.parseInt(request);
        } catch (NumberFormatException e) {
            return Constants.CELL_INVALID;
        }

        if (!board.isMoveInRange(clientMove)) {
            return Constants.CELL_INVALID;
        }

        if (!board.isCellEmpty(clientMove)) {
            return Constants.CELL_OCCUPIED;
        }

        board.setCell(clientMove, 1);

        if (board.checkWin()) {
            isFinished = true;
            return "Player#1 won!";
        } else if (board.isFull()) {
            isFinished = true;
            return Constants.DRAW_ANNOUNCEMENT;
        }

        int computerMove = computer.makeMove(board);
        board.setCell(computerMove, 2);

        if (board.checkWin()) {
            isFinished = true;
            return "Player#2 won!";
        } else if (board.isFull()) {
            isFinished = true;
            return Constants.DRAW_ANNOUNCEMENT;
        }

        return board.oneLiner();
    }

    public boolean isFinished() {
        return isFinished;
    }

}
