package org.example.repository.impl;

import org.example.config.ConnectionManager;
import org.example.config.MetricsConfig;
import org.example.model.entity.Product;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория пользователей с использованием JDBC.
 * Обеспечивает хранение и управление данными пользователей в PostgreSQL.
 * Реализует паттерн Singleton.
 */
public class UserRepositoryImpl implements UserRepository {

    /**
     * Единственный экземпляр репозитория пользователей.
     */
    private static UserRepositoryImpl instance;

    /**
     * Репозиторий продуктов для работы с корзиной пользователя.
     */
    private ProductRepositoryImpl productRepository;

    /**
     * Возвращает единственный экземпляр репозитория пользователей.
     *
     * @return экземпляр UserRepositoryImpl
     */
    public static synchronized UserRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }
        return instance;
    }

    /**
     * Приватный конструктор для реализации паттерна Singleton.
     * Инициализирует зависимость от репозитория продуктов.
     */
    private UserRepositoryImpl() {
        productRepository = ProductRepositoryImpl.getInstance();
    }

    /**
     * {@inheritDoc}
     * Генерирует уникальный идентификатор для нового пользователя.
     */
    @Override
    public User save(User user) {
        String sql = "INSERT INTO entity.users (user_name, password, role) VALUES (?, ?, ?)";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUserName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getRole().name());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error saving user", e);
        }
        return user;
    }

    /**
     * {@inheritDoc}
     * Загружает корзину пользователя вместе с основными данными.
     */
    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM entity.users WHERE id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                user.setMapBasket(getBasket(user.getId()));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error finding user by id", e);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     * Загружает корзину пользователя вместе с основными данными.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM entity.users WHERE user_name = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = mapResultSetToUser(resultSet);
                user.setMapBasket(getBasket(user.getId()));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error finding user by username", e);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM entity.users WHERE user_name = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error checking user existence", e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * Возвращает пользователей отсортированных по идентификатору.
     */
    @Override
    public List<User> findAllUser() {
        List<User> listUser = new ArrayList<>();
        String sql = "SELECT * FROM entity.users ORDER BY id";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                listUser.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error finding all products", e);
        }
        return listUser;
    }

    /**
     * {@inheritDoc}
     * Перед удалением пользователя очищает его корзину.
     */
    @Override
    public void deleteById(Long id) {
        clearBasket(id);
        String sql = "DELETE FROM entity.users WHERE id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error deleting user", e);
        }
    }

    /**
     * Загружает корзину пользователя из базы данных.
     *
     * @param userId идентификатор пользователя
     * @return карта товаров в корзине (ключ - ID товара, значение - товар с количеством)
     * @throws RuntimeException если произошла ошибка при загрузке корзины
     */
    @Override
    public HashMap<Long, Product> getBasket(Long userId) {
        HashMap<Long, Product> basket = new HashMap<>();

        String sql = "SELECT p.*, ub.quantity as basket_quantity FROM entity.user_basket ub " +
                     "JOIN entity.products p ON ub.product_id = p.id " +
                     "WHERE ub.user_id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = productRepository.mapResultSetToProduct(resultSet);
                product.setQuantity(resultSet.getInt("basket_quantity"));
                basket.put(product.getId(), product);
            }
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error loading basket from database", e);
        }
        return basket;
    }

    @Override
    public void deleteAllUser() {
        String sql = "DELETE FROM entity.users";
        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all users", e);
        }
    }

    /**
     * {@inheritDoc}
     * Если товар уже есть в корзине, увеличивает его количество.
     */
    @Override
    public void addToBasket(Long userId, Long productId, int quantity) {
        String sql =
                "INSERT INTO entity.user_basket (user_id, product_id, quantity) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (user_id, product_id) " +
                "DO UPDATE SET quantity = EXCLUDED.quantity, added_at = CURRENT_TIMESTAMP";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, userId);
            preparedStatement.setLong(2, productId);
            preparedStatement.setInt(3, quantity);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error adding product to basket", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromBasket(Long userId, Long productId) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearBasket(Long userId) {
        String sql = "DELETE FROM entity.user_basket WHERE user_id = ?";

        try (Connection connection = ConnectionManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            MetricsConfig.getInstance().getDatabaseErrorCounter().increment();
            throw new RuntimeException("Error clearing basket", e);
        }
    }

    /**
     * Преобразует ResultSet в объект User.
     * Не загружает корзину пользователя.
     *
     * @param resultSet ResultSet с данными пользователя
     * @return объект User с заполненными полями
     * @throws SQLException если произошла ошибка при чтении данных из ResultSet
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUserName(resultSet.getString("user_name"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        return user;
    }



}