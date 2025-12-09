package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productCatalogService.controller.CategoryAdminController;
import com.productCatalogService.dto.CategoryDTO;
import com.productCatalogService.service.CategoryService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryAdminControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryAdminController categoryAdminController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryAdminController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createCategory_WithValidRequest_ShouldReturnCreated() throws Exception {
        // Arrange
        CategoryDTO.CreateCategory createCategory = CategoryDTO.CreateCategory.builder()
                .name("New Category")
                .build();

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("New Category")
                .build();

        when(categoryService.createCategory(eq("Bearer admin-token"), any()))
                .thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Category"));

        verify(categoryService, times(1)).createCategory(eq("Bearer admin-token"), any());
    }

    @Test
    void updateCategory_WithValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        CategoryDTO.UpdateCategory updateCategory = CategoryDTO.UpdateCategory.builder()
                .name("Updated Category")
                .build();

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Updated Category")
                .build();

        when(categoryService.updateCategory(eq("Bearer admin-token"), eq(1L), any()))
                .thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(put("/api/admin/categories/1")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Category"));

        verify(categoryService, times(1)).updateCategory(eq("Bearer admin-token"), eq(1L), any());
    }

    @Test
    void createCategory_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CategoryDTO.CreateCategory invalidRequest = CategoryDTO.CreateCategory.builder()
                .name("")  // Empty name - violates @NotBlank
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(), any());
    }

    @Test
    void createCategory_WithNullName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", null);

        // Act & Assert
        mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).createCategory(any(), any());
    }

    @Test
    void updateCategory_WithEmptyName_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CategoryDTO.UpdateCategory invalidRequest = CategoryDTO.UpdateCategory.builder()
                .name("")  // Empty name - violates @NotBlank
                .build();

        // Act & Assert
        mockMvc.perform(put("/api/admin/categories/1")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).updateCategory(any(), any(), any());
    }

    @Test
    void deleteCategory_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory("Bearer admin-token", 1L);

        // Act & Assert
        mockMvc.perform(delete("/api/admin/categories/1")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory("Bearer admin-token", 1L);
    }
}