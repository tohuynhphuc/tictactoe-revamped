package com.phuc.tictactoe.servlet.server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.phuc.tictactoe.servlet.database.PlayerMoveDatabase;
import com.phuc.tictactoe.servlet.protocol.ClientRequest;
import com.phuc.tictactoe.servlet.protocol.ServerResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/game")
public class Server extends HttpServlet {

    private PlayerMoveDatabase database;

    @Override
    public void init() throws ServletException {
        System.out.println("NEW SERVLET VERSION IS RUNNING");
        try {
            database = new PlayerMoveDatabase();
            System.out.println("Database is ready.");
        } catch (SQLException e) {
            System.err.println("Failed to Work with Database.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-store");

        String requestRaw = request.getReader().lines().collect(Collectors.joining());

        try {
            ServerResponse serverResponse = handleClient(requestRaw, response);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(serverResponse.toString());
        } catch (IOException | SQLException e) {
            System.err.println("Unexpected Error.");
            System.err.println("Error Message: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Invalid client request.");
        }
    }

    @Override
    public void destroy() {
        if (database == null) {
            return;
        }

        try {
            database.close();
        } catch (SQLException e) {
            getServletContext().log("Could not close the database.", e);
        }
    }

    private ServerResponse handleClient(String requestRaw, HttpServletResponse response)
            throws IOException, SQLException {
        OnlineGame game = new OnlineGame();

        ClientRequest clientRequest = new ClientRequest();

        if (!clientRequest.isValid(requestRaw)) {
            System.out.println(requestRaw);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Invalid client request.");
            return null;
        }

        clientRequest.decode(requestRaw);

        ServerResponse serverResponse;
        synchronized (database) {
            serverResponse = game.processRequest(clientRequest, database);
        }
        return serverResponse;
    }

}
