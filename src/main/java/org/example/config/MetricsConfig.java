package org.example.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import org.example.repository.impl.MetricsRepositoryImpl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Конфигурационный класс для управления метриками приложения.
 * Реализует паттерн Singleton для обеспечения единой точки доступа к метрикам.
 *
 * <p>Класс предоставляет следующие типы метрик:
 * <ul>
 *   <li>Счетчики (Counter) - для подсчета событий</li>
 *   <li>Таймеры (Timer) - для измерения времени выполнения операций</li>
 *   <li>Gauges - для измерения текущих значений</li>
 * </ul>
 *
 * <p>Метрики интегрируются с репозиторием для сохранения в базе данных.
 *
 * @author Your Name
 * @version 1.0
 * @see Counter
 * @see Timer
 * @see Gauge
 * @see MetricsRepositoryImpl
 */
public class MetricsConfig {
    private static MetricsConfig instance;
    private final MeterRegistry registry;
    private final MetricsRepositoryImpl metricsRepository;

    // Основные счетчики
    private final Counter userLoginCounter;
    private final Counter userLogoutCounter;
    private final Counter userRegistrationCounter;
    private final Counter productAddCounter;
    private final Counter productUpdateCounter;
    private final Counter productDeleteCounter;
    private final Counter basketAddCounter;
    private final Counter databaseErrorCounter;

    // Таймеры
    private final Timer databaseQueryTimer;
    private final Timer userAuthenticationTimer;
    private final Timer productOperationTimer;

    // Gauges
    private final AtomicInteger activeUsersGauge = new AtomicInteger(0);

    /**
     * Приватный конструктор для инициализации всех метрик.
     * Создает счетчики, таймеры и gauges для мониторинга приложения.
     */
    private MetricsConfig() {
        this.registry = Metrics.globalRegistry;
        this.metricsRepository = MetricsRepositoryImpl.getInstance();

        // Инициализируем счетчики
        userLoginCounter = Counter.builder("app.user.login.total")
                .description("Total number of user logins")
                .register(registry);

        userLogoutCounter = Counter.builder("app.user.logout.total")
                .description("Total number of user logouts")
                .register(registry);

        userRegistrationCounter = Counter.builder("app.user.registration.total")
                .description("Total number of user registrations")
                .register(registry);

        productAddCounter = Counter.builder("app.product.add.total")
                .description("Total number of products added")
                .register(registry);

        productUpdateCounter = Counter.builder("app.product.update.total")
                .description("Total number of products updated")
                .register(registry);

        productDeleteCounter = Counter.builder("app.product.delete.total")
                .description("Total number of products deleted")
                .register(registry);

        basketAddCounter = Counter.builder("app.basket.add.total")
                .description("Total number of basket additions")
                .register(registry);

        databaseErrorCounter = Counter.builder("app.database.errors.total")
                .description("Total number of database errors")
                .register(registry);

        // Инициализируем таймеры
        databaseQueryTimer = Timer.builder("app.database.query.duration")
                .description("Database query execution time")
                .register(registry);

        userAuthenticationTimer = Timer.builder("app.user.auth.duration")
                .description("User authentication time")
                .register(registry);

        productOperationTimer = Timer.builder("app.product.operation.duration")
                .description("Product operation execution time")
                .register(registry);

        initializeGauges();
    }

    /**
     * Возвращает единственный экземпляр MetricsConfig (реализация Singleton).
     *
     * @return единственный экземпляр MetricsConfig
     */
    public static synchronized MetricsConfig getInstance() {
        if (instance == null) {
            instance = new MetricsConfig();
        }
        return instance;
    }

    /**
     * Инициализирует gauge метрики для мониторинга текущих значений.
     */
    private void initializeGauges() {
        Gauge.builder("app.users.active", activeUsersGauge, AtomicInteger::get)
                .description("Number of active users")
                .register(registry);
    }

