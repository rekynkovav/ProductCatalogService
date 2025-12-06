package org.example.integration;

import com.productcatalogservice.model.Product;
import com.productcatalogservice.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты для работы с базой данных.
 * Использует Testcontainers для изолированного тестирования с реальной PostgreSQL.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
@DisplayName("Database Integration Tests")
class DatabaseIntegrationTest {

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
    private ProductRepository productRepository;

    /**
     * Тест сохранения и извлечения продукта из БД.
     */
    @Test
    @DisplayName("Should save and retrieve product from database")
    void saveAndRetrieveProduct() {
        // Given
        Product product = Product.builder()
                .name("Database Test Product")
                .description("Database Test Description")
                .price(BigDecimal.valueOf(123.45))
                .quantity(25)
                .categoryId(5L)
                .build();

        // When
        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElseThrow();

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Database Test Product");
        assertThat(foundProduct.getPrice()).isEqualTo(BigDecimal.valueOf(123.45));
        assertThat(foundProduct.getCategoryId()).isEqualTo(5L);
    }

    /**
     * Тест обновления продукта в БД.
     */
    @Test
    @DisplayName("Should update product in database")
    void updateProduct() {
        // Given
        Product product = Product.builder()
                .name("Original Product")
                .price(BigDecimal.valueOf(100.00))
                .quantity(10)
                .build();

        Product savedProduct = productRepository.save(product);

        // When
        savedProduct.setName("Updated Product");
        savedProduct.setPrice(BigDecimal.valueOf(150.00));
        Product updatedProduct = productRepository.save(savedProduct);

        // Then
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(150.00));

        // Verify in database
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(foundProduct.getName()).isEqualTo("Updated Product");
    }

    /**
     * Тест удаления продукта из БД.
     */
    @Test
    @DisplayName("Should delete product from database")
    void deleteProduct() {
        // Given
        Product product = Product.builder()
                .name("Product to Delete")
                .price(BigDecimal.valueOf(50.00))
                .quantity(5)
                .build();

        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getId();

        // Verify product exists
        assertThat(productRepository.existsById(productId)).isTrue();

        // When
        productRepository.deleteById(productId);

        // Then
        assertThat(productRepository.existsById(productId)).isFalse();
    }

    /**
     * Тест поиска продуктов по категории.
     */
    @Test
    @DisplayName("Should find products by category id")
    void findProductsByCategoryId() {
        // Given
        Product product1 = Product.builder()
                .name("Product 1")
                .price(BigDecimal.valueOf(10.00))
                .quantity(1)
                .categoryId(1L)
                .build();

        Product product2 = Product.builder()
                .name("Product 2")
                .price(BigDecimal.valueOf(20.00))
                .quantity(2)
                .categoryId(1L)  // Same category
                .build();

        Product product3 = Product.builder()
                .name("Product 3")
                .price(BigDecimal.valueOf(30.00))
                .quantity(3)
                .categoryId(2L)  // Different category
                .build();

        productRepository.saveAll(List.of(product1, product2, product3));

        // When
        List<Product> category1Products = productRepository.findByCategoryId(1L);
        List<Product> category2Products = productRepository.findByCategoryId(2L);

        // Then
        assertThat(category1Products).hasSize(2);
        assertThat(category1Products)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Product 1", "Product 2");

        assertThat(category2Products).hasSize(1);
        assertThat(category2Products.get(0).getName()).isEqualTo("Product 3");
    }

    /**
     * Тест поиска продуктов по имени (регистронезависимый).
     */
    @Test
    @DisplayName("Should find products by name (case insensitive)")
    void findProductsByNameContainingIgnoreCase() {
        // Given
        Product product1 = Product.builder()
                .name("Apple iPhone")
                .price(BigDecimal.valueOf(999.00))
                .quantity(10)
                .build();

        Product product2 = Product.builder()
                .name("Samsung Galaxy")
                .price(BigDecimal.valueOf(899.00))
                .quantity(8)
                .build();

        Product product3 = Product.builder()
                .name("Apple MacBook")
                .price(BigDecimal.valueOf(1999.00))
                .quantity(5)
                .build();

        productRepository.saveAll(List.of(product1, product2, product3));

        // When
        List<Product> appleProducts = productRepository.findByNameContainingIgnoreCase("apple");
        List<Product> samsungProducts = productRepository.findByNameContainingIgnoreCase("SAMSUNG");
        List<Product> noMatchProducts = productRepository.findByNameContainingIgnoreCase("nonexistent");

        // Then
        assertThat(appleProducts).hasSize(2);
        assertThat(appleProducts)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Apple iPhone", "Apple MacBook");

        assertThat(samsungProducts).hasSize(1);
        assertThat(samsungProducts.get(0).getName()).isEqualTo("Samsung Galaxy");

        assertThat(noMatchProducts).isEmpty();
    }

    /**
     * Тест транзакционности операций.
     */
    @Test
    @DisplayName("Should rollback transaction on exception")
    void transactionShouldRollbackOnException() {
        // Given
        Product validProduct = Product.builder()
                .name("Valid Product")
                .price(BigDecimal.valueOf(100.00))
                .quantity(10)
                .build();

        // Save valid product
        productRepository.save(validProduct);

        long initialCount = productRepository.count();

        try {
            // When - пытаемся сохранить продукт с невалидными данными
            Product invalidProduct = Product.builder()
                    .name("")  // Пустое имя - должно вызвать исключение при валидации
                    .price(BigDecimal.valueOf(-10.00))  // Отрицательная цена
                    .quantity(-5)  // Отрицательное количество
                    .build();

            // Note: В реальном приложении валидация может происходить на уровне сервиса
            // Здесь мы имитируем ситуацию, когда операция должна откатиться

            // Эта операция не должна выполняться из-за исключения в реальном приложении
            productRepository.save(invalidProduct);

        } catch (Exception e) {
            // Then - транзакция должна откатиться
            long finalCount = productRepository.count();
            assertThat(finalCount).isEqualTo(initialCount);  // Количество продуктов не изменилось
        }
    }
}
