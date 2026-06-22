package com.phuc.tictactoe.http.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import com.phuc.tictactoe.http.board.Board;
import com.phuc.tictactoe.http.protocol.ClientRequest;
import com.phuc.tictactoe.http.protocol.ServerResponse;
import com.phuc.tictactoe.http.util.Constants;

public class Client {

    private static final String GAME_PATH = "/game";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private static final URI SERVER_URI = URI
            .create("http://" + Constants.SOCKET_ADDRESS + ":" + Constants.SOCKET_PORT + GAME_PATH);

    private static String currentBoard = "";
    private static String currentHashBoard = "";

    private static int currentNonce = 0;
    private static String currentHashNonce = "";

    private static long currentTimestamp = 0;
    private static String currentHashTimestamp = "";

    private static boolean isFirstConnect = true;

    public static void main(String[] args) {
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter consoleOutput = new PrintWriter(System.out, true);

        try {
            startRequestResponseLoop(consoleInput, consoleOutput);
        } catch (IOException e) {
            System.err.println("Failed to Connect to Server. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

    private static void startRequestResponseLoop(BufferedReader consoleInput, PrintWriter consoleOutput)
            throws IOException {
        while (true) {
            ClientRequest clientRequest;
            if (isFirstConnect) {
                clientRequest = new ClientRequest();
                isFirstConnect = false;
            } else {
                int playerMove = getUserMove(consoleInput);
                clientRequest = new ClientRequest(playerMove, currentBoard, currentHashBoard, currentNonce,
                        currentHashNonce, currentTimestamp, currentHashTimestamp);
            }
            String response = sendMessageAndReceive(clientRequest);
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
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(SERVER_URI)
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .header("Accept", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(request.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> httpResponse = HTTP_CLIENT.send(httpRequest,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (httpResponse.statusCode() != 200) {
                throw new IOException("Server returned HTTP " + httpResponse.statusCode() + ": " + httpResponse.body());
            }

            if (httpResponse.body() == null || httpResponse.body().isBlank()) {
                throw new IOException("Server returned an empty response.");
            }

            return httpResponse.body();
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for server response.", e);
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