package com.phuc.tictactoe.online.mumt.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.phuc.tictactoe.online.mumt.Constants;
import com.phuc.tictactoe.online.mumt.board.Board;
import com.phuc.tictactoe.online.mumt.protocol.ClientRequest;
import com.phuc.tictactoe.online.mumt.protocol.ServerResponse;

public class Server {

    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        try {
            serverSocket = new ServerSocket(Constants.SOCKET_PORT);
        } catch (IOException e) {
            System.err.println("Failed to Start Server Socket. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
            return;
        }

        System.out.println("Server started on localhost:" + Constants.SOCKET_PORT);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                threadPool.execute(() -> {
                    try {
                        acceptClient(clientSocket);
                    } catch (IOException e) {
                        System.err.println("Failed to Connect to Client. Program Exiting.");
                        System.err.println("Error Message: " + e.getMessage());
                    }
                });

            } catch (IOException e) {
                System.err.println("Failed to Connect to Client. Program Exiting.");
                System.err.println("Error Message: " + e.getMessage());
                return;
            }
        }
    }

    private static void acceptClient(Socket clientSocket) throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
        output.println(new ServerResponse(false, new Board(output).oneLiner(), Constants.INITIAL_MESSAGE));
        handleClient(input, output);
    }

    private static void handleClient(BufferedReader input, PrintWriter output) throws IOException {
        OnlineGame game = new OnlineGame();

        String request;
        while (!game.isFinished() && (request = input.readLine()) != null) {
            ClientRequest clientRequest = new ClientRequest();
            clientRequest.decode(request);
            ServerResponse response = game.processRequest(clientRequest);
            output.println(response);
        }
    }

}
