package com.phuc.tictactoe.online.mumt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.phuc.tictactoe.online.mumt.Constants;
import com.phuc.tictactoe.online.mumt.board.Board;
import com.phuc.tictactoe.online.mumt.protocol.ClientRequest;
import com.phuc.tictactoe.online.mumt.protocol.ServerResponse;

public class Client {

    private static String currentBoard;

    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        try {
            connectsToServer(consoleInput, new PrintWriter(System.out, true));
        } catch (IOException e) {
            System.err.println("Failed to Connect to Server. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

    private static void connectsToServer(BufferedReader consoleInput, PrintWriter consoleOutput) throws IOException {
        try (Socket socket = new Socket(Constants.SOCKET_ADDRESS, Constants.SOCKET_PORT)) {
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);

            ServerResponse initialMessage = new ServerResponse();
            initialMessage.decode(serverInput.readLine());
            consoleOutput.println(initialMessage.getMessage());
            currentBoard = initialMessage.getBoard();
            Board.fromOneLiner(currentBoard, consoleOutput).display();

            startRequestResponseLoop(consoleInput, consoleOutput, serverInput, serverOutput);
        }
    }

    private static void startRequestResponseLoop(BufferedReader consoleInput, PrintWriter output,
            BufferedReader serverInput, PrintWriter serverOutput) throws IOException {
        while (true) {
            int playerMove = getUserMove(consoleInput);
            ClientRequest clientRequestProtocol = new ClientRequest(playerMove, currentBoard);
            serverOutput.println(clientRequestProtocol);

            String response = serverInput.readLine();
            if (response == null) {
                output.println("Server closed the connection.");
                break;
            }

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.decode(response);

            output.println(serverResponse.getMessage());
            currentBoard = serverResponse.getBoard();

            if (Board.isOneLiner(currentBoard)) {
                Board receivedBoard = Board.fromOneLiner(serverResponse.getBoard(), new PrintWriter(System.out, true));
                receivedBoard.display();
            } else {
                output.println("[Server] " + response);
            }

            if (serverResponse.isFinished()) {
                break;
            }
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