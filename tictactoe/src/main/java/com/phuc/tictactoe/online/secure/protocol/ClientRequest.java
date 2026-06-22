package com.phuc.tictactoe.online.secure.protocol;

import com.phuc.tictactoe.online.secure.board.Board;

public class ClientRequest implements Protocol {

    /** Move 1-9, -1 = quit, -2 = invalid */
    private int move;
    private String board;
    private String hashBoard;
    private int nonce;
    private String hashNonce;
    private long timestamp;
    private String hashTimestamp;

    public ClientRequest() {
        this(0, "", "", 0, "", 0, "");
    }

    public ClientRequest(int move, String board, String hashBoard, int nonce, String hashNonce, long timestamp,
            String hashTimestamp) {
        this.move = move;
        this.board = board;
        this.hashBoard = hashBoard;
        this.nonce = nonce;
        this.hashNonce = hashNonce;
        this.timestamp = timestamp;
        this.hashTimestamp = hashTimestamp;
    }

    @Override
    public int getSize() {
        return 7;
    }

    @Override
    public void decode(String raw) {
        String contents = raw.substring(1, raw.length() - 1);
        String[] parts = contents.split(";", getSize());

        this.move = Integer.parseInt(parts[0]);
        this.board = parts[1];
        this.hashBoard = parts[2];
        this.nonce = Integer.parseInt(parts[3]);
        this.hashNonce = parts[4];
        this.timestamp = Long.parseLong(parts[5]);
        this.hashTimestamp = parts[6];
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
            Integer.valueOf(parts[3]);
            Long.valueOf(parts[5]);
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
        sb.append(board).append(";");
        sb.append(hashBoard).append(";");
        sb.append(nonce).append(";");
        sb.append(hashNonce).append(";");
        sb.append(timestamp).append(";");
        sb.append(hashTimestamp);
        sb.append("]");

        return sb.toString();
    }

    public int getMove() {
        return move;
    }

    public String getBoard() {
        return board;
    }

    public String getHashBoard() {
        return hashBoard;
    }

    public int getNonce() {
        return nonce;
    }

    public String getHashNonce() {
        return hashNonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHashTimestamp() {
        return hashTimestamp;
    }

}
