package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productCatalogService.controller.AuthController;
import com.productCatalogService.dto.UserDTO;
import com.productCatalogService.entity.Role;
import com.productCatalogService.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser_WithValidRequest_ShouldReturnCreated() throws Exception {
        // Arrange
        UserDTO.RegisterRequest registerRequest = UserDTO.RegisterRequest.builder()
                .userName("testuser")
                .password("password123")
                .build();

        UserDTO.AuthResponse authResponse = UserDTO.AuthResponse.builder()
                .message("User registered successfully")
                .token("jwt-token")
                .user(UserDTO.UserInfo.builder()
                        .id(1L)
                        .userName("testuser")
                        .role(Role.USER)
                        .build())
                .build();

        when(authService.register(any(UserDTO.RegisterRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.userName").value("testuser"));

        verify(authService, times(1)).register(any(UserDTO.RegisterRequest.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnOk() throws Exception {
        // Arrange
        UserDTO.LoginRequest loginRequest = UserDTO.LoginRequest.builder()
                .userName("testuser")
                .password("password123")
                .build();

        UserDTO.AuthResponse authResponse = UserDTO.AuthResponse.builder()
                .message("Login successful")
                .token("jwt-token")
                .user(UserDTO.UserInfo.builder()
                        .id(1L)
                        .userName("testuser")
                        .role(Role.USER)
                        .build())
                .build();

        when(authService.login(any(UserDTO.LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.userName").value("testuser"));

        verify(authService, times(1)).login(any(UserDTO.LoginRequest.class));
    }

    @Test
    void logout_WithValidToken_ShouldReturnOk() throws Exception {
        // Arrange
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");

        when(authService.logout("Bearer valid-token")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        verify(authService, times(1)).logout("Bearer valid-token");
    }

    @Test
    void userExists_WhenUserExists_ShouldReturnTrue() throws Exception {
        // Arrange
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", true);

        when(authService.checkUserExists("existinguser")).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/auth/users/exists/existinguser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        verify(authService, times(1)).checkUserExists("existinguser");
    }

    @Test
    void registerUser_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        UserDTO.RegisterRequest invalidRequest = UserDTO.RegisterRequest.builder()
                .userName("")  // Empty username - violates @NotBlank and @Size(min = 3)
                .password("123")  // Too short password - violates @Size(min = 6)
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }
}