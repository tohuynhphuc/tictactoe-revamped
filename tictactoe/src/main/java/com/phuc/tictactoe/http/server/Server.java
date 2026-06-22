package com.phuc.tictactoe.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.phuc.tictactoe.http.database.PlayerMoveDatabase;
import com.phuc.tictactoe.http.protocol.ClientRequest;
import com.phuc.tictactoe.http.protocol.ServerResponse;
import com.phuc.tictactoe.http.util.Constants;
import com.phuc.tictactoe.http.util.Hash;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class Server {

    private static final String GAME_PATH = "/game";

    private static final String SECRET_HASH_BOARD_KEY = "secket Key 3312%";
    private static final String SECRET_HASH_NONCE_KEY = "one two three !!! nonce";
    private static final String SECRET_HASH_TIMESTAMP_KEY = "six seven six seven 6767";

    public static void main(String[] args) {
        try {
            PlayerMoveDatabase database = new PlayerMoveDatabase();
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(Constants.SOCKET_PORT), 0);

            ExecutorService executor = Executors.newSingleThreadExecutor();

            httpServer.setExecutor(executor);
            httpServer.createContext(GAME_PATH, exchange -> {
                try {
                    handleRequest(exchange, database);
                } catch (SQLException e) {
                    System.err.println("Error while handling request.");
                    System.err.println("Error Message: " + e.getMessage());
                }
            });

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nStopping server...");

                httpServer.stop(0);
                executor.shutdown();

                try {
                    database.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close database: " + e.getMessage());
                }
            }));

            httpServer.start();

            System.out.println("Database is ready.\nServer started on localhost:" + Constants.SOCKET_PORT + GAME_PATH);
        } catch (IOException e) {
            System.err.println("Failed to Work with Server Socket. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Failed to Work with Database. Program Exiting.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

    private static void handleRequest(HttpExchange exchange, PlayerMoveDatabase database)
            throws IOException, SQLException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");

        try (exchange) {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.getResponseHeaders().set("Allow", "POST");
                sendResponse(exchange, 405, "Method not allowed. Only POST.");
                return;
            }

            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            ServerResponse serverResponse = handleClient(requestBody, database);

            sendResponse(exchange, 200, serverResponse.toString());
        }
    }

    private static ServerResponse handleClient(String requestRaw, PlayerMoveDatabase database)
            throws IOException, SQLException {
        OnlineGame game = new OnlineGame();

        ClientRequest clientRequest = new ClientRequest();
        clientRequest.decode(requestRaw);

        ServerResponse response = game.processRequest(clientRequest, database);
        return response;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(responseBytes);
        }
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
