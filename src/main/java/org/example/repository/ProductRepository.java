package org.example.repository;

import org.example.model.entity.Category;
import org.example.model.entity.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с товарами.
 * Определяет основные CRUD-операции и специализированные методы для работы с продуктами.
 */
public interface ProductRepository {

    /**
     * Сохраняет товар в базе данных.
     *
     * @param product товар для сохранения
     * @return сохраненный товар с присвоенным идентификатором
     */
    Product save(Product product);

    /**
     * Находит товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return Optional с найденным товаром или empty если товар не найден
     */
    Optional<Product> findById(Long id);

    /**
     * Возвращает список всех товаров.
     *
     * @return список всех товаров в базе данных
     */
    List<Product> findAll();
    List<Product> findAll(int page);

    /**
     * Находит товары по категории.
     *
     * @param category категория для поиска
     * @return список товаров указанной категории
     */
    List<Product> findByCategory(Category category);

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара для удаления
     */
    boolean deleteById(Long id);

    /**
     * Обновляет информацию о товаре.
     *
     * @param product товар с обновленными данными
     * @return обновленный товар
     */
    Product update(Product product);

    /**
     * Удаляет продукт из корзины пользователя
     *
     * @param userId    идентификатор пользователя
     * @param productId идентификатор продукта для удаления из корзины
     * @throws RuntimeException если произошла ошибка при удалении из базы данных
     */
    void removeBasket(Long userId, Long productId);

    /**
     * Ищет товар по имени
     */
    List<Product> findByName(String nameProduct);

    /**
     * Удаляет все товары
     */
    void deleteAllProducts();

    /**
     * Преобразует ResultSet в объект Product.
     *
     * @param resultSet ResultSet с данными товара
     * @return объект Product с заполненными полями
     * @throws SQLException если произошла ошибка при чтении данных из ResultSet
     */
    Product mapResultSetToProduct(ResultSet resultSet) throws SQLException;

    /**
     * Возвращает общее количество товаров в базе данных.
     */
    int getTotalProductsCount();
}