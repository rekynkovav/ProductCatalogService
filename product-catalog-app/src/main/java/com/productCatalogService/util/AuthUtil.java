package com.productCatalogService.util;

import com.productCatalogService.entity.Role;
import com.productCatalogService.entity.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Утилитарный класс для работы с аутентификацией и управлением сессиями пользователей.
 * Предоставляет методы для генерации, валидации и управления токенами авторизации,
 * а также для хранения активных пользовательских сессий в памяти приложения.
 *
 * <p>Основные функции класса:</p>
 * <ul>
 *   <li>Генерация токенов авторизации на основе учетных данных пользователя</li>
 *   <li>Хранение активных сессий пользователей в памяти</li>
 *   <li>Извлечение и валидация токенов из заголовков запросов</li>
 *   <li>Проверка ролей пользователей для контроля доступа</li>
 *   <li>Управление жизненным циклом сессий</li>
 * </ul>
 *
 * <p>Токены генерируются в формате Base64 и могут быть двух типов:</p>
 * <ol>
 *   <li>С использованием имени пользователя и пароля (для аутентификации)</li>
 *   <li>С использованием имени пользователя и временной метки (для обновления токенов)</li>
 * </ol>
 *
 * <p><strong>Внимание:</strong> Данная реализация хранит сессии в памяти приложения,
 * что может быть неэффективно для распределенных систем. Для production-среды
 * рекомендуется использовать распределенное хранилище сессий (например, Redis).</p>
 *
 * @see User
 * @see Role
 * @since 1.0
 */
@Component
public class AuthUtil {

    @Getter
    private final Map<String, User> activeSessions = new HashMap<>();

    /**
     * Генерирует токен авторизации на основе имени пользователя и пароля.
     * Формирует строку "username:password" и кодирует её в Base64.
     *
     * @param username имя пользователя (не должно быть null или пустым)
     * @param password пароль пользователя (не должно быть null или пустым)
     * @return закодированный в Base64 токен авторизации
     * @throws IllegalArgumentException если username или password равны null
     * @throws NullPointerException если username или password равны null
     */
    public String generateToken(String username, String password) {
        String credentials = username + ":" + password;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    /**
     * Генерирует токен только по имени пользователя с добавлением временной метки.
     * Используется для обновления токенов без необходимости повторной аутентификации.
     * Формирует строку "username:timestamp" и кодирует её в Base64.
     *
     * @param username имя пользователя (не должно быть null или пустым)
     * @return закодированный в Base64 токен с временной меткой
     * @throws IllegalArgumentException если username равен null или пуст
     */
    public String generateToken(String username) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String data = username + ":" + timestamp;
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    /**
     * Извлекает токен из заголовка Authorization HTTP-запроса.
     * Ожидает заголовок в формате "Bearer {token}".
     *
     * @param authHeader значение заголовка Authorization
     * @return извлеченный токен без префикса "Bearer ", или пустая строка если заголовок некорректен
     */
    public String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "";
        }
        return authHeader.substring(7);
    }

    /**
     * Добавляет новую активную сессию пользователя.
     * Связывает токен с объектом пользователя для последующего быстрого доступа.
     *
     * @param token токен авторизации (должен быть не null и не пустым)
     * @param user объект пользователя (должен быть не null)
     * @throws IllegalArgumentException если token или user равны null
     */
    public void addSession(String token, User user) {
        activeSessions.put(token, user);
    }

    /**
     * Удаляет активную сессию по токену.
     *
     * @param token токен сессии для удаления
     * @return объект пользователя, связанный с удаленной сессией, или null если сессия не найдена
     */
    public User removeSession(String token) {
        return activeSessions.remove(token);
    }

    /**
     * Получает пользователя по токену из заголовка авторизации.
     * Автоматически извлекает токен из заголовка перед поиском пользователя.
     *
     * @param token заголовок Authorization или очищенный токен
     * @return Optional с объектом пользователя, если сессия найдена, иначе пустой Optional
     */
    public Optional<User> getUserFromToken(String token) {
        String cleanToken = extractToken(token);
        return Optional.ofNullable(activeSessions.get(cleanToken));
    }

    /**
     * Получает пользователя по токену (уже очищенному от префикса "Bearer").
     *
     * @param token очищенный токен (без "Bearer ")
     * @return объект пользователя, связанный с токеном, или null если токен невалиден
     */
    public User getUserByToken(String token) {
        return activeSessions.get(token);
    }

    /**
     * Получает пользователя по заголовку авторизации.
     * Извлекает токен из заголовка и ищет соответствующую сессию.
     *
     * @param authHeader заголовок Authorization в формате "Bearer {token}"
     * @return объект пользователя, или null если токен невалиден или заголовок некорректен
     */
    public User getUserByAuthHeader(String authHeader) {
        String token = extractToken(authHeader);
        return getUserByToken(token);
    }

    /**
     * Проверяет, имеет ли пользователь, связанный с токеном, указанную роль.
     * Использует заголовок Authorization для извлечения токена.
     *
     * @param token заголовок Authorization или очищенный токен
     * @param requiredRole требуемая роль для проверки
     * @return true если пользователь существует и имеет требуемую роль, иначе false
     */
    public boolean hasRole(String token, Role requiredRole) {
        return getUserFromToken(token)
                .map(user -> user.getRole() == requiredRole)
                .orElse(false);
    }

    /**
     * Проверяет роль пользователя по токену (уже очищенному).
     *
     * @param token очищенный токен (без "Bearer ")
     * @param requiredRole требуемая роль для проверки
     * @return true если пользователь существует и имеет требуемую роль, иначе false
     */
    public boolean hasRoleFromToken(String token, Role requiredRole) {
        User user = getUserByToken(token);
        return user != null && user.getRole() == requiredRole;
    }

    /**
     * Возвращает количество активных пользовательских сессий.
     *
     * @return количество активных сессий в системе
     */
    public int getActiveSessionsCount() {
        return activeSessions.size();
    }

    /**
     * Проверяет валидность токена (существует ли активная сессия с таким токеном).
     *
     * @param token токен для проверки
     * @return true если токен валиден (существует активная сессия), иначе false
     */
    public boolean isValidToken(String token) {
        return activeSessions.containsKey(token);
    }

    /**
     * Очищает все активные пользовательские сессии.
     * Используется при перезапуске сервиса или для принудительного разлогинивания всех пользователей.
     */
    public void clearAllSessions() {
        activeSessions.clear();
    }
}