    /**
     * Увеличивает значение метрики пользователя в базе данных.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип метрики для увеличения
     */
    public void incrementUserMetric(Long userId, String metricType) {
        metricsRepository.incrementMetric(userId, metricType);
    }

    /**
     * Возвращает значение конкретной метрики пользователя.
     *
     * @param userId     идентификатор пользователя
     * @param metricType тип запрашиваемой метрики
     * @return значение метрики или 0, если метрика не найдена
     */
    public int getUserMetricValue(Long userId, String metricType) {
        return metricsRepository.getMetricValue(userId, metricType);
    }

    /**
     * Возвращает все метрики для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return карта метрик пользователя (тип метрики → значение)
     */
    public Map<String, Integer> getUserMetrics(Long userId) {
        return metricsRepository.getUserMetrics(userId);
    }

    /**
     * Возвращает агрегированные метрики всех пользователей.
     *
     * @return карта всех метрик (тип метрики → суммарное значение)
     */
    public Map<String, Integer> getAllMetrics() {
        return metricsRepository.getAllMetrics();
    }

    /**
     * Устанавливает количество активных пользователей.
     *
     * @param count количество активных пользователей
     */
    public void setActiveUsers(int count) {
        activeUsersGauge.set(count);
    }

    /**
     * Увеличивает счетчик активных пользователей на 1.
     */
    public void incrementActiveUsers() {
        activeUsersGauge.incrementAndGet();
    }

    /**
     * Уменьшает счетчик активных пользователей на 1.
     */
    public void decrementActiveUsers() {
        activeUsersGauge.decrementAndGet();
    }

    // Геттеры для основных метрик

    /**
     * Возвращает счетчик входов пользователей в систему.
     *
     * @return счетчик входов пользователей
     */
    public Counter getUserLoginCounter() {
        return userLoginCounter;
    }

    /**
     * Возвращает счетчик выходов пользователей из системы.
     *
     * @return счетчик выходов пользователей
     */
    public Counter getUserLogoutCounter() {
        return userLogoutCounter;
    }

    /**
     * Возвращает счетчик регистраций новых пользователей.
     *
     * @return счетчик регистраций пользователей
     */
    public Counter getUserRegistrationCounter() {
        return userRegistrationCounter;
    }

    /**
     * Возвращает счетчик добавления товаров.
     *
     * @return счетчик добавления товаров
     */
    public Counter getProductAddCounter() {
        return productAddCounter;
    }

    /**
     * Возвращает счетчик обновления товаров.
     *
     * @return счетчик обновления товаров
     */
    public Counter getProductUpdateCounter() {
        return productUpdateCounter;
    }

    /**
     * Возвращает счетчик удаления товаров.
     *
     * @return счетчик удаления товаров
     */
    public Counter getProductDeleteCounter() {
        return productDeleteCounter;
    }

    /**
     * Возвращает счетчик добавления товаров в корзину.
     *
     * @return счетчик добавления в корзину
     */
    public Counter getBasketAddCounter() {
        return basketAddCounter;
    }

    /**
     * Возвращает счетчик ошибок базы данных.
     *
     * @return счетчик ошибок БД
     */
    public Counter getDatabaseErrorCounter() {
        return databaseErrorCounter;
    }

    /**
     * Возвращает таймер выполнения запросов к базе данных.
     *
     * @return таймер запросов БД
     */
    public Timer getDatabaseQueryTimer() {
        return databaseQueryTimer;
    }

    /**
     * Возвращает таймер аутентификации пользователей.
     *
     * @return таймер аутентификации
     */
    public Timer getUserAuthenticationTimer() {
        return userAuthenticationTimer;
    }

    /**
     * Возвращает таймер операций с товарами.
     *
     * @return таймер операций с товарами
     */
    public Timer getProductOperationTimer() {
        return productOperationTimer;
    }

    /**
     * Устанавливает экземпляр MetricsConfig (используется для тестирования).
     *
     * @param instance экземпляр MetricsConfig для установки
     */
    public static void setInstance(MetricsConfig instance) {
        MetricsConfig.instance = instance;
    }
}