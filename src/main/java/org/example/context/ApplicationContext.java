package org.example.context;

import org.example.config.ConnectionManager;
import org.example.config.LiquibaseMigration;
import org.example.config.MetricsConfig;
import org.example.controller.ViewingConsole;
import org.example.repository.MetricsRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.repository.impl.MetricsRepositoryImpl;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.MetricsService;
import org.example.service.ProductService;
import org.example.service.SecurityService;
import org.example.service.UserService;
import org.example.service.impl.MetricsServiceImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.SecurityServiceImpl;
import org.example.service.impl.UserServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Контекст приложения для управления зависимостями.
 */
public class ApplicationContext {

    private static ApplicationContext instance;
    private final Map<Class<?>, Object> mapBeans = new HashMap<>();

    private ApplicationContext() {
        initializeBeans();
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    private void initializeBeans() {
        try {
            ConnectionManager connectionManager = new ConnectionManager();
            registerBean(ConnectionManager.class, connectionManager);

            MetricsRepository metricsRepository = new MetricsRepositoryImpl(connectionManager);
            registerBean(MetricsRepository.class, metricsRepository);

            MetricsConfig metricsConfig = new MetricsConfig(metricsRepository);
            registerBean(MetricsConfig.class, metricsConfig);

            ProductRepository productRepository = new ProductRepositoryImpl(connectionManager, metricsConfig);
            registerBean(ProductRepository.class, productRepository);

            UserRepository userRepository = new UserRepositoryImpl(connectionManager, metricsConfig, productRepository);
            registerBean(UserRepository.class, userRepository);

            UserService userService = new UserServiceImpl(userRepository);
            registerBean(UserService.class, userService);

            MetricsService metricsService = new MetricsServiceImpl(metricsRepository, userRepository);
            registerBean(MetricsService.class, metricsService);

            SecurityService securityService = new SecurityServiceImpl(userService, metricsConfig, metricsService);
            registerBean(SecurityService.class, securityService);

            ProductService productService = new ProductServiceImpl(securityService, userRepository, metricsConfig, productRepository, metricsService);
            registerBean(ProductService.class, productService);

            LiquibaseMigration liquibaseMigration = new LiquibaseMigration(connectionManager);
            registerBean(LiquibaseMigration.class, liquibaseMigration);

            ViewingConsole viewingConsole = new ViewingConsole(productService, userService, securityService, metricsService);
            registerBean(ViewingConsole.class, viewingConsole);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize application context", e);
        }
    }

    private <T> void registerBean(Class<T> type, T bean) {
        mapBeans.put(type, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        Object bean = mapBeans.get(type);
        if (bean == null) {
            throw new IllegalStateException("Bean not found: " + type.getName());
        }
        return (T) bean;
    }

    /**
     * Очищает контекст (для тестов)
     */
    public static void clear() {
        instance = null;
    }
}