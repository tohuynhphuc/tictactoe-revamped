package com.phuc.tictactoe.servlet.server.util;

import com.phuc.tictactoe.servlet.util.Hash;

public class ServerHash {

    private static final String SECRET_HASH_BOARD_KEY = "secket Key 3312%";
    private static final String SECRET_HASH_NONCE_KEY = "one two three !!! nonce";
    private static final String SECRET_HASH_TIMESTAMP_KEY = "six seven six seven 6767";

    public static String generateHashBoard(String input) {
        return Hash.sha256(input + SECRET_HASH_BOARD_KEY);
    }

    public static boolean verifyHashBoard(String input, String originalHash) {
        return generateHashBoard(input).equals(originalHash);
    }

    public static String generateHashNonce(int input) {
        return Hash.sha256(input + SECRET_HASH_NONCE_KEY);
    }

    public static boolean verifyHashNonce(int input, String originalHash) {
        return generateHashNonce(input).equals(originalHash);
    }

    public static String generateHashTimestamp(long input) {
        return Hash.sha256(input + SECRET_HASH_TIMESTAMP_KEY);
    }

    public static boolean verifyHashTimestamp(long input, String originalHash) {
        return generateHashTimestamp(input).equals(originalHash);
    }

}
