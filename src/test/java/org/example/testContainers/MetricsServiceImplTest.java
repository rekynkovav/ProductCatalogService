package org.example.testContainers;

import org.example.config.ConnectionManager;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.impl.MetricsServiceImpl;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsServiceImplTest extends BaseDatabaseTest {

    private MetricsServiceImpl metricsService = MetricsServiceImpl.getInstance();
    private UserServiceImpl userService = UserServiceImpl.getInstance();
    private User testUser;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        cleanupDatabase();

        testUser = createTestUser();
        userService.saveUser(testUser);
    }

    @Test
    void testIncrementMetric() {
        // When
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);

        // Then
        int value = metricsService.getMetricValue(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);
        assertThat(value).isEqualTo(1);
    }

    @Test
    void testIncrementMetricWithValue() {
        // When
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT, 3);

        // Then
        int value = metricsService.getMetricValue(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);
        assertThat(value).isEqualTo(3);
    }

    @Test
    void testGetUserMetrics() {
        // Given
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGOUT_COUNT);

        // When
        Map<String, Integer> userMetrics = metricsService.getUserMetrics(testUser.getId());

        // Then
        assertThat(userMetrics).containsKeys("LOGIN_COUNT", "LOGOUT_COUNT");
    }

    @Test
    void testGetAllMetrics() {
        // Given
        User user2 = createTestUser("user2", Role.ADMIN);
        userService.saveUser(user2);

        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);
        metricsService.incrementMetric(user2.getId(), MetricsServiceImpl.LOGIN_COUNT);

        // When
        Map<String, Integer> allMetrics = metricsService.getAllMetrics();

        // Then
        assertThat(allMetrics.get("LOGIN_COUNT")).isEqualTo(2);
    }

    @Test
    void testGetUserMetricsByUsername() {
        // Given
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);

        // When
        Map<String, Integer> metrics = metricsService.getUserMetricsByUsername("testuser");

        // Then
        assertThat(metrics.get("LOGIN_COUNT")).isEqualTo(1);
    }

    @Test
    void testGetOverallStatistics() {
        // Given
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.PRODUCT_ADD_COUNT);

        // When
        String statistics = metricsService.getOverallStatistics();

        // Then
        assertThat(statistics).contains("Входы в систему");
        assertThat(statistics).contains("Добавлено товаров");
    }

    @Test
    void testGetUserStatistics() {
        // Given
        metricsService.incrementMetric(testUser.getId(), MetricsServiceImpl.LOGIN_COUNT);

        // When
        String statistics = metricsService.getUserStatistics(testUser.getId());

        // Then
        assertThat(statistics).contains("testuser");
        assertThat(statistics).contains("Входы в систему");
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
        try (Connection connection = ConnectionManager.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            // Отключаем проверки внешних ключей для PostgreSQL
            statement.execute("SET session_replication_role = 'replica'");

            // Очищаем все таблицы в правильном порядке с CASCADE
            statement.execute("TRUNCATE TABLE entity.user_metrics CASCADE");
            statement.execute("TRUNCATE TABLE entity.users CASCADE");

            // Включаем проверки внешних ключей обратно
            statement.execute("SET session_replication_role = 'origin'");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to cleanup database", e);
        }
    }
}