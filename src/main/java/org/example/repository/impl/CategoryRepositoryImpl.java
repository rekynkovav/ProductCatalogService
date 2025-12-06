package org.example.repository.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.entity.Category;
import org.example.repository.CategoryRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_ALL = """
        SELECT id, name
        FROM entity.categories
        """;

    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE id = ?";

    private static final String SELECT_BY_NAME = SELECT_ALL + " WHERE name = ?";

    private static final String INSERT = """
        INSERT INTO entity.categories (name)
        VALUES (?)
        """;

    private static final String UPDATE = """
        UPDATE entity.categories
        SET name = ?
        WHERE id = ?
        """;

    private static final String DELETE = "DELETE FROM entity.categories WHERE id = ?";

    private static final String EXISTS_BY_ID = "SELECT COUNT(*) FROM entity.categories WHERE id = ?";

    private final RowMapper<Category> categoryRowMapper = (resultSet, rowNum) -> {
        Category category = new Category();
        category.setId(resultSet.getLong("id"));
        category.setName(resultSet.getString("name"));
        return category;
    };

    @Override
    public List<Category> findAll() {
        return jdbcTemplate.query(SELECT_ALL, categoryRowMapper);
    }

    @Override
    public Optional<Category> findById(Long id) {
        try {
            Category category = jdbcTemplate.queryForObject(SELECT_BY_ID, categoryRowMapper, id);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        try {
            Category category = jdbcTemplate.queryForObject(SELECT_BY_NAME, categoryRowMapper, name);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT, new String[]{"id"});
                preparedStatement.setString(1, category.getName());
                return preparedStatement;
            }, keyHolder);

            category.setId(keyHolder.getKey().longValue());
        } else {
            jdbcTemplate.update(UPDATE,
                    category.getName(),
                    category.getId());
        }
        return category;
    }

    @Override
    public boolean deleteById(Long id) {
        int rowsAffected = jdbcTemplate.update(DELETE, id);
        return rowsAffected > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_ID, Integer.class, id);
        return count != null && count > 0;
    }
}