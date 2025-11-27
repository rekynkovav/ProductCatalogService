package org.example.testContainers;

import org.example.context.ApplicationContext;
import org.example.model.entity.User;
import org.example.model.entity.Role;
import org.example.service.SecurityService;
import org.example.servlet.ApiServlet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты для ApiServlet с использованием TestContainers
 */
@Testcontainers
class ApiServletIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private ApiServlet apiServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        // Настройка тестовой БД
        System.setProperty("DB_URL", postgres.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgres.getUsername());
        System.setProperty("DB_PASSWORD", postgres.getPassword());

        apiServlet = new ApiServlet();
        apiServlet.init();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        objectMapper = new ObjectMapper();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @AfterEach
    void tearDown() {
        // Очистка контекста после каждого теста
        ApplicationContext.clear();
    }

    @Test
    void testHandleLoginSuccess() throws Exception {
        // Given
        String requestBody = "{\"username\":\"admin\",\"password\":\"admin\"}";
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        SecurityService securityService = ApplicationContext.getInstance().getBean(SecurityService.class);
        securityService.registerUser("admin", "admin", Role.ADMIN);

        // When
        apiServlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("success"));
        assertTrue(responseContent.contains("Login successful"));
    }

    @Test
    void testHandleLoginInvalidCredentials() throws Exception {
        // Given
        String requestBody = "{\"username\":\"wrong\",\"password\":\"wrong\"}";
        when(request.getPathInfo()).thenReturn("/login");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // When
        apiServlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Invalid credentials"));
    }

    @Test
    void testHandleRegister() throws Exception {
        // Given
        String requestBody = "{\"username\":\"newuser\",\"password\":\"password\",\"role\":\"USER\"}";
        when(request.getPathInfo()).thenReturn("/register");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(requestBody)));

        // When
        apiServlet.doPost(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Registration successful"));
    }

    @Test
    void testHandleGetProducts() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/products");
        when(request.getParameter("page")).thenReturn("0");

        // When
        apiServlet.doGet(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Products retrieved"));
    }

    @Test
    void testHandleGetUserProfileUnauthenticated() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/user/profile");
        when(request.getSession()).thenReturn(mock(HttpSession.class));

        // When
        apiServlet.doGet(request, response);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("User not authenticated"));
    }

    @Test
    void testHandleLogout() throws Exception {
        // Given
        when(request.getPathInfo()).thenReturn("/logout");
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);

        User user = new User("testuser", "password", Role.USER);
        user.setId(1L);
        when(session.getAttribute("user")).thenReturn(user);

        // When
        apiServlet.doPost(request, response);

        // Then
        verify(session).invalidate();
        verify(response).setStatus(HttpServletResponse.SC_OK);
        String responseContent = responseWriter.toString();
        assertTrue(responseContent.contains("Logout successful"));
    }
}