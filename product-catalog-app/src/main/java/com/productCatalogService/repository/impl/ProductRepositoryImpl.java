package com.productCatalogService.repository.impl;

import com.productCatalogService.entity.Product;
import com.productCatalogService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация репозитория товаров с использованием JDBC.
 * Обеспечивает взаимодействие с таблицей products в базе данных PostgreSQL.
 */
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL = """
            SELECT * FROM entity.products
            """;
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String SELECT_BY_CATEGORY_ID = SELECT_ALL + " WHERE category_id = ?";

    private static final String INSERT = """
        INSERT INTO entity.products (name, quantity, price, category_id)
        VALUES (?, ?, ?, ?)
        """;

    private static final String UPDATE = """
        UPDATE entity.products
        SET name = ?, quantity = ?, price = ?, category_id = ?
        WHERE id = ?
        """;

    private static final String DECREASE_QUANTITY_SQL = """
        UPDATE entity.products
        SET quantity = quantity - ?
        WHERE id = ? AND quantity >= ?
        """;

    private static final String INCREASE_QUANTITY_SQL = """
        UPDATE entity.products
        SET quantity = quantity + ?
        WHERE id = ?
        """;
    private static final String DELETE = "DELETE FROM entity.products WHERE id = ?";

    private static final String EXISTS_BY_ID = "SELECT COUNT(*) FROM entity.products WHERE id = ?";

    private static final String SELECT_PAGINATED = SELECT_ALL + " ORDER BY id LIMIT ? OFFSET ?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM entity.products";

    private static final String SELECT_ALL_BY_IDS = """
            SELECT * FROM entity.products
            WHERE id IN (%s)
            """;

    private final RowMapper<Product> productRowMapper = (resultSet, rowNum) -> {
        Product product = new Product();
        product.setId(resultSet.getLong("id"));
        product.setName(resultSet.getString("name"));
        product.setQuantity(resultSet.getInt("quantity"));
        product.setPrice(resultSet.getInt("price"));
        product.setCategoryId(resultSet.getLong("category_id"));
        return product;
    };

    /**
     * {@inheritDoc}
     * Метод Возвращает все товары
     */
    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query(SELECT_ALL, productRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Product> findById(Long id) {
        try {
            Product product = jdbcTemplate.queryForObject(SELECT_BY_ID, productRowMapper, id);
            return Optional.ofNullable(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     * Возвращает товары указанной категории отсортированные по идентификатору.
     */
    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return jdbcTemplate.query(SELECT_BY_CATEGORY_ID, productRowMapper, categoryId);
    }

    /**
     * {@inheritDoc}
     * Генерирует уникальный идентификатор для нового товара.
     */
    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT, new String[]{"id"});
                preparedStatement.setString(1, product.getName());
                preparedStatement.setInt(2, product.getQuantity());
                preparedStatement.setInt(3, product.getPrice());
                preparedStatement.setLong(4, product.getCategoryId());
                return preparedStatement;
            }, keyHolder);

            product.setId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(UPDATE,
                    product.getName(),
                    product.getQuantity(),
                    product.getPrice(),
                    product.getCategoryId(),
                    product.getId());
        }
        return product;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteById(Long id) {
        return jdbcTemplate.update(DELETE, id) > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_ID, Integer.class, id);
        return count != null && count > 0;    }

    @Override
    public boolean decreaseQuantity(Long productId, int quantity) {
        int rowsAffected = jdbcTemplate.update(DECREASE_QUANTITY_SQL, quantity, productId, quantity);
        return rowsAffected > 0;
    }

    @Override
    public boolean increaseQuantity(Long productId, int quantity) {
        int rowsAffected = jdbcTemplate.update(INCREASE_QUANTITY_SQL, quantity, productId);
        return rowsAffected > 0;
    }

    @Override
    public List<Product> findAllPaginated(int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(SELECT_PAGINATED, productRowMapper, size, offset);
    }

    @Override
    public Long count() {
        Long count = jdbcTemplate.queryForObject(COUNT_ALL, Long.class);
        return count != null ? count : 0;
    }

    @Override
    public List<Product> findAllById(Iterable<Long> ids) {
        if (ids == null) {
            throw new IllegalArgumentException("Список идентификаторов не может быть null");
        }

        // Преобразуем Iterable в List для удобства работы
        List<Long> idList = new ArrayList<>();
        ids.forEach(idList::add);

        if (idList.isEmpty()) {
            return Collections.emptyList();
        }

        // Создаем SQL запрос с параметрами IN (...)
        String sql = createInQuery(SELECT_ALL_BY_IDS, idList.size());

        // Выполняем запрос
        return jdbcTemplate.query(sql, productRowMapper, idList.toArray());
    }

    /**
     * Вспомогательный метод для создания SQL-запроса с оператором IN.
     *
     * @param baseQuery базовый SQL запрос с заполнителем для IN
     * @param paramCount количество параметров в IN
     * @return SQL запрос с нужным количеством плейсхолдеров
     */
    private String createInQuery(String baseQuery, int paramCount) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < paramCount; i++) {
            placeholders.append("?");
            if (i < paramCount - 1) {
                placeholders.append(",");
            }
        }
        return String.format(baseQuery, placeholders.toString());
    }
}