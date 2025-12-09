package controller;

import com.productCatalogService.controller.UserAdminController;
import com.productCatalogService.dto.UserDTO;
import com.productCatalogService.entity.Role;
import com.productCatalogService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserAdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAdminController userAdminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userAdminController).build();
    }

    @Test
    void getAllUsers_WithAdminToken_ShouldReturnUsersList() throws Exception {
        // Arrange
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .userName("admin")
                .role(Role.ADMIN)
                .build();

        UserDTO user2 = UserDTO.builder()
                .id(2L)
                .userName("user1")
                .role(Role.USER)
                .build();

        List<UserDTO> users = Arrays.asList(user1, user2);

        when(userService.getAllUsersForAdmin("Bearer admin-token")).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].role").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$[1].username").value("user1"))
                .andExpect(jsonPath("$[1].role").value("ROLE_USER"));

        verify(userService, times(1)).getAllUsersForAdmin("Bearer admin-token");
    }
}