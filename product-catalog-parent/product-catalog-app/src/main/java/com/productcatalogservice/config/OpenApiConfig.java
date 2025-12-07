package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурационный класс для SpringDoc OpenAPI (замена Swagger 2).
 * Настраивает документацию API в соответствии со спецификацией OpenAPI 3.
 *
 * <p>Swagger UI доступен по адресу: <code>/swagger-ui.html</code></p>
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see Configuration
 */
@Configuration
public class OpenApiConfig {

    /**
     * URL сервера из конфигурации.
     */
    @Value("${server.url:http://localhost:8080}")
    private String serverUrl;

    /**
     * Название приложения из конфигурации.
     */
    @Value("${spring.application.name:Product Catalog Service}")
    private String applicationName;

    /**
     * Версия приложения из конфигурации.
     */
    @Value("${application.version:1.0.0}")
    private String applicationVersion;

    /**
     * Создает и настраивает OpenAPI конфигурацию.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI productCatalogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Catalog API")
                        .description("""
                    REST API for Product Catalog Management
                    
                    ### Основные функции:
                    - Управление продуктами
                    - Управление категориями
                    - Управление тегами
                    - Поиск и фильтрация продуктов
                    
                    ### Технологии:
                    - Spring Boot 3.2.0
                    - Spring Data JPA
                    - PostgreSQL
                    - Liquibase
                    - SpringDoc OpenAPI
                    """)
                        .version(applicationVersion)
                        .contact(new Contact()
                                .name("Y_Lab_Student")
                                .url("https://github.com/rekynkovav")
                                .email("Rekynkovav@yandex.ru"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url(serverUrl)
                                .description("Production Server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")));
    }
}