package org.example.service.impl;

import org.example.model.entity.Product;
import org.example.model.entity.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервисный класс для управления пользователями.
 * Обеспечивает бизнес-логику работы с пользователями и их корзинами.
 * Реализует паттерн Singleton.
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Сохраняет пользователя в системе.
     *
     * @param user объект пользователя для сохранения
     */
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    /**
     * Проверяет существование пользователя с указанным именем.
     *
     * @param userName имя пользователя для проверки
     * @return true если пользователь существует, false в противном случае
     */
    @Override
    public boolean isContainsUser(String userName) {
        return userRepository.existsByUsername(userName);
    }

    /**
     * Находит пользователя по имени пользователя.
     *
     * @param username имя пользователя для поиска
     * @return Optional с найденным пользователем или пустой Optional если пользователь не найден
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Получает корзину пользователя.
     *
     * @param userId идентификатор пользователя
     * @return Map товаров в корзине пользователя, где ключ - ID товара, значение - объект товара
     */
    @Override
    public Map<Long, Product> getUserBasket(Long userId) {
        return userRepository.getBasket(userId);
    }

    /**
     * Показывает всех пользователей системы.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> showAllUser() {
        return userRepository.findAllUser();
    }

    @Override
    public void clearUserBasket(Long userId) {
        userRepository.clearBasket(userId);
    }
}
