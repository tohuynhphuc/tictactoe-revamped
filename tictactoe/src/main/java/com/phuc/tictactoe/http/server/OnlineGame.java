package com.phuc.tictactoe.http.server;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import com.phuc.tictactoe.http.board.Board;
import com.phuc.tictactoe.http.database.PlayerMoveDatabase;
import com.phuc.tictactoe.http.protocol.ClientRequest;
import com.phuc.tictactoe.http.protocol.ServerResponse;
import com.phuc.tictactoe.http.server.player.Computer;
import com.phuc.tictactoe.http.util.Constants;

/**
 * The game instance.
 */
public class OnlineGame {

    private final Computer computer;
    private boolean isFinished;


    public OnlineGame() {
        this.computer = new Computer("2");
        this.isFinished = false;
    }

    public ServerResponse processRequest(ClientRequest request, PlayerMoveDatabase database) throws SQLException {
        int clientMove = request.getMove();

        if (clientMove == 0) {
            isFinished = false;
            return new ServerResponse(isFinished,
                    new Board(new PrintWriter(OutputStream.nullOutputStream(), true)).oneLiner(),
                    Constants.INITIAL_MESSAGE);
        }

        if (clientMove == -1) {
            isFinished = true;
            return new ServerResponse(isFinished, request.getBoard(), Constants.GAME_END);
        }

        if (clientMove == -2) {
            return new ServerResponse(isFinished, request.getBoard(), Constants.CELL_INVALID);
        }

        boolean isHashBoardCorrect = Server.verifyHashBoard(request.getBoard(), request.getHashBoard());
        boolean isHashNonceCorrect = Server.verifyHashNonce(request.getNonce(), request.getHashNonce());
        boolean isHashTimestampCorrect = Server.verifyHashTimestamp(request.getTimestamp(), request.getHashTimestamp());

        if (!isHashBoardCorrect || !isHashNonceCorrect || !isHashTimestampCorrect) {
            isFinished = true;
            return new ServerResponse(isFinished, request.getBoard(), Constants.CHEATER_FOUND);
        }

        if (database.containsNonce(request.getNonce())) {
            isFinished = true;
            return new ServerResponse(isFinished, request.getBoard(), Constants.CHEATER_FOUND);
        }

        if (System.currentTimeMillis() - request.getTimestamp() > Constants.MOVE_TIMEOUT_MILLIS) {
            isFinished = true;
            return new ServerResponse(isFinished, request.getBoard(), Constants.MOVE_TIMEOUT);
        }

        database.insert(request.getNonce(), request.getTimestamp());

        Board board = Board.fromOneLiner(request.getBoard(), new PrintWriter(OutputStream.nullOutputStream(), true));

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

}
