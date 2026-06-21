package com.phuc.tictactoe.online.must.protocol;

import com.phuc.tictactoe.online.must.board.Board;

public class ServerResponse implements Protocol {

    private boolean isFinished;
    private String board;
    private String message;

    public ServerResponse() {

    }

    public ServerResponse(boolean isFinished, String board, String message) {
        this.isFinished = isFinished;
        this.board = board;
        this.message = message;
    }

    public ServerResponse(boolean isFinished, Board board, String message) {
        this(isFinished, board.oneLiner(), message);
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public void decode(String raw) {
        String contents = raw.substring(1, raw.length() - 1);
        String[] parts = contents.split(";", getSize());
        boolean finished = parts[0].equals("1");

        this.isFinished = finished;
        this.board = parts[1];
        this.message = parts[2];
    }

    @Override
    public boolean isValid(String raw) {
        if (raw == null) {
            return false;
        }

        if (!raw.startsWith("[") || !raw.endsWith("]")) {
            return false;
        }

        String contents = raw.substring(1, raw.length() - 1);
        String[] parts = contents.split(";", -1);

        if (parts.length != getSize()) {
            return false;
        }

        String finishedValue = parts[0].trim();
        String boardValue = parts[1].trim();

        if (!finishedValue.equals("0") && !finishedValue.equals("1")) {
            return false;
        }

        return Board.isOneLiner(boardValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(isFinished ? 1 : 0).append(";");
        sb.append(board).append(";");
        sb.append(message);
        sb.append("]");

        return sb.toString();
    }

    public boolean isFinished() {
        return isFinished;
    }

    public String getBoard() {
        return board;
    }

    public String getMessage() {
        return message;
    }

}
