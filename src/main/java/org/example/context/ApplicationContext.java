package org.example.context;

import org.example.aspect.AuditAspect;
import org.example.aspect.LoggingAspect;
import org.example.config.ConnectionManager;
import org.example.config.LiquibaseMigration;
import org.example.aspect.MetricsAspect;
import org.example.repository.AspectRepository;
import org.example.repository.ProductRepository;
import org.example.repository.UserRepository;
import org.example.repository.impl.AspectRepositoryImpl;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.ProductService;
import org.example.service.SecurityService;
import org.example.service.UserService;
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

            AspectRepository aspectRepository = new AspectRepositoryImpl(connectionManager);
            registerBean(AspectRepository.class, aspectRepository);

            AuditAspect auditAspect = new AuditAspect(aspectRepository);
            registerBean(AuditAspect.class, auditAspect);

            ProductRepository productRepository = new ProductRepositoryImpl(connectionManager);
            registerBean(ProductRepository.class, productRepository);

            UserRepository userRepository = new UserRepositoryImpl(connectionManager, productRepository);
            registerBean(UserRepository.class, userRepository);

            UserService userService = new UserServiceImpl(userRepository);
            registerBean(UserService.class, userService);

            SecurityService securityService = new SecurityServiceImpl(userService);
            registerBean(SecurityService.class, securityService);

            ProductService productService = new ProductServiceImpl( userRepository, productRepository);
            registerBean(ProductService.class, productService);

            LiquibaseMigration liquibaseMigration = new LiquibaseMigration(connectionManager);
            registerBean(LiquibaseMigration.class, liquibaseMigration);

            LoggingAspect loggingAspect = new LoggingAspect();
            registerBean(LoggingAspect.class, loggingAspect);

            MetricsAspect metricsAspect = new MetricsAspect(userService);
            registerBean(MetricsAspect.class, metricsAspect);
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