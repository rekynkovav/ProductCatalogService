package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Конфигурационный класс для настройки Swagger (OpenAPI) документации.
 * Включает автоматическую генерацию API документации на основе аннотаций в контроллерах.
 *
 * <p>Swagger UI доступен по адресу: <code>/swagger-ui.html</code></p>
 *
 * @see Docket
 * @see EnableSwagger2
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Создает и настраивает основной Docket (конфигурацию) для Swagger.
     * Определяет базовый пакет для сканирования контроллеров и глобальные настройки API.
     *
     * @return настроенный объект Docket для документации API
     * @apiNote API будет включать только контроллеры из пакета org.example.controller
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.example.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .enable(true);
    }

    /**
     * Создает метаинформацию об API для отображения в Swagger UI.
     * Включает заголовок, описание, версию и контактную информацию.
     *
     * @return объект ApiInfo с информацией об API
     * @see ApiInfoBuilder
     */
    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Product Catalog API")
                .description("REST API for Product Catalog Management")
                .version("1.0.0")
                .contact(new Contact("Y_Lab_Student",
                        "https://github.com/rekynkovav",
                        "Rekynkovav@yandex.ru"))
                .build();
    }
}