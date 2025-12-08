package com.productCatalogService.repository.impl;

import com.productCatalogService.entity.Product;
import com.productCatalogService.entity.Role;
import com.productCatalogService.entity.User;
import com.productCatalogService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Реализация репозитория пользователей с использованием JDBC.
 * Обеспечивает хранение и управление данными пользователей в PostgreSQL.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String SAVE_SQL = """
            INSERT INTO entity.users (user_name, password, role)
            VALUES (?, ?, ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE entity.users
            SET user_name = ?, password = ?, role = ?
            WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = "SELECT * FROM entity.users WHERE id = ?";
    private static final String FIND_BY_USERNAME_SQL = "SELECT * FROM entity.users WHERE user_name = ?";
    private static final String EXISTS_BY_USERNAME_SQL = "SELECT COUNT(*) FROM entity.users WHERE user_name = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM entity.users ORDER BY id";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM entity.users WHERE id = ?";

    private static final String GET_BASKET_SQL = """
            SELECT p.*, ub.quantity as basket_quantity FROM entity.user_basket ub
            JOIN entity.products p ON ub.product_id = p.id 
            WHERE ub.user_id = ?
            """;

    private static final String ADD_TO_BASKET_SQL = """
            INSERT INTO entity.user_basket (user_id, product_id, quantity)
            VALUES (?, ?, ?) 
            ON CONFLICT (user_id, product_id) 
            DO UPDATE SET quantity = EXCLUDED.quantity, added_at = CURRENT_TIMESTAMP
            """;

    private static final String REMOVE_FROM_BASKET_SQL = "DELETE FROM entity.user_basket WHERE user_id = ? AND product_id = ?";
    private static final String CLEAR_BASKET_SQL = "DELETE FROM entity.user_basket WHERE user_id = ?";

    private static final String GET_BASKET_QUANTITIES_SQL = """
            SELECT product_id, quantity FROM entity.user_basket 
            WHERE user_id = ?
            """;

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setUserName(resultSet.getString("user_name"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        return user;
    };

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUserName());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRole().name());
                return ps;
            }, keyHolder);

            user.setId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(UPDATE_SQL,
                    user.getUserName(),
                    user.getPassword(),
                    user.getRole().name(),
                    user.getId());
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        List<User> users = jdbcTemplate.query(FIND_BY_ID_SQL, userRowMapper, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }

        User user = users.get(0);
        user.setBasket(getBasketQuantities(user.getId()));
        return Optional.of(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        List<User> users = jdbcTemplate.query(FIND_BY_USERNAME_SQL, userRowMapper, username);
        if (users.isEmpty()) {
            return Optional.empty();
        }

        User user = users.get(0);
        user.setBasket(getBasketQuantities(user.getId()));
        return Optional.of(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_USERNAME_SQL, Integer.class, username);
        return count != null && count > 0;
    }

    @Override
    public List<User> findAllUser() {
        return jdbcTemplate.query(FIND_ALL_SQL, userRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        clearBasket(id);
        jdbcTemplate.update(DELETE_BY_ID_SQL, id);
    }

    @Override
    public Map<Long, Product> getBasket(Long userId) {
        List<Product> products = jdbcTemplate.query(
                GET_BASKET_SQL,
                (rs, rowNum) -> {
                    Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setName(rs.getString("name"));
                    product.setQuantity(rs.getInt("basket_quantity"));
                    product.setPrice(rs.getInt("price"));
                    product.setCategoryId(rs.getLong("category_id"));
                    return product;
                },
                userId
        );

        return products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    @Override
    public void addToBasket(Long userId, Long productId, int quantity) {
        jdbcTemplate.update(ADD_TO_BASKET_SQL, userId, productId, quantity);
    }

    @Override
    public void removeFromBasket(Long userId, Long productId) {
        jdbcTemplate.update(REMOVE_FROM_BASKET_SQL, userId, productId);
    }

    @Override
    public void clearBasket(Long userId) {
        jdbcTemplate.update(CLEAR_BASKET_SQL, userId);
    }

    /**
     * Получает корзину пользователя в формате Map<Long, Integer> (ID товара -> количество)
     * для заполнения поля basket в объекте User
     */
    private Map<Long, Integer> getBasketQuantities(Long userId) {
        return jdbcTemplate.query(
                        GET_BASKET_QUANTITIES_SQL,
                        (rs, rowNum) -> {
                            Map<Long, Integer> item = new HashMap<>();
                            item.put(rs.getLong("product_id"), rs.getInt("quantity"));
                            return item;
                        },
                        userId
                ).stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}