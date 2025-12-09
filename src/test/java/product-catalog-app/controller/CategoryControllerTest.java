package controller;

import com.productCatalogService.controller.CategoryController;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private MockMvc mockMvc;

    private CategoryDTO category1;
    private CategoryDTO category2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        category1 = CategoryDTO.builder()
                .id(1L)
                .name("Electronics")
                .build();

        category2 = CategoryDTO.builder()
                .id(2L)
                .name("Books")
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnCategories() throws Exception {
        // Arrange
        List<CategoryDTO> categories = Arrays.asList(category1, category2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Books"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getCategoryById_WithValidId_ShouldReturnCategory() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(category1);

        // Act & Assert
        mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic devices"));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    void getCategoryById_WithInvalidId_ShouldThrowException() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(999L))
                .thenThrow(new RuntimeException("Category not found"));

        // Act & Assert
        mockMvc.perform(get("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(categoryService, times(1)).getCategoryById(999L);
    }
}