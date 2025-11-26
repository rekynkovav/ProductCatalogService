package org.example.testContainers;

import org.example.context.ApplicationContext;
import org.example.repository.impl.MetricsRepositoryImpl;
import org.example.repository.impl.UserRepositoryImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
public abstract class BaseDatabaseTest {

    protected static Connection connection;
    protected static Statement statement;

    protected static UserRepositoryImpl userRepository = ApplicationContext.getInstance().getBean(UserRepositoryImpl.class);
    protected static MetricsRepositoryImpl metricsRepository = ApplicationContext.getInstance().getBean(MetricsRepositoryImpl.class);

    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setUpBeforeAll() throws Exception {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        // Создаем соединение с Testcontainer
        connection = DriverManager.getConnection(jdbcUrl, username, password);
        statement = connection.createStatement();

        // Создаем схемы и таблицы
        createSchemasAndTables();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Очищаем данные перед каждым тестом
        clearTestData();
    }

    @AfterAll
    static void tearDownAfterAll() throws Exception {
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private static void createSchemasAndTables() throws SQLException {
        // Создаем схемы
        statement.execute("CREATE SCHEMA IF NOT EXISTS entity");
        statement.execute("CREATE SCHEMA IF NOT EXISTS service");

        // Создаем последовательности
        statement.execute("CREATE SEQUENCE IF NOT EXISTS service.user_id_seq START 1 INCREMENT 1");
        statement.execute("CREATE SEQUENCE IF NOT EXISTS service.product_id_seq START 1 INCREMENT 1");
        statement.execute("CREATE SEQUENCE IF NOT EXISTS service.basket_item_seq START 1 INCREMENT 1");
        statement.execute("CREATE SEQUENCE IF NOT EXISTS service.user_metrics_seq START 1 INCREMENT 1");

        // Создаем таблицы
        statement.execute("""
                    CREATE TABLE IF NOT EXISTS entity.users (
                        id BIGINT PRIMARY KEY DEFAULT nextval('service.user_id_seq'),
                        user_name VARCHAR(100) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL,
                        role VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

        statement.execute("""
                    CREATE TABLE IF NOT EXISTS entity.products (
                        id BIGINT PRIMARY KEY DEFAULT nextval('service.product_id_seq'),
                        name VARCHAR(200) NOT NULL,
                        quantity INTEGER NOT NULL,
                        price INTEGER NOT NULL,
                        category VARCHAR(100) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

        statement.execute("""
                    CREATE TABLE IF NOT EXISTS entity.user_basket (
                        id BIGINT PRIMARY KEY DEFAULT nextval('service.basket_item_seq'),
                        user_id BIGINT NOT NULL REFERENCES entity.users(id) ON DELETE CASCADE,
                        product_id BIGINT NOT NULL REFERENCES entity.products(id) ON DELETE CASCADE,
                        quantity INTEGER DEFAULT 1,
                        added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE(user_id, product_id)
                    )
                """);

        statement.execute("""
                    CREATE TABLE IF NOT EXISTS entity.user_metrics (
                        id BIGINT PRIMARY KEY DEFAULT nextval('service.user_metrics_seq'),
                        user_id BIGINT NOT NULL REFERENCES entity.users(id) ON DELETE CASCADE,
                        metric_type VARCHAR(50) NOT NULL,
                        value INTEGER DEFAULT 0,
                        created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        UNIQUE(user_id, metric_type)
                    )
                """);
    }

    protected static void clearTestData() throws SQLException {
        try {
            // Используем TRUNCATE для быстрой очистки
            statement.execute("TRUNCATE TABLE entity.user_metrics CASCADE");
            statement.execute("TRUNCATE TABLE entity.user_basket CASCADE");
            statement.execute("TRUNCATE TABLE entity.products CASCADE");
            statement.execute("TRUNCATE TABLE entity.users CASCADE");

            // Сброс последовательностей
            statement.execute("ALTER SEQUENCE service.user_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.product_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.basket_item_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.user_metrics_seq RESTART WITH 1");

        } catch (SQLException e) {
            // Если TRUNCATE не работает, используем DELETE
            statement.execute("DELETE FROM entity.user_metrics");
            statement.execute("DELETE FROM entity.user_basket");
            statement.execute("DELETE FROM entity.products");
            statement.execute("DELETE FROM entity.users");

            statement.execute("ALTER SEQUENCE service.user_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.product_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.basket_item_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.user_metrics_seq RESTART WITH 1");
        }
    }

    // Вспомогательный метод для создания уникальных имен пользователей
    protected String generateUniqueUsername(String base) {
        return base + "_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
    }
}