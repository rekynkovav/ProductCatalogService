package com.productCatalogService;

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
 */
@SpringBootApplication(scanBasePackages = "com.productCatalogService")
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableSwagger
public class ProductCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductCatalogApplication.class, args);
    }
}