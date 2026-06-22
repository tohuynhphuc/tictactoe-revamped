package com.phuc.tictactoe.online.secure.cheat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.phuc.tictactoe.online.secure.board.Board;
import com.phuc.tictactoe.online.secure.protocol.ClientRequest;
import com.phuc.tictactoe.online.secure.protocol.ServerResponse;
import com.phuc.tictactoe.online.secure.util.Constants;

public class CheatClient {

    private static String currentBoard = "";
    private static String currentHashBoard = "";

    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("CHEAT CLIENT! BEWARE...");

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
            int manualMove = getManualMove(consoleInput, consoleOutput);
            String manualBoard = getManualBoard(consoleInput, consoleOutput);
            String manualHashBoard = getManualHashBoard(consoleInput, consoleOutput);

            ClientRequest clientRequestProtocol = new ClientRequest(manualMove, manualBoard, manualHashBoard, 0, "", 0,
                    "");

            String response = sendMessageAndReceive(clientRequestProtocol);
            if (response == null) {
                consoleOutput.println("Server closed the connection.");
                break;
            }

            ServerResponse serverResponse = new ServerResponse();
            serverResponse.decode(response);

            consoleOutput.println(serverResponse.getMessage());
            currentBoard = serverResponse.getBoard();
            currentHashBoard = serverResponse.getHashBoard();

            if (Board.isOneLiner(currentBoard)) {
                Board.fromOneLiner(serverResponse.getBoard(), new PrintWriter(System.out, true)).display();
                consoleOutput.println("Hash: " + currentHashBoard);
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

    private static int getManualMove(BufferedReader consoleInput, PrintWriter consoleOutput) throws IOException {
        consoleOutput.println("Enter move [1-9], or q to quit.");

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

    private static String getManualBoard(BufferedReader consoleInput, PrintWriter consoleOutput) throws IOException {
        while (true) {
            consoleOutput.println("Enter board as 9 digits using 0, 1, and 2: ");
            String input = consoleInput.readLine();

            if (input == null) {
                return "";
            }

            input = input.trim();
            if (!input.matches("[0-2]{9}")) {
                consoleOutput.println("Invalid board. Enter exactly 9 digits containing only 0, 1, or 2.");
                continue;
            }

            return convertDigitsToOneLiner(input);
        }
    }

    private static String convertDigitsToOneLiner(String digits) {
        StringBuilder board = new StringBuilder();

        board.append("{");
        for (int i = 0; i < digits.length(); i++) {
            board.append(digits.charAt(i));
            if (i < digits.length() - 1) {
                board.append(",");
            }
        }
        board.append("}");

        return board.toString();
    }

    private static String getManualHashBoard(BufferedReader consoleInput, PrintWriter consoleOutput)
            throws IOException {
        consoleOutput.println("Enter hashBoard manually: ");
        String hashBoard = consoleInput.readLine();

        return hashBoard == null ? "" : hashBoard.trim();
    }

}
