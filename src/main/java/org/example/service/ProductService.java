package org.example.service;

import org.example.model.entity.Category;
import org.example.model.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для работы с товарами.
 * Определяет бизнес-логику управления товарами магазина.
 */
public interface ProductService {

    /**
     * Сохраняет новый товар в магазине.
     *
     * @param product товар для сохранения
     * @return
     */
    Product saveProduct(Product product);

    /**
     * Обновляет информацию о существующем товаре.
     *
     * @param id       идентификатор товара
     * @param name     новое название товара
     * @param quantity новое количество товара
     * @param price    новая цена товара
     * @param category новая категория товара
     * @return
     */
    Product updateProduct(Product product);

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара для удаления
     */
    boolean deleteProductById(long id);

    /**
     * Отображает все товары в магазине.
     * Выводит список в консоль.
     */
    List<Product> getAllProduct();

    /**
     * Ищет товары по категории и отображает результаты.
     *
     * @param category категория для поиска
     */
    void searchCategory(Category category);

    /**
     * Добавляет товар в корзину пользователя.
     * Проверяет наличие достаточного количества товара.
     *
     * @param userId    идентификатор пользователя
     * @param productId идентификатор товара
     * @param quantity  количество товара для добавления
     */
    void addBasket(long userId, long productId, int quantity);

    /**
     * Находит товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return Optional с найденным товаром или empty если товар не найден
     */
    Optional<Product> findById(Long id);

    /**
     * Удаляет продукт из корзины пользователя
     *
     * @param userId    идентификатор пользователя
     * @param productId идентификатор продукта для удаления из корзины
     * @throws RuntimeException если произошла ошибка при удалении из базы данных
     */
    void removeBasket(Long userId, Long productId);

    /**
     * Находит товар по имени.
     *
     * @param nameProduct идентификатор товара
     * @return Optional с найденным товаром или empty если товар не найден
     */
    List<Product> findByName(String nameProduct);

    List<Product> getAllProduct(int page);
    int getTotalPages();
}