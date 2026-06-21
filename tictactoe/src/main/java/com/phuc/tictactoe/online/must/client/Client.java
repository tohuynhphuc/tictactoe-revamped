package com.phuc.tictactoe.online.must.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.phuc.tictactoe.online.must.Constants;
import com.phuc.tictactoe.online.must.board.Board;
import com.phuc.tictactoe.online.must.protocol.ClientRequest;
import com.phuc.tictactoe.online.must.protocol.ServerResponse;

public class Client {

    private static String currentBoard = "";
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
                ClientRequest initialRequestProtocol = new ClientRequest(0, currentBoard);
                response = sendMessageAndReceive(initialRequestProtocol);
                isFirstConnect = false;
            } else {
                String request = consoleInput.readLine();
                request = request == null ? "q" : request;

                int playerMove;
                if (request.equalsIgnoreCase("q")) {
                    playerMove = -1;
                } else {
                    try {
                        playerMove = Integer.parseInt(request);
                    } catch (NumberFormatException e) {
                        playerMove = -2;
                    }
                }

                ClientRequest clientRequestProtocol = new ClientRequest(playerMove, currentBoard);
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

}