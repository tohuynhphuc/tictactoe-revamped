package com.phuc.tictactoe.online.sust.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import com.phuc.tictactoe.online.sust.Constants;
import com.phuc.tictactoe.online.sust.server.player.Computer;
import com.phuc.tictactoe.online.sust.server.player.Human;
import com.phuc.tictactoe.online.sust.server.player.Player;

public class Server {

    private static ServerSocket serverSocket;

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
            try (Socket clientSocket = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {
                handleClient(input, output);
            } catch (IOException e) {
                System.err.println("Failed to Connect to Client. Program Exiting.");
                System.err.println("Error Message: " + e.getMessage());
                return;
            }
        }
    }

    private static void handleClient(BufferedReader input, PrintWriter output) throws IOException {
        Scanner scanner = new Scanner(input);

        Player playerOne = new Human("1", scanner, output);
        Player playerTwo = new Computer("2");

        Game game = new Game(1, playerOne, playerTwo, output);

        Thread gameThread = new Thread(() -> {
            game.start();
        });

        gameThread.start();

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            System.err.println("Game Thread Interrupted.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

}
