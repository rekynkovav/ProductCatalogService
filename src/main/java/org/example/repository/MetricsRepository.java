package org.example.repository;

import java.util.Map;

/**
 * Репозиторий для работы с метриками пользователей.
 * Предоставляет методы для сбора, получения и управления метриками.
 *
 * @author Your Name
 * @version 1.0
 * @since 2024
 */
public interface MetricsRepository {

    /**
     * Увеличивает значение указанной метрики для пользователя на 1.
     * Если метрика не существует, она будет создана с начальным значением 1.
     *
     * @param userId     идентификатор пользователя, для которого увеличивается метрика
     * @param metricType тип метрики для увеличения (например, "login_count", "page_views")
     * @throws IllegalArgumentException если userId is null или metricType is null/empty
     */
    void incrementMetric(Long userId, String metricType);

    /**
     * Возвращает значение конкретной метрики для указанного пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип запрашиваемой метрики
     * @return значение метрики для пользователя. Если метрика не найдена, возвращает 0
     * @throws IllegalArgumentException если userId is null или metricType is null/empty
     */
    int getMetricValue(Long userId, String metricType);

    /**
     * Возвращает все метрики для указанного пользователя в виде карты "тип метрики -> значение".
     *
     * @param userId идентификатор пользователя
     * @return карта всех метрик пользователя. Если пользователь не найден или метрик нет,
     * возвращает пустую карту (не null)
     * @throws IllegalArgumentException если userId is null
     */
    Map<String, Integer> getUserMetrics(Long userId);

    /**
     * Возвращает агрегированные метрики всех пользователей.
     * Метрики группируются по типу и суммируются по всем пользователям.
     *
     * @return карта всех метрик системы в формате "тип метрики -> общее значение".
     * Если метрик нет, возвращает пустую карту (не null)
     */
    Map<String, Integer> getAllMetrics();

    /**
     * Удаляет все метрики всех пользователей из системы.
     * Используется преимущественно для тестирования и очистки данных.
     *
     * @implNote Операция необратима. Все данные о метриках будут безвозвратно удалены.
     */
    void deleteAllMetrics();

    /**
     * Удаляет метрики юзера по id
     */
    void deleteUserMetricsById(Long userId);
}