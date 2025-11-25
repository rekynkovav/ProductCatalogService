package org.example.repository.impl;

import org.example.config.ConnectionManager;
import org.example.config.MetricsConfig;
import org.example.model.entity.Category;
import org.example.model.entity.Product;
import org.example.repository.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория товаров с использованием JDBC.
 * Обеспечивает взаимодействие с таблицей products в базе данных PostgreSQL.
 * Реализует паттерн Singleton.
 */
public class ProductRepositoryImpl implements ProductRepository {

    /**
     * Единственный экземпляр репозитория товаров.
     */
    private static ProductRepositoryImpl productRepository;

    /**
     * Возвращает единственный экземпляр репозитория товаров.
     *
     * @return экземпляр ProductRepositoryImpl
     */
    public static synchronized ProductRepositoryImpl getInstance() {
        if (productRepository == null) {
            productRepository = new ProductRepositoryImpl();
        }
        return productRepository;
    }

    /**
     * Приватный конструктор для реализации паттерна Singleton.
     */
    private ProductRepositoryImpl() {
    }

    /**
     * {@inheritDoc}
     * Генерирует уникальный идентификатор для нового товара.
     */
    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO entity.products (name, quantity, price, category) VALUES (?, ?, ?, ?)";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setInt(2, product.getQuantity());
            preparedStatement.setInt(3, product.getPrice());
            preparedStatement.setString(4, product.getCategory().name());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving product", e);
        }
        return product;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM entity.products WHERE id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapResultSetToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by id", e);
        }

        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     * Возвращает товары отсортированные по идентификатору.
     */
    @Override
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM entity.products ORDER BY id";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
        return products;
    }

    /**
     * {@inheritDoc}
     * Возвращает товары указанной категории отсортированные по идентификатору.
     */
    @Override
    public List<Product> findByCategory(Category category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM entity.products WHERE category = ? ORDER BY id";
        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category.name());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }
        return products;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM entity.products WHERE id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product by id", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Product update(Product product) {
        String sql = "UPDATE entity.products SET name = ?, quantity = ?, price = ?, category = ? WHERE id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setInt(2, product.getQuantity());
            preparedStatement.setInt(3, product.getPrice());
            preparedStatement.setString(4, product.getCategory().name());
            preparedStatement.setLong(5, product.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }
        return product;
    }

    @Override
    public void removeBasket(Long userId, Long productId) {
        String sql = "DELETE FROM entity.user_basket WHERE user_id = ? AND product_id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, productId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error removing product from basket", e);
        }
    }

    @Override
    public List<Product> findByName(String nameProduct) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM entity.products WHERE name = ? ORDER BY id";
        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nameProduct);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                products.add(mapResultSetToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by name", e);
        }
        return products;
    }

    /**
     * Преобразует ResultSet в объект Product.
     *
     * @param resultSet ResultSet с данными товара
     * @return объект Product с заполненными полями
     * @throws SQLException если произошла ошибка при чтении данных из ResultSet
     */
    Product mapResultSetToProduct(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getLong("id"));
        product.setName(resultSet.getString("name"));
        product.setQuantity(resultSet.getInt("quantity"));
        product.setPrice(resultSet.getInt("price"));
        product.setCategory(Category.valueOf(resultSet.getString("category")));
        return product;
    }

    /**
     * удаляет все записи в таблице products
     */
    @Override
    public void deleteAllProducts() {
        String sql = "DELETE FROM entity.products";
        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all products", e);
        }
    }
}