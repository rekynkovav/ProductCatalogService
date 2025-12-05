package org.example.testContainers;

import org.example.config.ConnectionManager;
import org.example.context.ApplicationContext;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BasketIntegrationTest extends BaseDatabaseTest {

    private ProductServiceImpl productService = ApplicationContext.getInstance().getBean(ProductServiceImpl.class);
    private UserServiceImpl userService = ApplicationContext.getInstance().getBean(UserServiceImpl.class);
    private ConnectionManager connectionManager = ApplicationContext.getInstance().getBean(ConnectionManager.class);
    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        cleanDatabase(); // Сначала очищаем базу
        testUser = createTestUser();
        userService.saveUser(testUser);

        testProduct = createTestProduct();
        productService.saveProduct(testProduct);
    }

    void cleanDatabase() {
        try (Connection connection = connectionManager.getConnection()) {
            connection.setAutoCommit(false); // Начинаем транзакцию

            try {
                // Отключаем проверку внешних ключей временно
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("SET CONSTRAINTS ALL DEFERRED");
                }

                // Очищаем таблицы в правильном порядке (сначала дочерние, потом родительские)
                String[] tables = {
                        "user_basket", // обновлено на user_basket
                        "user_metrics",
                        "products",
                        "users"
                };

                for (String table : tables) {
                    String sql = "DELETE FROM entity." + table;
                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        int deletedRows = stmt.executeUpdate();
                        System.out.println("Deleted " + deletedRows + " rows from " + table);
                    }
                }

                // Коммитим изменения
                connection.commit();

            } catch (SQLException e) {
                connection.rollback(); // Откатываем при ошибке
                throw e;
            } finally {
                connection.setAutoCommit(true); // Возвращаем авто-коммит
            }

            // Включаем проверку обратно
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SET CONSTRAINTS ALL IMMEDIATE");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error cleaning database", e);
        }
    }

    @Test
    void testAddToBasket() {
        // When
        productService.addBasket(testUser.getId(), testProduct.getId(), 2);

        // Then
        Map<Long, Product> basket = userService.getUserBasket(testUser.getId());
        assertThat(basket).hasSize(1);

        Product productInBasket = basket.get(testProduct.getId());
        assertThat(productInBasket.getQuantity()).isEqualTo(2); // Проверяем quantity в Product
    }

    @Test
    void testGetUserBasket() {
        // Given
        productService.addBasket(testUser.getId(), testProduct.getId(), 1);

        // When
        Map<Long, Product> basket = userService.getUserBasket(testUser.getId());

        // Then
        assertThat(basket).isNotEmpty();
        assertThat(basket.containsKey(testProduct.getId())).isTrue(); // Проверяем по ID

        Product productInBasket = basket.get(testProduct.getId());
        assertThat(productInBasket.getQuantity()).isEqualTo(1); // Берем quantity из Product
    }

    @Test
    void testRemoveFromBasket() {
        // Given
        productService.addBasket(testUser.getId(), testProduct.getId(), 3);

        // When
        productService.removeBasket(testUser.getId(), testProduct.getId());

        // Then
        Map<Long, Product> basket = userService.getUserBasket(testUser.getId());
        assertThat(basket).isEmpty();
    }

    @Test
    void testUpdateBasketQuantity() {
        // Given
        productService.addBasket(testUser.getId(), testProduct.getId(), 1);

        // When
        productService.addBasket(testUser.getId(), testProduct.getId(), 2); // Обновляем количество

        // Then
        Map<Long, Product> basket = userService.getUserBasket(testUser.getId());
        Product productInBasket = basket.get(testProduct.getId());
        assertThat(productInBasket.getQuantity()).isEqualTo(2); // Проверяем quantity в Product
    }

    @Test
    void testClearUserBasket() {
        // Given
        productService.addBasket(testUser.getId(), testProduct.getId(), 1);

        // Create another product
        Product anotherProduct = createTestProduct();
        productService.saveProduct(anotherProduct);
        productService.addBasket(testUser.getId(), anotherProduct.getId(), 2);

        // When
        userService.clearUserBasket(testUser.getId());

        // Then
        Map<Long, Product> basket = userService.getUserBasket(testUser.getId());
        assertThat(basket).isEmpty();
    }

    private User createTestUser() {
        User user = new User();
        user.setUserName("testuser_" + UUID.randomUUID().toString().substring(0, 8));
        user.setPassword("password");
        user.setRole(Role.USER);
        return user;
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setName("Test Product_" + UUID.randomUUID().toString().substring(0, 8));
        product.setQuantity(10);
        product.setPrice(100);
        product.setCategory(Category.ELECTRONICS);
        return product;
    }
}