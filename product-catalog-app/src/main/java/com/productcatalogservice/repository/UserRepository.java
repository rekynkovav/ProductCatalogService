package com.productcatalogservice.repository;

import com.productcatalogservice.entity.Product;
import com.productcatalogservice.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с пользователями.
 * Определяет методы для управления пользователями, их корзинами и статистикой активности.
 */
public interface UserRepository {

    /**
     * Сохраняет пользователя в базе данных.
     *
     * @param user пользователь для сохранения
     * @return сохраненный пользователь с присвоенным идентификатором
     */
    User save(User user);

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional с найденным пользователем или empty если пользователь не найден
     */
    Optional<User> findById(Long id);

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional с найденным пользователем или empty если пользователь не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Удаляет пользователя по идентификатору.
     * Перед удалением очищает корзину пользователя.
     *
     * @param id идентификатор пользователя для удаления
     */
    void deleteById(Long id);

    /**
     * Проверяет существование пользователя с указанным именем.
     *
     * @param username имя пользователя для проверки
     * @return true если пользователь существует, иначе false
     */
    boolean existsByUsername(String username);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей отсортированный по идентификатору
     */
    List<User> findAllUser();

    /**
     * Добавляет продукт в корзину пользователя или обновляет количество
     */
    void addToBasket(Long userId, Long productId, int quantity);

    /**
     * Удаляет товар из корзины пользователя.
     *
     * @param userId    идентификатор пользователя
     * @param productId идентификатор товара
     */
    void removeFromBasket(Long userId, Long productId);

    /**
     * Очищает корзину пользователя.
     *
     * @param userId идентификатор пользователя
     */
    void clearBasket(Long userId);

    /**
     * Возвращает корзину пользователя.
     *
     * @param userId идентификатор пользователя
     * @return карта товаров в корзине (ключ - ID товара, значение - товар)
     */
    Map<Long, Product> getBasket(Long userId);
}