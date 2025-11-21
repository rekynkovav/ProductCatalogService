package org.example.service;

import java.util.Map;

/**
 * Сервис для работы с метриками пользователей.
 * Предоставляет методы для управления и получения метрик из базы данных.
 */
public interface MetricsService {

    /**
     * Увеличивает счетчик метрики для пользователя на 1.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики
     */
    void incrementMetric(Long userId, String metricType);

    /**
     * Увеличивает счетчик метрики для пользователя на указанное значение.
     *
     * @param userId         идентификатор пользователя
     * @param metricType     тип метрики
     * @param incrementValue значение для увеличения
     */
    void incrementMetric(Long userId, String metricType, int incrementValue);

    /**
     * Получает значение конкретной метрики для пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики
     * @return значение метрики
     */
    int getMetricValue(Long userId, String metricType);

    /**
     * Получает все метрики для конкретного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return карта метрик (тип метрики -> значение)
     */
    Map<String, Integer> getUserMetrics(Long userId);

    /**
     * Получает агрегированные метрики по всем пользователям.
     *
     * @return карта метрик (тип метрики -> общее значение)
     */
    Map<String, Integer> getAllMetrics();

    /**
     * Получает метрики для пользователя по имени.
     *
     * @param username имя пользователя
     * @return карта метрик (тип метрики -> значение)
     */
    Map<String, Integer> getUserMetricsByUsername(String username);

    /**
     * Сбрасывает все метрики для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     */
    void resetUserMetrics(Long userId);

    /**
     * Сбрасывает конкретную метрику для пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики
     */
    void resetMetric(Long userId, String metricType);

    /**
     * Получает топ N пользователей по конкретной метрике.
     *
     * @param metricType тип метрики
     * @param limit      количество пользователей в топе
     * @return карта (имя пользователя -> значение метрики)
     */
    Map<String, Integer> getTopUsersByMetric(String metricType, int limit);

    /**
     * Получает общую статистику по всем метрикам.
     *
     * @return строка с форматированной статистикой
     */
    String getOverallStatistics();

    /**
     * Получает детальную статистику для конкретного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return строка с форматированной статистикой
     */
    String getUserStatistics(Long userId);
}