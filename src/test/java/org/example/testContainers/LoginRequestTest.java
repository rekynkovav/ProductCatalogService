package org.example.testContainers;

import org.example.servlet.LoginRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для класса LoginRequest
 */
class LoginRequestTest {

    @Test
    void testDefaultConstructor() {
        LoginRequest request = new LoginRequest();

        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void testParameterizedConstructor() {
        String username = "testuser";
        String password = "testpass";

        LoginRequest request = new LoginRequest(username, password);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        LoginRequest request = new LoginRequest();
        String username = "newuser";
        String password = "newpass";

        request.setUsername(username);
        request.setPassword(password);

        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
    }
}