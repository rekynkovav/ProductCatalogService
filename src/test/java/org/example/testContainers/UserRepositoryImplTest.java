package org.example.testContainers;

import org.example.config.ConnectionManager;
import org.example.context.ApplicationContext;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.repository.impl.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
class UserRepositoryImplTest extends BaseDatabaseTest {

    private UserRepositoryImpl userRepository = ApplicationContext.getInstance().getBean(UserRepositoryImpl.class);
    private ConnectionManager connectionManager = ApplicationContext.getInstance().getBean(ConnectionManager.class);

    @BeforeEach
    void setUp() {
        String sql = "TRUNCATE TABLE entity.user_metrics, entity.users CASCADE";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error cleaning database", e);
        }
    }

    @Test
    void testSaveUser() {
        // Given
        User user = new User();
        user.setUserName("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUserName()).isEqualTo("testuser");
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void testFindById() {
        // Given
        User user = createTestUser("testuser", Role.USER);
        User savedUser = userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserName()).isEqualTo("testuser");
    }

    @Test
    void testFindByUsername() {
        // Given
        User user = createTestUser("testuser", Role.USER);
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUserName()).isEqualTo("testuser");
    }

    @Test
    void testExistsByUsername() {
        // Given
        User user = createTestUser("existinguser", Role.USER);
        userRepository.save(user);

        // When & Then
        assertThat(userRepository.existsByUsername("existinguser")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    void testFindAllUser() {
        // Given
        userRepository.save(createTestUser("user1", Role.USER));
        userRepository.save(createTestUser("user2", Role.ADMIN));

        // When
        List<User> users = userRepository.findAllUser();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getUserName)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void testDeleteById() {
        // Given
        User user = createTestUser("todelete", Role.USER);
        User savedUser = userRepository.save(user);

        // When
        userRepository.deleteById(savedUser.getId());

        // Then
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    private User createTestUser(String username, Role role) {
        User user = new User();
        user.setUserName(username);
        user.setPassword("password");
        user.setRole(role);
        return user;
    }
}