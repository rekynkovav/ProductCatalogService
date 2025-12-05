package org.example.service;

import org.example.model.entity.Product;
import org.example.model.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервисный интерфейс для управления пользователями
 * Определяет контракт для работы с пользователями, их корзинами и статистикой активности
 */

public interface UserService {
    /**
     * Сохраняет пользователя в системе
     * @param user объект пользователя для сохранения
     */
    User saveUser(User user);

    /**
     * Проверяет существование пользователя с указанным именем
     * @param userName имя пользователя для проверки
     * @return true если пользователь существует, false в противном случае
     */
    boolean isContainsUser(String userName);

    /**
     * Находит пользователя по имени пользователя
     * @param username имя пользователя для поиска
     * @return Optional с найденным пользователем или пустой Optional если пользователь не найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Получает корзину пользователя
     * @param userId идентификатор пользователя
     * @return Map товаров в корзине пользователя, где ключ - ID товара, значение - объект товара
     */
    Map<Long, Product> getUserBasket(Long userId);

    /**
     * Показывает всех пользователей системы
     * @return список всех пользователей
     */
    List<User> showAllUser();

    /**
     * Очищает всю корзину пользователя
     */
    void clearUserBasket(Long userId);
    /**
     * Добавление товара в корзину
     */
    void addToBasket(Long userId, Long productId, int quantity);
    /**
     * Удаления товара из корзины
     */
    void removeFromBasket(Long userId, Long productId);
}