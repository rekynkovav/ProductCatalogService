package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ProductDTO;
import org.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit тесты для ProductController.
 * Использует MockMvc для тестирования REST эндпоинтов.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see ProductController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;

    private ProductDto testProduct;
    private List<ProductDto> productList;

    /**
     * Настройка тестового окружения перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();

        testProduct = ProductDto.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .categoryId(1L)
                .build();

        productList = Arrays.asList(
                testProduct,
                ProductDto.builder()
                        .id(2L)
                        .name("Another Product")
                        .description("Another Description")
                        .price(BigDecimal.valueOf(49.99))
                        .quantity(5)
                        .categoryId(1L)
                        .build()
        );
    }

    /**
     * Тест получения всех продуктов.
     */
    @Test
    @DisplayName("Should return all products when GET /api/products")
    void getAllProducts_ShouldReturnProductList() throws Exception {
        given(productService.getAllProducts()).willReturn(productList);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].price", is(99.99)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Another Product")));
    }

    /**
     * Тест получения продукта по ID.
     */
    @Test
    @DisplayName("Should return product when GET /api/products/{id} with valid id")
    void getProductById_WithValidId_ShouldReturnProduct() throws Exception {
        given(productService.getProductById(1L)).willReturn(testProduct);

        mockMvc.perform(get("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    /**
     * Тест создания нового продукта.
     */
    @Test
    @DisplayName("Should create product when POST /api/products with valid data")
    void createProduct_WithValidData_ShouldCreateProduct() throws Exception {
        ProductDto newProduct = ProductDto.builder()
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .categoryId(1L)
                .build();

        ProductDto savedProduct = ProductDto.builder()
                .id(3L)
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .categoryId(1L)
                .build();

        given(productService.createProduct(any(ProductDto.class))).willReturn(savedProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(header().string("Location", "/api/products/3"));
    }

    /**
     * Тест обновления продукта.
     */
    @Test
    @DisplayName("Should update product when PUT /api/products/{id} with valid data")
    void updateProduct_WithValidData_ShouldUpdateProduct() throws Exception {
        ProductDto updatedProduct = ProductDto.builder()
                .name("Updated Product")
                .description("Updated Description")
                .price(BigDecimal.valueOf(89.99))
                .quantity(20)
                .categoryId(2L)
                .build();

        given(productService.updateProduct(anyLong(), any(ProductDto.class))).willReturn(updatedProduct);

        mockMvc.perform(put("/api/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.price", is(89.99)));
    }

    /**
     * Тест удаления продукта.
     */
    @Test
    @DisplayName("Should delete product when DELETE /api/products/{id}")
    void deleteProduct_ShouldDeleteProduct() throws Exception {
        willDoNothing().given(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    /**
     * Тест валидации при создании продукта.
     */
    @Test
    @DisplayName("Should return bad request when POST /api/products with invalid data")
    void createProduct_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        ProductDto invalidProduct = ProductDto.builder()
                .name("")  // Пустое имя
                .price(BigDecimal.valueOf(-10))  // Отрицательная цена
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Тест поиска продуктов по категории.
     */
    @Test
    @DisplayName("Should return products by category when GET /api/products/category/{categoryId}")
    void getProductsByCategory_ShouldReturnProducts() throws Exception {
        given(productService.getProductsByCategoryId(1L)).willReturn(productList);

        mockMvc.perform(get("/api/products/category/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryId", is(1)))
                .andExpect(jsonPath("$[1].categoryId", is(1)));
    }
}