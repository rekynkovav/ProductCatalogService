package org.example.service.impl;

import org.example.service.SecurityService;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.UserService;

import java.util.Optional;

/**
 * Реализация конфигурации безопасности пользователей.
 * Обеспечивает аутентификацию, регистрацию и управление текущим пользователем.
 * Собирает метрики для мониторинга активности пользователей.
 * Реализует паттерн Singleton.
 */
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;
    private User thisUser;

    public SecurityServiceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * Возвращает текущего аутентифицированного пользователя.
     *
     * @return текущий пользователь или null если пользователь не аутентифицирован
     */
    public User getThisUser() {
        return thisUser;
    }

    /**
     * Устанавливает текущего аутентифицированного пользователя.
     *
     * @param thisUser объект пользователя
     */
    @Override
    public void setThisUser(User thisUser) {
        this.thisUser = thisUser;
    }

    /**
     * Проверяет учетные данные пользователя.
     * Собирает метрики времени аутентификации и количества входов.
     *
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @return true если аутентификация успешна, иначе false
     */
    @Override
    public boolean verificationUser(String userName, String password) {
        Optional<User> userOptional = userService.findByUsername(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                setThisUser(user);
                return true;
            }
        }
        System.out.println("Неверный логин или пароль");
        return false;

    }

    /**
     * Регистрирует нового пользователя в системе.
     * Собирает метрики регистрации пользователей.
     *
     * @param userName имя пользователя
     * @param password пароль пользователя
     * @param role     роль пользователя
     */
    @Override
    public void registerUser(String userName, String password, Role role) {
        User user = new User(userName, password, role);
        userService.saveUser(user);
        thisUser = user;
    }

    /**
     * Проверяет аутентифицирован ли текущий пользователь.
     *
     * @return true если пользователь аутентифицирован, иначе false
     */
    @Override
    public boolean isAuthenticated() {
        return thisUser != null;
    }

    /**
     * Выполняет выход пользователя из системы.
     * Собирает метрики выходов пользователей.
     */
    public void logout() {
        if (thisUser != null) {
            thisUser = null;
        }
    }

    /**
     * Сбрасывает авторизированного пользователя (для тестирования).
     */
    @Override
    public void reset() {
        this.thisUser = null;
    }
}