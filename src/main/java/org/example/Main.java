package org.example;

import org.example.config.impl.UserSecurityConfigImpl;
import org.example.controller.ViewingConsole;
import org.example.repository.impl.AuditRepositoryImpl;
import org.example.repository.impl.ProductRepositoryImpl;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.impl.ProductServiceImpl;
import org.example.service.impl.UserServiceImpl;

public class Main {

    public static void main(String[] args) {
        // 1. Создаем экземпляры репозиториев (уровень данных)
        UserRepositoryImpl userRepository = new UserRepositoryImpl();
        ProductRepositoryImpl productRepository = new ProductRepositoryImpl();
        AuditRepositoryImpl auditRepository = new AuditRepositoryImpl();

        // 2. Создаем экземпляры конфигураций
        UserSecurityConfigImpl userSecurityConfig = new UserSecurityConfigImpl();

        // 3. Создаем экземпляры сервисов (бизнес-логика)
        UserServiceImpl userService = new UserServiceImpl();
        ProductServiceImpl productService = new ProductServiceImpl();

        // 4. Настраиваем зависимости между компонентами
        // Настраиваем UserSecurityConfig
        userSecurityConfig.setUserRepository(userRepository);

        // Настраиваем UserService
        userService.setUserRepository(userRepository);

        // Настраиваем AuditRepository
        auditRepository.setUserSecurityConfig(userSecurityConfig);

        // Настраиваем ProductService
        productService.setUserRepository(userRepository);
        productService.setProductRepository(productRepository);
        productService.setAuditRepository(auditRepository);
        productService.setUserSecurityService(userSecurityConfig);
        productService.setUserService(userService);

        // 5. Создаем и настраиваем контроллер
        ViewingConsole viewingConsole = new ViewingConsole();

        // Передаем настроенные зависимости в контроллер
        viewingConsole.setProductService(productService);
        viewingConsole.setUserService(userService);
        viewingConsole.setUserSecurityConfig(userSecurityConfig);
        viewingConsole.setAuditRepository(auditRepository);

        viewingConsole.start();
    }
}