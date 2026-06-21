package com.phuc.tictactoe.online.mumt.server;

import java.io.OutputStream;
import java.io.PrintWriter;

import com.phuc.tictactoe.online.mumt.Constants;
import com.phuc.tictactoe.online.mumt.board.Board;
import com.phuc.tictactoe.online.mumt.protocol.ClientRequest;
import com.phuc.tictactoe.online.mumt.protocol.ServerResponse;
import com.phuc.tictactoe.online.mumt.server.player.Computer;

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

    public ServerResponse processRequest(ClientRequest request) {
        int clientMove = request.getMove();

        if (clientMove == -1) {
            isFinished = true;
            return new ServerResponse(isFinished, request.getBoard(), Constants.GAME_END);
        }

        if (clientMove == -2) {
            return new ServerResponse(isFinished, request.getBoard(), Constants.CELL_INVALID);
        }

        if (!board.isMoveInRange(clientMove)) {
            return new ServerResponse(isFinished, request.getBoard(), Constants.CELL_INVALID);
        }

        if (!board.isCellEmpty(clientMove)) {
            return new ServerResponse(isFinished, request.getBoard(), Constants.CELL_OCCUPIED);
        }

        board.setCell(clientMove, 1);

        if (board.checkWin()) {
            isFinished = true;
            return new ServerResponse(isFinished, board, "Player#1 won!");
        } else if (board.isFull()) {
            isFinished = true;
            return new ServerResponse(isFinished, board, Constants.DRAW_ANNOUNCEMENT);
        }

        int computerMove = computer.makeMove(board);
        board.setCell(computerMove, 2);

        if (board.checkWin()) {
            isFinished = true;
            return new ServerResponse(isFinished, board, "Player#2 won!");
        } else if (board.isFull()) {
            isFinished = true;
            return new ServerResponse(isFinished, board, Constants.DRAW_ANNOUNCEMENT);
        }
        return new ServerResponse(isFinished, board, "Player#1's turn");
    }

    public boolean isFinished() {
        return isFinished;
    }

}
