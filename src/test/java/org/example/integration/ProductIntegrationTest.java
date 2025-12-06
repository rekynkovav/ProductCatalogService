package org.example.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ProductDto;
import org.example.model.Product;
import org.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для Product API.
 * Использует Testcontainers с PostgreSQL для тестирования полного стека приложения.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
@DisplayName("Product Integration Tests")
class ProductIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    /**
     * Настройка тестовых данных перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        productRepository.deleteAll();

        // Создаем тестовый продукт
        testProduct = Product.builder()
                .name("Integration Test Product")
                .description("Integration Test Description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .categoryId(1L)
                .build();

        testProduct = productRepository.save(testProduct);
    }

    /**
     * Интеграционный тест получения всех продуктов.
     */
    @Test
    @DisplayName("Should return all products - integration test")
    void getAllProducts_IntegrationTest() throws Exception {
        // Добавляем еще один продукт
        Product anotherProduct = Product.builder()
                .name("Another Product")
                .description("Another Description")
                .price(BigDecimal.valueOf(49.99))
                .quantity(5)
                .categoryId(2L)
                .build();

        productRepository.save(anotherProduct);

        // When & Then
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Integration Test Product")))
                .andExpect(jsonPath("$[1].name", is("Another Product")));
    }

    /**
     * Интеграционный тест получения продукта по ID.
     */
    @Test
    @DisplayName("Should return product by id - integration test")
    void getProductById_IntegrationTest() throws Exception {
        mockMvc.perform(get("/api/products/{id}", testProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testProduct.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Product")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.quantity", is(10)));
    }

    /**
     * Интеграционный тест создания продукта.
     */
    @Test
    @DisplayName("Should create product - integration test")
    void createProduct_IntegrationTest() throws Exception {
        ProductDto newProduct = ProductDto.builder()
                .name("New Integration Product")
                .description("New Integration Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .categoryId(3L)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New Integration Product")))
                .andExpect(jsonPath("$.price", is(79.99)))
                .andExpect(jsonPath("$.quantity", is(15)))
                .andExpect(header().exists("Location"));

        // Проверяем, что продукт действительно сохранен в БД
        assertThat(productRepository.count()).isEqualTo(2);
    }

    /**
     * Интеграционный тест обновления продукта.
     */
    @Test
    @DisplayName("Should update product - integration test")
    void updateProduct_IntegrationTest() throws Exception {
        ProductDto updateDto = ProductDto.builder()
                .name("Updated Integration Product")
                .description("Updated Integration Description")
                .price(BigDecimal.valueOf(89.99))
                .quantity(20)
                .categoryId(2L)
                .build();

        mockMvc.perform(put("/api/products/{id}", testProduct.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Integration Product")))
                .andExpect(jsonPath("$.price", is(89.99)))
                .andExpect(jsonPath("$.quantity", is(20)));

        // Проверяем, что продукт обновлен в БД
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("Updated Integration Product");
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(89.99));
    }

    /**
     * Интеграционный тест удаления продукта.
     */
    @Test
    @DisplayName("Should delete product - integration test")
    void deleteProduct_IntegrationTest() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", testProduct.getId()))
                .andExpect(status().isNoContent());

        // Проверяем, что продукт удален из БД
        assertThat(productRepository.existsById(testProduct.getId())).isFalse();
    }

    /**
     * Интеграционный тест валидации при создании продукта.
     */
    @Test
    @DisplayName("Should return validation errors for invalid product - integration test")
    void createProduct_WithInvalidData_IntegrationTest() throws Exception {
        ProductDto invalidProduct = ProductDto.builder()
                .name("")  // Пустое имя
                .price(BigDecimal.valueOf(-10))  // Отрицательная цена
                .quantity(-5)  // Отрицательное количество
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(3)));  // 3 ошибки валидации

        // Проверяем, что продукт не сохранен в БД
        assertThat(productRepository.count()).isEqualTo(1);
    }

    /**
     * Интеграционный тест получения продуктов по категории.
     */
    @Test
    @DisplayName("Should return products by category - integration test")
    void getProductsByCategory_IntegrationTest() throws Exception {
        // Создаем продукт в той же категории
        Product sameCategoryProduct = Product.builder()
                .name("Same Category Product")
                .description("Same Category Description")
                .price(BigDecimal.valueOf(59.99))
                .quantity(8)
                .categoryId(1L)  // Та же категория
                .build();

        productRepository.save(sameCategoryProduct);

        mockMvc.perform(get("/api/products/category/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryId", is(1)))
                .andExpect(jsonPath("$[1].categoryId", is(1)));
    }
}
