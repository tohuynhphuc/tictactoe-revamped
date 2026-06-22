package com.phuc.tictactoe.http.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import com.phuc.tictactoe.http.database.PlayerMoveDatabase;
import com.phuc.tictactoe.http.protocol.ClientRequest;
import com.phuc.tictactoe.http.protocol.ServerResponse;
import com.phuc.tictactoe.http.util.Constants;
import com.phuc.tictactoe.http.util.Hash;

public class Server {

    private static ServerSocket serverSocket;

    private static final String SECRET_HASH_BOARD_KEY = "secket Key 3312%";
    private static final String SECRET_HASH_NONCE_KEY = "one two three !!! nonce";
    private static final String SECRET_HASH_TIMESTAMP_KEY = "six seven six seven 6767";

    public static void main(String[] args) {
        try {
            PlayerMoveDatabase database = new PlayerMoveDatabase();
            serverSocket = new ServerSocket(Constants.SOCKET_PORT);

            System.out.println("Database is ready.\nServer started on localhost:" + Constants.SOCKET_PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    acceptClient(clientSocket, database);
                } catch (IOException e) {
                    System.err.println("Failed to Connect to Client. Program Exiting.");
                    System.err.println("Error Message: " + e.getMessage());
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to Wotk with Server Socket. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to Work with Database. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

    private static void acceptClient(Socket clientSocket, PlayerMoveDatabase database)
            throws IOException, SQLException {
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
        handleClient(input, output, database);
    }

    private static void handleClient(BufferedReader input, PrintWriter output, PlayerMoveDatabase database)
            throws IOException, SQLException {
        OnlineGame game = new OnlineGame();

        ClientRequest clientRequest = new ClientRequest();
        clientRequest.decode(input.readLine());

        ServerResponse response = game.processRequest(clientRequest, database);
        output.println(response);
    }

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
