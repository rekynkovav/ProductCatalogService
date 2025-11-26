package org.example.service.impl;

import org.example.config.MetricsConfig;
import org.example.service.MetricsService;
import org.example.service.SecurityService;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.service.UserService;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Реализация конфигурации безопасности пользователей.
 * Обеспечивает аутентификацию, регистрацию и управление текущим пользователем.
 * Собирает метрики для мониторинга активности пользователей.
 * Реализует паттерн Singleton.
 */
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;
    private MetricsConfig metricsConfig;
    private MetricsService metricsService;
    private User thisUser;

    public SecurityServiceImpl(UserService userService, MetricsConfig metricsConfig, MetricsService metricsService) {
        this.userService = userService;
        this.metricsConfig = metricsConfig;
        this.metricsService = metricsService;
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
        long startTime = System.currentTimeMillis();

        try {
            Optional<User> userOptional = userService.findByUsername(userName);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (user.getPassword().equals(password)) {
                    setThisUser(user);

                    // Сохраняем метрики в БД
                    metricsService.incrementMetric(user.getId(), "LOGIN_COUNT");
                    metricsConfig.incrementActiveUsers();

                    // Micrometer метрики
                    metricsConfig.getUserLoginCounter().increment();

                    return true;
                }
            }
            System.out.println("Неверный логин или пароль");
            return false;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            metricsConfig.getUserAuthenticationTimer().record(duration, TimeUnit.MILLISECONDS);
        }
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
        metricsConfig.getUserRegistrationCounter().increment();
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
            metricsConfig.incrementUserMetric(thisUser.getId(), "LOGOUT_COUNT");
            metricsConfig.getUserLogoutCounter().increment();
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