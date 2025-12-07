import org.example.dto.CategoryDTO;
import org.example.dto.ProductDTO;
import org.example.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    private CategoryController categoryController;
    private CategoryAdminController categoryAdminController;

    @BeforeEach
    void setUp() {
        categoryController = new CategoryController(categoryService);
        categoryAdminController = new CategoryAdminController(categoryService);
    }

    @Test
    void testGetAllCategories() {
        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setId(1L);
        categoryDTO1.setName("Electronics");

        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setId(2L);
        categoryDTO2.setName("Books");

        List<CategoryDTO> categories = Arrays.asList(categoryDTO1, categoryDTO2);

        when(categoryService.getAllCategories()).thenReturn(categories);

        ResponseEntity<List<CategoryDTO>> response = categoryController.getAllCategories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Electronics", response.getBody().get(0).getName());
    }

    @Test
    void testGetCategoryById() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Test Category");

        when(categoryService.getCategoryById(1L)).thenReturn(categoryDTO);

        ResponseEntity<CategoryDTO> response = categoryController.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Test Category", response.getBody().getName());
    }

    @Test
    void testGetProductsByCategory() {
        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);
        productDTO1.setName("Laptop");

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);
        productDTO2.setName("Phone");

        List<ProductDTO> products = Arrays.asList(productDTO1, productDTO2);

        when(categoryService.getProductsByCategoryIdDto(1L)).thenReturn(products);

        ResponseEntity<List<ProductDTO>> response = categoryController.getProductsByCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Laptop", response.getBody().get(0).getName());
    }

    @Test
    void testCreateCategory_Success() {
        String token = "Bearer admintoken";
        CategoryDTO.CreateCategory createCategory = new CategoryDTO.CreateCategory();
        createCategory.setName("New Category");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("New Category");

        when(categoryService.createCategory(token, createCategory)).thenReturn(categoryDTO);

        ResponseEntity<?> response = categoryAdminController.createCategory(token, createCategory);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        CategoryDTO responseBody = (CategoryDTO) response.getBody();
        assertEquals(1L, responseBody.getId());
        assertEquals("New Category", responseBody.getName());
    }

    @Test
    void testCreateCategory_AccessDenied() {
        String token = "Bearer usertoken";
        CategoryDTO.CreateCategory createCategory = new CategoryDTO.CreateCategory();

        when(categoryService.createCategory(token, createCategory))
                .thenThrow(new SecurityException("Доступ запрещен"));

        ResponseEntity<?> response = categoryAdminController.createCategory(token, createCategory);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Доступ запрещен", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void testUpdateCategory_Success() {
        String token = "Bearer admintoken";
        Long categoryId = 1L;
        CategoryDTO.UpdateCategory updateCategory = new CategoryDTO.UpdateCategory();
        updateCategory.setName("Updated Category");

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(categoryId);
        categoryDTO.setName("Updated Category");

        when(categoryService.updateCategory(token, categoryId, updateCategory)).thenReturn(categoryDTO);

        ResponseEntity<?> response = categoryAdminController.updateCategory(token, categoryId, updateCategory);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CategoryDTO responseBody = (CategoryDTO) response.getBody();
        assertEquals(categoryId, responseBody.getId());
        assertEquals("Updated Category", responseBody.getName());
    }

    @Test
    void testUpdateCategory_NotFound() {
        String token = "Bearer admintoken";
        Long categoryId = 999L;
        CategoryDTO.UpdateCategory updateCategory = new CategoryDTO.UpdateCategory();

        when(categoryService.updateCategory(token, categoryId, updateCategory))
                .thenThrow(new IllegalArgumentException("Категория не найдена"));

        ResponseEntity<?> response = categoryAdminController.updateCategory(token, categoryId, updateCategory);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteCategory_Success() {
        String token = "Bearer admintoken";
        Long categoryId = 1L;

        ResponseEntity<?> response = categoryAdminController.deleteCategory(token, categoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteCategory_AccessDenied() {
        String token = "Bearer usertoken";
        Long categoryId = 1L;

        when(categoryService.deleteCategory(token, categoryId))
                .thenThrow(new SecurityException("Доступ запрещен"));

        ResponseEntity<?> response = categoryAdminController.deleteCategory(token, categoryId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Доступ запрещен", ((Map<?, ?>) response.getBody()).get("error"));
    }
}