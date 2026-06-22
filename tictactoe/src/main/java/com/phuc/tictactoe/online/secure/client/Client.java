package com.phuc.tictactoe.online.secure.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.phuc.tictactoe.online.secure.board.Board;
import com.phuc.tictactoe.online.secure.protocol.ClientRequest;
import com.phuc.tictactoe.online.secure.protocol.ServerResponse;
import com.phuc.tictactoe.online.secure.util.Constants;

public class Client {

    private static String currentBoard = "";
    private static String currentHashBoard = "";
    private static int currentNonce = 0;
    private static String currentHashNonce = "";
    private static long currentTimestamp = 0;
    private static String currentHashTimestamp = "";
    private static boolean isFirstConnect = true;

    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        try {
            startRequestResponseLoop(consoleInput, new PrintWriter(System.out, true));
        } catch (IOException e) {
            System.err.println("Failed to Connect to Server. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

    private static void startRequestResponseLoop(BufferedReader consoleInput, PrintWriter consoleOutput)
            throws IOException {
        while (true) {
            String response;
            if (isFirstConnect) {
                ClientRequest initialRequestProtocol = new ClientRequest();
                response = sendMessageAndReceive(initialRequestProtocol);
                isFirstConnect = false;
            } else {
                int playerMove = getUserMove(consoleInput);
                ClientRequest clientRequestProtocol = new ClientRequest(playerMove, currentBoard, currentHashBoard,
                        currentNonce, currentHashNonce, currentTimestamp, currentHashTimestamp);
                response = sendMessageAndReceive(clientRequestProtocol);
            }
            if (response == null) {
                consoleOutput.println("Server closed the connection.");
                break;
            }

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.decode(response);

            consoleOutput.println(serverResponse.getMessage());
            currentBoard = serverResponse.getBoard();
            currentHashBoard = serverResponse.getHashBoard();
            currentNonce = serverResponse.getNonce();
            currentHashNonce = serverResponse.getHashNonce();
            currentTimestamp = serverResponse.getTimestamp();
            currentHashTimestamp = serverResponse.getHashTimestamp();

            if (Board.isOneLiner(currentBoard)) {
                Board.fromOneLiner(serverResponse.getBoard(), new PrintWriter(System.out, true)).display();
            } else {
                consoleOutput.println("[Server] " + response);
            }

            if (serverResponse.isFinished()) {
                break;
            }
        }
    }

    private static String sendMessageAndReceive(ClientRequest request) throws IOException {
        try (Socket socket = new Socket(Constants.SOCKET_ADDRESS, Constants.SOCKET_PORT)) {
            PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutput.println(request);

            String response = serverInput.readLine();
            return response;
        }
    }

    private static int getUserMove(BufferedReader consoleInput) throws IOException {
        String request = consoleInput.readLine();
        request = request == null ? "q" : request;

        int playerMove;
        if (request.equalsIgnoreCase("q")) {
            playerMove = -1;
        } else {
            try {
                playerMove = Integer.parseInt(request);
                if (playerMove <= 0 || playerMove >= 10) {
                    playerMove = -2;
                }
            } catch (NumberFormatException e) {
                playerMove = -2;
            }
        }
        return playerMove;
    }

}