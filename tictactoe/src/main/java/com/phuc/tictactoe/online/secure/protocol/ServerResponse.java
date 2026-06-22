package com.phuc.tictactoe.online.secure.protocol;

import java.util.concurrent.ThreadLocalRandom;

import com.phuc.tictactoe.online.secure.board.Board;
import com.phuc.tictactoe.online.secure.server.Server;

public class ServerResponse implements Protocol {

    private boolean isFinished;
    private String board;
    private String message;
    private String hashBoard;
    private int nonce;
    private String hashNonce;
    private long timestamp;
    private String hashTimestamp;

    public ServerResponse() {

    }

    public ServerResponse(boolean isFinished, String board, String message, int nonce, long timestamp) {
        this.isFinished = isFinished;
        this.board = board;
        this.message = message;
        this.hashBoard = Server.generateHashBoard(board);
        this.nonce = nonce;
        this.hashNonce = Server.generateHashNonce(nonce);
        this.timestamp = timestamp;
        this.hashTimestamp = Server.generateHashTimestamp(timestamp);
    }

    public ServerResponse(boolean isFinished, String board, String message) {
        this(isFinished, board, message, ThreadLocalRandom.current().nextInt(), System.currentTimeMillis());
    }

    public ServerResponse(boolean isFinished, Board board, String message, int nonce, long timestamp) {
        this(isFinished, board.oneLiner(), message, nonce, timestamp);
    }

    public ServerResponse(boolean isFinished, Board board, String message) {
        this(isFinished, board.oneLiner(), message);
    }

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public void decode(String raw) {
        String contents = raw.substring(1, raw.length() - 1);
        String[] parts = contents.split(";", getSize());
        boolean finished = parts[0].equals("1");

        this.isFinished = finished;
        this.board = parts[1];
        this.message = parts[2];
        this.hashBoard = parts[3];
        this.nonce = Integer.parseInt(parts[4]);
        this.hashNonce = parts[5];
        this.timestamp = Long.parseLong(parts[6]);
        this.hashTimestamp = parts[7];
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

        try {
            Integer.valueOf(parts[4]);
            Long.valueOf(parts[6]);
        } catch (NumberFormatException e) {
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
        sb.append(message).append(";");
        sb.append(hashBoard).append(";");
        sb.append(nonce).append(";");
        sb.append(hashNonce).append(";");
        sb.append(timestamp).append(";");
        sb.append(hashTimestamp);
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
