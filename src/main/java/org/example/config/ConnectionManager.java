package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Менеджер соединений с базой данных.
 * Реализует паттерн Singleton для обеспечения единственного экземпляра.
 * Обеспечивает создание и управление соединением с PostgreSQL.
 */
public class ConnectionManager {

    /**
     * Единственный экземпляр менеджера соединений.
     */
    private static ConnectionManager instance;

    /**
     * Соединение с базой данных.
     */
    private Connection connection;

    /**
     * Статический блок инициализации драйвера PostgreSQL.
     * Выполняется при загрузке класса.
     *
     * @throws RuntimeException если драйвер PostgreSQL не найден
     */
    static {
        try {
            Class.forName(DataBaseConfig.getDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgresSQL Driver not found", e);
        }
    }

    /**
     * Приватный конструктор для реализации паттерна Singleton.
     * Инициализирует соединение с базой данных.
     *
     * @throws RuntimeException если не удалось установить соединение с БД
     */
    private ConnectionManager() {
        try {
            this.connection = DriverManager.getConnection(
                    DataBaseConfig.getUrl(),
                    DataBaseConfig.getUserName(),
                    DataBaseConfig.getPassword()
            );
            System.out.println("Connected to database: " + DataBaseConfig.getUrl());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection to: " +
                                       DataBaseConfig.getUrl() + " with user: " + DataBaseConfig.getUserName(), e);
        }
    }

    /**
     * Возвращает единственный экземпляр менеджера соединений.
     *
     * @return экземпляр ConnectionManager
     */
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    /**
     * Возвращает соединение с базой данных.
     * Если соединение закрыто или не существует, создает новое.
     *
     * @return активное соединение с БД
     * @throws RuntimeException если не удалось получить соединение
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        DataBaseConfig.getUrl(),
                        DataBaseConfig.getUserName(),
                        DataBaseConfig.getPassword()
                );
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    /**
     * Закрывает соединение с базой данных.
     * Если соединение активно, закрывает его.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}