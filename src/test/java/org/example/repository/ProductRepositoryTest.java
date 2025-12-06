package org.example.repository;

import org.example.model.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Data JPA тесты для ProductRepository.
 * Использует встроенную базу данных H2 для тестирования операций с БД.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see ProductRepository
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;
    private Product anotherProduct;

    /**
     * Настройка тестовых данных перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        entityManager.clear();

        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .quantity(10)
                .categoryId(1L)
                .build();

        anotherProduct = Product.builder()
                .name("Another Product")
                .description("Another Description")
                .price(BigDecimal.valueOf(49.99))
                .quantity(5)
                .categoryId(2L)
                .build();

        // Сохраняем продукты в базе
        entityManager.persist(testProduct);
        entityManager.persist(anotherProduct);
        entityManager.flush();
    }

    /**
     * Тест сохранения продукта.
     */
    @Test
    @DisplayName("Should save product and generate id")
    void save_ShouldPersistProductAndGenerateId() {
        // Given
        Product newProduct = Product.builder()
                .name("New Product")
                .description("New Description")
                .price(BigDecimal.valueOf(79.99))
                .quantity(15)
                .categoryId(3L)
                .build();

        // When
        Product savedProduct = productRepository.save(newProduct);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("New Product");
        assertThat(savedProduct.getPrice()).isEqualTo(BigDecimal.valueOf(79.99));

        // Проверяем, что продукт действительно сохранен в БД
        Product foundProduct = entityManager.find(Product.class, savedProduct.getId());
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("New Product");
    }

    /**
     * Тест поиска продукта по ID.
     */
    @Test
    @DisplayName("Should find product by id when product exists")
    void findById_WithExistingId_ShouldReturnProduct() {
        // When
        Optional<Product> foundProduct = productRepository.findById(testProduct.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
        assertThat(foundProduct.get().getDescription()).isEqualTo("Test Description");
    }

    /**
     * Тест поиска продукта по несуществующему ID.
     */
    @Test
    @DisplayName("Should return empty optional when product not found")
    void findById_WithNonExistingId_ShouldReturnEmptyOptional() {
        // When
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Then
        assertThat(foundProduct).isEmpty();
    }

    /**
     * Тест получения всех продуктов.
     */
    @Test
    @DisplayName("Should return all products")
    void findAll_ShouldReturnAllProducts() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).isNotNull();
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName)
                .containsExactlyInAnyOrder("Test Product
