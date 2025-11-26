package org.example.service.impl;

import org.example.model.entity.User;
import org.example.repository.MetricsRepository;
import org.example.repository.UserRepository;
import org.example.repository.impl.MetricsRepositoryImpl;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.MetricsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация сервиса для работы с метриками пользователей.
 *
 * <p>Класс предоставляет бизнес-логику для управления и анализа метрик пользовательской активности.
 * Реализует паттерн Singleton для обеспечения единого экземпляра сервиса в приложении.
 *
 * <p>Основные возможности:
 * <ul>
 *   <li>Увеличение счетчиков метрик</li>
 *   <li>Получение статистики по пользователям</li>
 *   <li>Форматирование метрик для отображения</li>
 *   <li>Сброс метрик</li>
 *   <li>Получение топ-пользователей по активности</li>
 * </ul>
 *
 * @author Your Name
 * @version 1.0
 * @see MetricsService
 * @see MetricsRepositoryImpl
 * @see UserRepositoryImpl
 */
public class MetricsServiceImpl implements MetricsService {
    private final MetricsRepository metricsRepository;
    private final UserRepository userRepository;

    public MetricsServiceImpl(MetricsRepository metricsRepository, UserRepository userRepository) {
        this.metricsRepository = metricsRepository;
        this.userRepository = userRepository;
    }

    /**
     * Тип метрики: количество входов в систему
     */
    public static final String LOGIN_COUNT = "LOGIN_COUNT";

    /**
     * Тип метрики: количество выходов из системы
     */
    public static final String LOGOUT_COUNT = "LOGOUT_COUNT";

    /**
     * Тип метрики: количество добавленных товаров
     */
    public static final String PRODUCT_ADD_COUNT = "PRODUCT_ADD_COUNT";

    /**
     * Тип метрики: количество обновленных товаров
     */
    public static final String PRODUCT_UPDATE_COUNT = "PRODUCT_UPDATE_COUNT";

    /**
     * Тип метрики: количество удаленных товаров
     */
    public static final String PRODUCT_DELETE_COUNT = "PRODUCT_DELETE_COUNT";

    /**
     * Тип метрики: количество добавлений в корзину
     */
    public static final String BASKET_ADD_COUNT = "BASKET_ADD_COUNT";

    /**
     * Тип метрики: общее количество товаров
     */
    public static final String TOTAL_PRODUCTS = "TOTAL_PRODUCTS";

    /**
     * Тип метрики: общее количество пользователей
     */
    public static final String TOTAL_USERS = "TOTAL_USERS";

    /**
     * Увеличивает значение метрики пользователя на 1.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики для увеличения
     * @throws IllegalArgumentException если userId равен null или metricType пустой
     */
    @Override
    public void incrementMetric(Long userId, String metricType) {
        metricsRepository.incrementMetric(userId, metricType);
    }

    /**
     * Увеличивает значение метрики пользователя на указанное количество.
     *
     * @param userId         идентификатор пользователя
     * @param metricType     тип метрики для увеличения
     * @param incrementValue количество увеличений
     * @throws IllegalArgumentException если userId равен null, metricType пустой или incrementValue отрицательный
     */
    @Override
    public void incrementMetric(Long userId, String metricType, int incrementValue) {
        // Для множественного увеличения
        for (int i = 0; i < incrementValue; i++) {
            metricsRepository.incrementMetric(userId, metricType);
        }
    }

    /**
     * Возвращает значение конкретной метрики пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип запрашиваемой метрики
     * @return значение метрики, 0 если метрика не найдена
     * @throws IllegalArgumentException если userId равен null или metricType пустой
     */
    @Override
    public int getMetricValue(Long userId, String metricType) {
        return metricsRepository.getMetricValue(userId, metricType);
    }

    /**
     * Возвращает все метрики указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return карта метрик пользователя (тип метрики → значение), пустая карта если пользователь не найден
     * @throws IllegalArgumentException если userId равен null
     */
    @Override
    public Map<String, Integer> getUserMetrics(Long userId) {
        return metricsRepository.getUserMetrics(userId);
    }

    /**
     * Возвращает агрегированные метрики всех пользователей системы.
     *
     * @return карта всех метрик (тип метрики → суммарное значение по всем пользователям)
     */
    @Override
    public Map<String, Integer> getAllMetrics() {
        return metricsRepository.getAllMetrics();
    }

