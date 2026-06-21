package com.phuc.tictactoe.online.sust.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.phuc.tictactoe.online.sust.Constants;
import com.phuc.tictactoe.online.sust.server.board.Board;

public class Client {

    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

        try (Socket socket = new Socket("localhost", Constants.SOCKET_PORT);
                BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);) {
            System.out.println(serverInput.readLine());

            while (true) {
                String request = consoleInput.readLine();
                request = request == null ? "q" : request;

                serverOutput.println(request);

                String response = serverInput.readLine();

                if (response == null) {
                    System.out.println("Server closed the connection.");
                    break;
                }

                if (Board.isOneLiner(response)) {
                    Board receivedBoard = Board.fromOneLiner(response, new PrintWriter(System.out, true));
                    receivedBoard.display();
                } else {
                    System.out.println("[Server] " + response);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to Connect to Server. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

}
