package com.productCatalogService;

import com.productCatalogService.audit.annotation.EnableAuditLogging;
import com.productCatalogService.swagger.EnableSwagger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Основной класс Spring Boot приложения Product Catalog Service.
 * Запускает приложение и настраивает автоконфигурацию.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Автоконфигурация Spring Boot</li>
 *   <li>Включение аудита JPA сущностей</li>
 *   <li>Активация модуля аудита и логирования</li>
 *   <li>Сканирование компонентов во всех пакетах приложения</li>
 * </ul>
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see SpringBootApplication
 * @see EnableAuditLogging
 */
@SpringBootApplication(scanBasePackages = "com.productCatalogService")
@EnableJpaAuditing
@EnableAuditLogging
@EnableAspectJAutoProxy
@EnableSwagger
public class ProductCatalogApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}