    /**
     * Возвращает метрики пользователя по его имени.
     *
     * @param username имя пользователя
     * @return карта метрик пользователя, пустая карта если пользователь не найден
     * @throws IllegalArgumentException если username равен null или пустой
     */
    @Override
    public Map<String, Integer> getUserMetricsByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return metricsRepository.getUserMetrics(userOptional.get().getId());
        }
        return new HashMap<>();
    }

    /**
     * Сбрасывает все метрики указанного пользователя.
     * Устанавливает значения всех метрик пользователя в 0.
     *
     * @param userId идентификатор пользователя
     * @throws IllegalArgumentException если userId равен null
     */
    @Override
    public void resetUserMetrics(Long userId) {
        // Удаляем все метрики пользователя
        Map<String, Integer> userMetrics = metricsRepository.getUserMetrics(userId);
        for (String metricType : userMetrics.keySet()) {
            resetMetric(userId, metricType);
        }
    }

    /**
     * Сбрасывает конкретную метрику пользователя.
     * Устанавливает значение указанной метрики в 0.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики для сброса
     * @throws IllegalArgumentException если userId равен null или metricType пустой
     */
    @Override
    public void resetMetric(Long userId, String metricType) {
        // Устанавливаем значение метрики в 0
        String sql = "INSERT INTO entity.user_metrics (user_id, metric_type, value) VALUES (?, ?, 0) " +
                     "ON CONFLICT (user_id, metric_type) DO UPDATE SET value = 0, " +
                     "updated_date = CURRENT_TIMESTAMP";
    }

    /**
     * Возвращает топ-N пользователей по указанной метрике.
     *
     * @param metricType тип метрики для сортировки
     * @param limit      количество пользователей в топе
     * @return карта (имя пользователя → значение метрики), отсортированная по убыванию значения метрики
     * @throws IllegalArgumentException если metricType пустой или limit отрицательный
     */
    @Override
    public Map<String, Integer> getTopUsersByMetric(String metricType, int limit) {
        Map<String, Integer> topUsers = new HashMap<>();

        // Получаем всех пользователей
        List<User> allUsers = userRepository.findAllUser();

        // Собираем метрики и сортируем
        Map<User, Integer> userMetrics = new HashMap<>();
        for (User user : allUsers) {
            int metricValue = metricsRepository.getMetricValue(user.getId(), metricType);
            if (metricValue > 0) {
                userMetrics.put(user, metricValue);
            }
        }

        // Сортируем по убыванию значения метрики и берем топ N
        userMetrics.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .forEach(entry -> topUsers.put(entry.getKey().getUserName(), entry.getValue()));

        return topUsers;
    }

    /**
     * Возвращает общую статистику системы в виде форматированной строки.
     * Включает агрегированные метрики по всем пользователям.
     *
     * @return форматированная строка с общей статистикой системы
     */
    @Override
    public String getOverallStatistics() {
        Map<String, Integer> allMetrics = getAllMetrics();
        StringBuilder statistics = new StringBuilder();

        statistics.append("=== ОБЩАЯ СТАТИСТИКА СИСТЕМЫ ===\n");
        statistics.append("Входы в систему: ").append(allMetrics.getOrDefault(LOGIN_COUNT, 0)).append("\n");
        statistics.append("Выходы из системы: ").append(allMetrics.getOrDefault(LOGOUT_COUNT, 0)).append("\n");
        statistics.append("Добавлено товаров: ").append(allMetrics.getOrDefault(PRODUCT_ADD_COUNT, 0)).append("\n");
        statistics.append("Обновлено товаров: ").append(allMetrics.getOrDefault(PRODUCT_UPDATE_COUNT, 0)).append("\n");
        statistics.append("Удалено товаров: ").append(allMetrics.getOrDefault(PRODUCT_DELETE_COUNT, 0)).append("\n");
        statistics.append("Добавлений в корзину: ").append(allMetrics.getOrDefault(BASKET_ADD_COUNT, 0)).append("\n");

        List<User> allUsers = userRepository.findAllUser();
        statistics.append("Всего пользователей: ").append(allUsers.size()).append("\n");

        return statistics.toString();
    }

    /**
     * Возвращает детальную статистику конкретного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return форматированная строка со статистикой пользователя или сообщение об ошибке если пользователь не найден
     * @throws IllegalArgumentException если userId равен null
     */
    @Override
    public String getUserStatistics(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return "Пользователь с ID " + userId + " не найден";
        }

        User user = userOptional.get();
        Map<String, Integer> userMetrics = getUserMetrics(userId);

        StringBuilder statistics = new StringBuilder();
        statistics.append("=== СТАТИСТИКА ПОЛЬЗОВАТЕЛЯ ===\n");
        statistics.append("Имя: ").append(user.getUserName()).append("\n");
        statistics.append("ID: ").append(user.getId()).append("\n");
        statistics.append("Роль: ").append(user.getRole()).append("\n\n");

        statistics.append("АКТИВНОСТЬ:\n");
        statistics.append("• Входы в систему: ").append(userMetrics.getOrDefault(LOGIN_COUNT, 0)).append("\n");
        statistics.append("• Выходы из системы: ").append(userMetrics.getOrDefault(LOGOUT_COUNT, 0)).append("\n");
        statistics.append("• Добавлено товаров: ").append(userMetrics.getOrDefault(PRODUCT_ADD_COUNT, 0)).append("\n");
        statistics.append("• Обновлено товаров: ").append(userMetrics.getOrDefault(PRODUCT_UPDATE_COUNT, 0)).append("\n");
        statistics.append("• Удалено товаров: ").append(userMetrics.getOrDefault(PRODUCT_DELETE_COUNT, 0)).append("\n");
        statistics.append("• Добавлений в корзину: ").append(userMetrics.getOrDefault(BASKET_ADD_COUNT, 0)).append("\n");

        return statistics.toString();
    }

    /**
     * Получает метрики в виде форматированной строки для отображения.
     * Включает детальную статистику по каждому пользователю и общую статистику системы.
     * Использует emoji для визуального улучшения вывода.
     *
     * @return форматированная строка со статистикой активности всех пользователей
     */
    @Override
    public String getFormattedMetrics() {
        List<User> allUsers = userRepository.findAllUser();
        if (allUsers.isEmpty()) {
            return "Пользователи не найдены";
        }

        StringBuilder result = new StringBuilder();
        result.append("=== СТАТИСТИКА АКТИВНОСТИ ПОЛЬЗОВАТЕЛЕЙ ===\n\n");

        for (User user : allUsers) {
            Map<String, Integer> userMetrics = getUserMetrics(user.getId());

            if (!userMetrics.isEmpty()) {
                result.append("👤 ").append(user.getUserName())
                        .append(" (ID: ").append(user.getId()).append(")\n");

                result.append("   🔐 Входы: ").append(userMetrics.getOrDefault(LOGIN_COUNT, 0))
                        .append(" | Выходы: ").append(userMetrics.getOrDefault(LOGOUT_COUNT, 0)).append("\n");

                result.append("   🛍️  Товары: +").append(userMetrics.getOrDefault(PRODUCT_ADD_COUNT, 0))
                        .append(" / ✏️  ").append(userMetrics.getOrDefault(PRODUCT_UPDATE_COUNT, 0))
                        .append(" / 🗑️  ").append(userMetrics.getOrDefault(PRODUCT_DELETE_COUNT, 0)).append("\n");

                result.append("   🛒 Корзина: ").append(userMetrics.getOrDefault(BASKET_ADD_COUNT, 0))
                        .append(" добавлений\n\n");
            }
        }

        // Общая статистика
        Map<String, Integer> allMetrics = getAllMetrics();
        result.append("=== ОБЩАЯ СТАТИСТИКА ===\n");
        result.append("📊 Всего операций:\n");
        result.append("   • Входы: ").append(allMetrics.getOrDefault(LOGIN_COUNT, 0)).append("\n");
        result.append("   • Товары добавлено: ").append(allMetrics.getOrDefault(PRODUCT_ADD_COUNT, 0)).append("\n");
        result.append("   • Товары обновлено: ").append(allMetrics.getOrDefault(PRODUCT_UPDATE_COUNT, 0)).append("\n");
        result.append("   • Товары удалено: ").append(allMetrics.getOrDefault(PRODUCT_DELETE_COUNT, 0)).append("\n");
        result.append("   • Добавлений в корзину: ").append(allMetrics.getOrDefault(BASKET_ADD_COUNT, 0)).append("\n");
        result.append("👥 Всего пользователей: ").append(allUsers.size()).append("\n");

        return result.toString();
    }
}