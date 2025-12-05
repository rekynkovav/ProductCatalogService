package org.example.testContainers;

import org.example.config.ConnectionManager;
import org.example.context.ApplicationContext;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceImplTest extends BaseDatabaseTest {

    private UserServiceImpl userService = ApplicationContext.getInstance().getBean(UserServiceImpl.class);
    private ConnectionManager connectionManager = ApplicationContext.getInstance().getBean(ConnectionManager.class);

    @BeforeEach
    void setUp() {
        cleanupDatabase();
    }

    @Test
    void testSaveUser() {
        // Given
        User user = createTestUser("testuser", Role.USER);

        // When
        userService.saveUser(user);

        // Then
        Optional<User> foundUser = userService.findByUsername("testuser");
        assertThat(foundUser).isPresent();
    }

    @Test
    void testIsContainsUser() {
        // Given
        User user = createTestUser("existinguser", Role.USER);
        userService.saveUser(user);

        // When & Then
        assertThat(userService.isContainsUser("existinguser")).isTrue();
        assertThat(userService.isContainsUser("nonexistent")).isFalse();
    }

    @Test
    void testFindByUsername() {
        // Given
        User user = createTestUser("testuser", Role.ADMIN);
        userService.saveUser(user);

        // When
        Optional<User> foundUser = userService.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void testShowAllUser() {
        // Given
        userService.saveUser(createTestUser("user1", Role.USER));
        userService.saveUser(createTestUser("user2", Role.ADMIN));

        // When
        List<User> users = userService.showAllUser();

        // Then
        assertThat(users).hasSize(2);
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
                statement.execute("ALTER SEQUENCE service.user_id_seq RESTART WITH 1");
                statement.execute("ALTER SEQUENCE service.user_metrics_seq RESTART WITH 1");

            } catch (SQLException ex) {
                throw new RuntimeException("Failed to cleanup database", ex);
            }
        }
    }
}