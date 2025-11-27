package org.example.repository.impl;

import org.example.config.ConnectionManager;
import org.example.repository.AspectRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация репозитория для работы с метриками пользователей в базе данных.
 * Предоставляет методы для управления метриками, включая инкремент, получение и удаление.
 *
 * <p>Класс реализует шаблон Singleton для обеспечения единственного экземпляра репозитория.</p>
 *
 * @see AspectRepository
 * @see org.example.config.ConnectionManager
 */

public class AspectRepositoryImpl implements AspectRepository {
    private final ConnectionManager connectionManager;

    public AspectRepositoryImpl(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Увеличивает значение метрики для указанного пользователя на 1.
     * Если запись не существует, создает новую с начальным значением 1.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики для инкремента
     * @throws RuntimeException если происходит ошибка SQL при выполнении запроса
     */
    @Override
    public void incrementMetric(Long userId, String metricType) {
        String sql = "INSERT INTO entity.user_metrics (user_id, metric_type, value) VALUES (?, ?, 1) " +
                     "ON CONFLICT (user_id, metric_type) DO UPDATE SET value = user_metrics.value + 1, " +
                     "updated_date = CURRENT_TIMESTAMP";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, metricType);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error incrementing metric: " + metricType, e);
        }
    }

    /**
     * Возвращает значение конкретной метрики для указанного пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики для получения
     * @return значение метрики, или 0 если запись не найдена
     * @throws RuntimeException если происходит ошибка SQL при выполнении запроса
     */
    @Override
    public int getMetricValue(Long userId, String metricType) {
        String sql = "SELECT value FROM entity.user_metrics WHERE user_id = ? AND metric_type = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, metricType);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("value");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting metric value: " + metricType, e);
        }
    }

    /**
     * Возвращает все метрики для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return Map где ключ - тип метрики, значение - значение метрики
     * @throws RuntimeException если происходит ошибка SQL при выполнении запроса
     */
    @Override
    public Map<String, Integer> getUserMetrics(Long userId) {
        Map<String, Integer> metrics = new HashMap<>();
        String sql = "SELECT metric_type, value FROM entity.user_metrics WHERE user_id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                metrics.put(resultSet.getString("metric_type"), resultSet.getInt("value"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting user metrics", e);
        }
        return metrics;
    }

    /**
     * Возвращает агрегированные метрики по всем пользователям.
     * Суммирует значения для каждого типа метрики.
     *
     * @return Map где ключ - тип метрики, значение - суммарное значение метрики по всем пользователям
     * @throws RuntimeException если происходит ошибка SQL при выполнении запроса
     */
    @Override
    public Map<String, Integer> getAllMetrics() {
        Map<String, Integer> metrics = new HashMap<>();
        String sql = "SELECT metric_type, SUM(value) as total_value FROM entity.user_metrics GROUP BY metric_type";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                metrics.put(resultSet.getString("metric_type"), resultSet.getInt("total_value"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all metrics", e);
        }
        return metrics;
    }

    /**
     * Удаляет все метрики всех пользователей из базы данных.
     *
     * @throws RuntimeException если происходит ошибка SQL при выполнении запроса
     */
    @Override
    public void deleteAllMetrics() {
        String sql = "DELETE FROM entity.user_metrics";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all user metrics", e);
        }
    }

    /**
     * Удаляет все метрики для указанного пользователя.
     *
     * @param userId идентификатор пользователя, чьи метрики нужно удалить
     * @throws RuntimeException если происходит ошибка SQL при выполнении запроса
     */
    @Override
    public void deleteUserMetricsById(Long userId) {
        String sql = "DELETE FROM entity.user_metrics WHERE user_id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user metrics", e);
        }
    }
    /**
     * Сохраняет запись аудита в базу данных
     *
     * @param userId ID пользователя (может быть null для анонимных действий)
     * @param username имя пользователя
     * @param action выполненное действие
     * @param clientIp IP адрес клиента
     * @param status выполнения (SUCCESS, ERROR)
     * @param executionTime время выполнения в миллисекундах
     * @param errorMessage сообщение об ошибке (если есть)
     */
    @Override
    public void saveAuditLog(Long userId, String username, String action, String clientIp,
                             String status, Long executionTime, String errorMessage) {
        String sql = """
            INSERT INTO entity.audit_logs (user_id, username, action, client_ip, status, execution_time_ms, error_message)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Устанавливаем параметры
            if (userId != null) {
                stmt.setLong(1, userId);
            } else {
                stmt.setNull(1, java.sql.Types.BIGINT);
            }
            stmt.setString(2, username != null ? username : "anonymous");
            stmt.setString(3, action);
            stmt.setString(4, clientIp != null ? clientIp : "unknown");
            stmt.setString(5, status);

            if (executionTime != null) {
                stmt.setLong(6, executionTime);
            } else {
                stmt.setNull(6, java.sql.Types.BIGINT);
            }

            stmt.setString(7, errorMessage);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving audit log for action: " + action, e);
        }
    }
}