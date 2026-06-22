package com.phuc.tictactoe.online.secure.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.phuc.tictactoe.online.secure.protocol.ClientRequest;
import com.phuc.tictactoe.online.secure.protocol.ServerResponse;
import com.phuc.tictactoe.online.secure.util.Constants;
import com.phuc.tictactoe.online.secure.util.Hash;

public class Server {

    private static ServerSocket serverSocket;

    private static String SECRET_HASH_BOARD_KEY = "secket Key 3312%";

    public static void main(String[] args) {
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
                acceptClient(clientSocket);
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
        handleClient(input, output);
    }

    private static void handleClient(BufferedReader input, PrintWriter output) throws IOException {
        OnlineGame game = new OnlineGame();

        ClientRequest clientRequest = new ClientRequest();
        clientRequest.decode(input.readLine());
        ServerResponse response = game.processRequest(clientRequest);
        output.println(response);
    }

    public static String generateHash(String input) {
        return Hash.sha256(input + SECRET_HASH_BOARD_KEY);
    }

    public static boolean verifyHash(String input, String originalHash) {
        return Hash.sha256(input + SECRET_HASH_BOARD_KEY).equals(originalHash);
    }

}
