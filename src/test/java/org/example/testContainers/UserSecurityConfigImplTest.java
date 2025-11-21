package org.example.testContainers;

import org.example.config.impl.UserSecurityConfigImpl;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserSecurityConfigImplTest extends BaseDatabaseTest {

    private UserSecurityConfigImpl userSecurityConfig;
    private User testUser;
    private String testUsername;

    @BeforeEach
    void setUp() {
        userSecurityConfig = UserSecurityConfigImpl.getInstance();

        // Используем уникальное имя для каждого теста
        testUsername = generateUniqueUsername("testuser");
        testUser = new User(testUsername, "password", Role.USER);
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        // Сбрасываем состояние синглтона через рефлексию
        resetUserSecurityConfig();
    }

    @Test
    void testVerificationUser_Success() {
        // When
        boolean result = userSecurityConfig.verificationUser(testUsername, "password");

        // Then
        assertThat(result).isTrue();
        assertThat(userSecurityConfig.isAuthenticated()).isTrue();
        assertThat(userSecurityConfig.getThisUser().getUserName()).isEqualTo(testUsername);
    }

    @Test
    void testVerificationUser_WrongPassword() {
        // When
        boolean result = userSecurityConfig.verificationUser(testUsername, "wrongpassword");

        // Then
        assertThat(result).isFalse();
        assertThat(userSecurityConfig.isAuthenticated()).isFalse();
        assertThat(userSecurityConfig.getThisUser()).isNull();
    }

    @Test
    void testVerificationUser_UserNotFound() {
        // When
        boolean result = userSecurityConfig.verificationUser("nonexistent_user", "password");

        // Then
        assertThat(result).isFalse();
        assertThat(userSecurityConfig.isAuthenticated()).isFalse();
        assertThat(userSecurityConfig.getThisUser()).isNull();
    }

    @Test
    void testRegisterUser() {
        // Given
        String uniqueUsername = generateUniqueUsername("newuser");

        // When
        userSecurityConfig.registerUser(uniqueUsername, "newpassword", Role.ADMIN);

        // Then
        assertThat(userSecurityConfig.isAuthenticated()).isTrue();
        assertThat(userSecurityConfig.getThisUser()).isNotNull();
        assertThat(userSecurityConfig.getThisUser().getUserName()).isEqualTo(uniqueUsername);
        assertThat(userSecurityConfig.getThisUser().getRole()).isEqualTo(Role.ADMIN);

        // Проверяем что пользователь сохранен в БД
        Optional<User> savedUser = userRepository.findByUsername(uniqueUsername);
        assertThat(savedUser).as("User should be saved in database").isPresent();
        assertThat(savedUser.get().getRole()).as("User role should be ADMIN").isEqualTo(Role.ADMIN);
    }

    @Test
    void testIsAuthenticated() {
        // Given
        userSecurityConfig.verificationUser(testUsername, "password");
        assertThat(userSecurityConfig.isAuthenticated()).isTrue();

        // When logout
        userSecurityConfig.logout();

        // Then
        assertThat(userSecurityConfig.isAuthenticated()).isFalse();
        assertThat(userSecurityConfig.getThisUser()).isNull();
    }

    @Test
    void testLogout() {
        // Given
        userSecurityConfig.verificationUser(testUsername, "password");
        assertThat(userSecurityConfig.isAuthenticated()).isTrue();
        assertThat(userSecurityConfig.getThisUser()).isNotNull();

        // When
        userSecurityConfig.logout();

        // Then
        assertThat(userSecurityConfig.isAuthenticated()).isFalse();
        assertThat(userSecurityConfig.getThisUser()).isNull();
    }

    @Test
    void testSetThisUser() {
        // Given
        User newUser = new User();
        newUser.setId(999L);
        newUser.setUserName("manualuser");
        newUser.setRole(Role.ADMIN);

        // When
        userSecurityConfig.setThisUser(newUser);

        // Then
        assertThat(userSecurityConfig.getThisUser()).isEqualTo(newUser);
        assertThat(userSecurityConfig.isAuthenticated()).isTrue();
    }

    private void resetUserSecurityConfig() {
        try {
            // Сбрасываем состояние через рефлексию
            var field = UserSecurityConfigImpl.class.getDeclaredField("thisUser");
            field.setAccessible(true);
            field.set(userSecurityConfig, null);
        } catch (Exception e) {
            System.out.println("Warning: Could not reset UserSecurityConfig state: " + e.getMessage());
        }
    }
}
