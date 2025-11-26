package org.example.testContainers;

import org.example.config.ConnectionManager;
import org.example.context.ApplicationContext;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsRepositoryImplTest extends BaseDatabaseTest {

    private ConnectionManager connectionManager = ApplicationContext.getInstance().getBean(ConnectionManager.class);
    private User testUser;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        cleanupDatabase();

        testUser = createTestUser();
        userRepository.save(testUser);
    }

    @Test
    void testIncrementMetric() {
        // When
        metricsRepository.incrementMetric(testUser.getId(), "LOGIN_COUNT");
        metricsRepository.incrementMetric(testUser.getId(), "LOGIN_COUNT");

        // Then
        int value = metricsRepository.getMetricValue(testUser.getId(), "LOGIN_COUNT");
        assertThat(value).isEqualTo(2);
    }

    @Test
    void testGetMetricValue() {
        // Given
        metricsRepository.incrementMetric(testUser.getId(), "TEST_METRIC");

        // When
        int value = metricsRepository.getMetricValue(testUser.getId(), "TEST_METRIC");

        // Then
        assertThat(value).isEqualTo(1);
    }

    @Test
    void testGetMetricValue_WhenNotExists() {
        // When
        int value = metricsRepository.getMetricValue(testUser.getId(), "NONEXISTENT_METRIC");

        // Then
        assertThat(value).isEqualTo(0);
    }

    @Test
    void testGetUserMetrics() {
        // Given
        metricsRepository.incrementMetric(testUser.getId(), "LOGIN_COUNT");
        metricsRepository.incrementMetric(testUser.getId(), "LOGOUT_COUNT");
        metricsRepository.incrementMetric(testUser.getId(), "LOGIN_COUNT");

        // When
        Map<String, Integer> userMetrics = metricsRepository.getUserMetrics(testUser.getId());

        // Then
        assertThat(userMetrics).hasSize(2);
        assertThat(userMetrics.get("LOGIN_COUNT")).isEqualTo(2);
        assertThat(userMetrics.get("LOGOUT_COUNT")).isEqualTo(1);
    }

    @Test
    void testGetAllMetrics() {
        // Given
        User user2 = createTestUser("user2", Role.ADMIN);
        userRepository.save(user2);

        metricsRepository.incrementMetric(testUser.getId(), "LOGIN_COUNT");
        metricsRepository.incrementMetric(user2.getId(), "LOGIN_COUNT");
        metricsRepository.incrementMetric(user2.getId(), "LOGIN_COUNT");

        // When
        Map<String, Integer> allMetrics = metricsRepository.getAllMetrics();

        // Then
        assertThat(allMetrics.get("LOGIN_COUNT")).isEqualTo(3);
    }

    private User createTestUser() {
        return createTestUser("testuser", Role.USER);
    }

    private User createTestUser(String username, Role role) {
        User user = new User();
        user.setUserName(username);
        user.setPassword("password");
        user.setRole(role);
        return user;
    }

    /**
     * Метод для очистки всех таблиц в базе данных с использованием TRUNCATE CASCADE для PostgreSQL
     */
    private void cleanupDatabase() {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {

            // Очищаем таблицы в правильном порядке (сначала зависимые, потом основные)
            statement.execute("TRUNCATE TABLE entity.user_metrics CASCADE");
            statement.execute("TRUNCATE TABLE entity.users CASCADE");

            // Сбрасываем последовательности
            statement.execute("ALTER SEQUENCE service.user_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.user_metrics_seq RESTART WITH 1");

        } catch (SQLException e) {
            // Если TRUNCATE не работает, используем DELETE
            try (Connection connection = connectionManager.getConnection();
                 Statement statement = connection.createStatement()) {

                statement.execute("DELETE FROM entity.user_metrics");
                statement.execute("DELETE FROM entity.users");
                statement.execute("ALTER SEQUENCE service.user_id_seq  RESTART WITH 1");
                statement.execute("ALTER SEQUENCE service.user_metrics_seq RESTART WITH 1");

            } catch (SQLException ex) {
                throw new RuntimeException("Failed to cleanup database", ex);
            }
        }
    }
}