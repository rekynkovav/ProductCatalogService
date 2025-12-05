package org.example.testContainers;

import org.example.config.ConnectionManager;
import org.example.context.ApplicationContext;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryImplTest extends BaseDatabaseTest {

    private ProductRepositoryImpl productRepository = ApplicationContext.getInstance().getBean(ProductRepositoryImpl.class);
    private ConnectionManager connectionManager = ApplicationContext.getInstance().getBean(ConnectionManager.class);

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом
        cleanupDatabase();
    }

    @Test
    void testSaveProduct() {
        // Given
        Product product = createTestProduct("Laptop", 10, 999, Category.ELECTRONICS);

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Laptop");
        assertThat(savedProduct.getCategory()).isEqualTo(Category.ELECTRONICS);
    }

    @Test
    void testFindById() {
        // Given
        Product product = createTestProduct("Smartphone", 5, 499, Category.ELECTRONICS);
        Product savedProduct = productRepository.save(product);

        // When
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Smartphone");
    }

    @Test
    void testFindAll() {
        // Given
        productRepository.save(createTestProduct("Product1", 10, 100, Category.ELECTRONICS));
        productRepository.save(createTestProduct("Product2", 5, 50, Category.BOOKS));

        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).hasSize(2);
    }

    @Test
    void testFindByCategory() {
        // Given
        productRepository.save(createTestProduct("Laptop", 10, 999, Category.ELECTRONICS));
        productRepository.save(createTestProduct("Book", 20, 29, Category.BOOKS));

        // When
        List<Product> electronics = productRepository.findByCategory(Category.ELECTRONICS);

        // Then
        assertThat(electronics).hasSize(1);
        assertThat(electronics.get(0).getName()).isEqualTo("Laptop");
    }

    @Test
    void testDeleteById() {
        // Given
        Product product = createTestProduct("ToDelete", 5, 100, Category.OTHER);
        Product savedProduct = productRepository.save(product);

        // When
        productRepository.deleteById(savedProduct.getId());

        // Then
        assertThat(productRepository.findById(savedProduct.getId())).isEmpty();
    }

    @Test
    void testUpdateProduct() {
        // Given
        Product product = createTestProduct("OldName", 5, 100, Category.OTHER);
        Product savedProduct = productRepository.save(product);

        // When
        savedProduct.setName("NewName");
        savedProduct.setPrice(150);
        Product updatedProduct = productRepository.update(savedProduct);

        // Then
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("NewName");
        assertThat(foundProduct.get().getPrice()).isEqualTo(150);
    }

    private Product createTestProduct(String name, int quantity, int price, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setQuantity(quantity);
        product.setPrice(price);
        product.setCategory(category);
        return product;
    }

    /**
     * Метод для очистки всех таблиц в базе данных с использованием TRUNCATE CASCADE для PostgreSQL
     */
    private void cleanupDatabase() {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {

            // Очищаем таблицы в правильном порядке (сначала зависимые, потом основные)
            statement.execute("TRUNCATE TABLE entity.user_metrics CASCADE");
            statement.execute("TRUNCATE TABLE entity.users CASCADE");
            statement.execute("TRUNCATE TABLE entity.products CASCADE");

            // Сбрасываем последовательности
            statement.execute("ALTER SEQUENCE service.user_id_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.user_metrics_seq RESTART WITH 1");
            statement.execute("ALTER SEQUENCE service.product_id_seq RESTART WITH 1");

        } catch (SQLException e) {
            // Если TRUNCATE не работает, используем DELETE
            try (Connection connection = connectionManager.getConnection();
                 Statement statement = connection.createStatement()) {

                statement.execute("DELETE FROM entity.user_metrics");
                statement.execute("DELETE FROM entity.users");
                statement.execute("DELETE FROM entity.products");
                statement.execute("ALTER SEQUENCE service.user_id_seq RESTART WITH 1");
                statement.execute("ALTER SEQUENCE service.user_metrics_seq RESTART WITH 1");
                statement.execute("ALTER SEQUENCE service.product_id_seq RESTART WITH 1");

            } catch (SQLException ex) {
                throw new RuntimeException("Failed to cleanup database", ex);
            }
        }
    }
}