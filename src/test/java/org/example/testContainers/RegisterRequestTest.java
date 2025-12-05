package org.example.testContainers;

import org.example.model.entity.Role;
import org.example.servlet.RegisterRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса RegisterRequest
 */
class RegisterRequestTest {

    @Test
    void testDefaultConstructor() {
        RegisterRequest request = new RegisterRequest();

        assertNull(request.getUsername());
        assertNull(request.getPassword());
        assertNull(request.getRole());
    }

    @Test
    void testParameterizedConstructor() {
        String username = "testuser";
        String password = "testpass";
        Role role = Role.USER;

        RegisterRequest request = new RegisterRequest(username, password, role);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(role, request.getRole());
    }

    @Test
    void testSettersAndGetters() {
        RegisterRequest request = new RegisterRequest();
        String username = "newuser";
        String password = "newpass";
        Role role = Role.ADMIN;

        request.setUsername(username);
        request.setPassword(password);
        request.setRole(role);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
        assertEquals(role, request.getRole());
    }

    @Test
    void testToString() {
        RegisterRequest request = new RegisterRequest("user", "pass", Role.USER);
        String toString = request.toString();

        assertTrue(toString.contains("user"));
        assertTrue(toString.contains("[PROTECTED]"));
        assertTrue(toString.contains("USER"));
        assertFalse(toString.contains("pass")); // Пароль должен быть скрыт
    }
}