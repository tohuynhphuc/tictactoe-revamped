package com.phuc.tictactoe.online.secure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.phuc.tictactoe.online.secure.util.Constants;

public class PlayerMoveDatabase implements AutoCloseable {

    private static final String DATABASE_URL = "jdbc:h2:mem:tictactoe";
    private final Connection connection;
    private final ScheduledExecutorService cleanupScheduler;
    private final Object databaseLock = new Object();
    private final int CLEANUP_INTERVAL_SECONDS = 5;

    public PlayerMoveDatabase() throws SQLException {
        connection = DriverManager.getConnection(DATABASE_URL);
        createTable();

        cleanupScheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "database-cleanup");
            thread.setDaemon(true);
            return thread;
        });

        cleanupScheduler.scheduleAtFixedRate(this::databaseCleanup, CLEANUP_INTERVAL_SECONDS, CLEANUP_INTERVAL_SECONDS,
                TimeUnit.SECONDS);
    }

    private void createTable() throws SQLException {
        String sql = "create table player_move (nonce int primary key, timestamp bigint not null)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    public void insert(int nonce, long timestamp) throws SQLException {
        String sql = "insert into player_move (nonce, timestamp) values (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nonce);
            statement.setLong(2, timestamp);
            statement.executeUpdate();
        }
    }

    public boolean containsNonce(int nonce) throws SQLException {
        String sql = "select 1 from player_move where nonce = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nonce);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private void databaseCleanup() {
        try {
            removeExpiredEntries();
        } catch (SQLException e) {
            System.err.println("Failed to remove expired player move entries.");
            System.err.println("Error Message: " + e.getMessage());
        }
    }

    private void removeExpiredEntries() throws SQLException {
        long expirationCutoff = System.currentTimeMillis() - Constants.MOVE_TIMEOUT_MILLIS;

        String sql = "delete from player_move where timestamp < ?";

        synchronized (databaseLock) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, expirationCutoff);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        cleanupScheduler.shutdownNow();
        synchronized (databaseLock) {
            connection.close();
        }
    }

}
