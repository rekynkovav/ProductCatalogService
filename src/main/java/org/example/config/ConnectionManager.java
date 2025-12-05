package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager {
    private static final int POOL_SIZE = 10;
    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);

    static {
        try {
            Class.forName(DataBaseConfig.getDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver not found", e);
        }
    }

    public ConnectionManager() {
        initializeConnectionPool();
    }

    private void initializeConnectionPool() {
        try {
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection connection = createNewConnection();
                connectionPool.offer(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(
                DataBaseConfig.getUrl(),
                DataBaseConfig.getUserName(),
                DataBaseConfig.getPassword()
        );
    }

    public Connection getConnection() {
        try {
            Connection connection = connectionPool.take();
            if (connection.isClosed() || !connection.isValid(2)) {
                connection = createNewConnection();
            }
            return connection;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }
}
