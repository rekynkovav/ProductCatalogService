import org.example.dto.UserDTO;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    @Test
    void testRegisterUser_Success() {
        UserDTO.RegisterRequest registerRequest = new UserDTO.RegisterRequest();
        registerRequest.setUserName("testuser");
        registerRequest.setPassword("password123");

        UserDTO.AuthResponse authResponse = new UserDTO.AuthResponse();
        authResponse.setMessage("Пользователь успешно зарегистрирован");
        authResponse.setToken("token123");
        authResponse.setUser(new UserDTO.UserInfo(1L, "testuser", org.example.model.entity.Role.USER));

        when(authService.register(any(UserDTO.RegisterRequest.class))).thenReturn(authResponse);

        ResponseEntity<UserDTO.AuthResponse> response = authController.registerUser(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Пользователь успешно зарегистрирован", response.getBody().getMessage());
        assertEquals("token123", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUser().getUsername());
    }

    @Test
    void testRegisterUser_Failure() {
        UserDTO.RegisterRequest registerRequest = new UserDTO.RegisterRequest();
        registerRequest.setUserName("existinguser");
        registerRequest.setPassword("password123");

        when(authService.register(any(UserDTO.RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("Пользователь уже существует"));

        ResponseEntity<UserDTO.AuthResponse> response = authController.registerUser(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Пользователь уже существует", response.getBody().getMessage());
        assertNull(response.getBody().getToken());
        assertNull(response.getBody().getUser());
    }

    @Test
    void testLogin_Success() {
        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        UserDTO.AuthResponse authResponse = new UserDTO.AuthResponse();
        authResponse.setMessage("Успешный вход в систему");
        authResponse.setToken("token123");
        authResponse.setUser(new UserDTO.UserInfo(1L, "testuser", org.example.model.entity.Role.USER));

        when(authService.login(any(UserDTO.LoginRequest.class))).thenReturn(authResponse);

        ResponseEntity<UserDTO.AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Успешный вход в систему", response.getBody().getMessage());
        assertEquals("token123", response.getBody().getToken());
    }

    @Test
    void testLogin_Failure() {
        UserDTO.LoginRequest loginRequest = new UserDTO.LoginRequest();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpass");

        when(authService.login(any(UserDTO.LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Неверное имя пользователя или пароль"));

        ResponseEntity<UserDTO.AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Неверное имя пользователя или пароль", response.getBody().getMessage());
    }

    @Test
    void testLogout_Success() {
        String token = "Bearer validtoken";
        Map<String, String> logoutResponse = Map.of("message", "Успешный выход из системы");

        when(authService.logout(token)).thenReturn(logoutResponse);

        ResponseEntity<Map<String, String>> response = authController.logout(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Успешный выход из системы", response.getBody().get("message"));
    }

    @Test
    void testUserExists() {
        String username = "testuser";
        Map<String, Boolean> existsResponse = Map.of("exists", true);

        when(authService.checkUserExists(username)).thenReturn(existsResponse);

        ResponseEntity<Map<String, Boolean>> response = authController.userExists(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("exists"));
    }
}