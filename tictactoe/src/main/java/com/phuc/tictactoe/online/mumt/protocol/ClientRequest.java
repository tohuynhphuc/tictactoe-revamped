package com.phuc.tictactoe.online.mumt.protocol;

import com.phuc.tictactoe.online.mumt.board.Board;

public class ClientRequest implements Protocol {

    /** Move 1-9, -1 = quit, -2 = invalid */
    private int move;
    private String board;

    public ClientRequest() {

    }

    public ClientRequest(int move, String board) {
        this.move = move;
        this.board = board;
    }

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public void decode(String raw) {
        String contents = raw.substring(1, raw.length() - 1);
        String[] parts = contents.split(";", getSize());

        this.move = Integer.parseInt(parts[0]);
        this.board = parts[1];
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
        String[] parts = contents.split(";", getSize());

        if (parts.length != getSize()) {
            return false;
        }

        String moveValue = parts[0].trim();
        String boardValue = parts[1].trim();

        try {
            Integer.valueOf(moveValue);
        } catch (NumberFormatException e) {
            return false;
        }

        return Board.isOneLiner(boardValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(move).append(";");
        sb.append(board);
        sb.append("]");

        return sb.toString();
    }

    public int getMove() {
        return move;
    }

    public String getBoard() {
        return board;
    }

}
