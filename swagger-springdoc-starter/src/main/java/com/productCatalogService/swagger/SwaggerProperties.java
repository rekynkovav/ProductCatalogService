package com.productCatalogService.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Свойства конфигурации для SpringDoc OpenAPI.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see ConfigurationProperties
 */
@Data
@ConfigurationProperties(prefix = "springdoc")
public class SwaggerProperties {

    /**
     * Включение/отключение SpringDoc OpenAPI.
     */
    private boolean enabled = true;

    /**
     * Настройки API документации.
     */
    private ApiDocsProperties apiDocs = new ApiDocsProperties();

    /**
     * Настройки Swagger UI.
     */
    private SwaggerUiProperties swaggerUi = new SwaggerUiProperties();

    /**
     * Настройки информации об API.
     */
    private InfoProperties info = new InfoProperties();

    @Data
    public static class ApiDocsProperties {
        /**
         * Путь к документации API.
         */
        private String path = "/v3/api-docs";

        /**
         * Включение/отключение документации API.
         */
        private boolean enabled = true;
    }

    @Data
    public static class SwaggerUiProperties {
        /**
         * Путь к Swagger UI.
         */
        private String path = "/swagger-ui.html";

        /**
         * Включение/отключение Swagger UI.
         */
        private boolean enabled = true;

        /**
         * Операции по умолчанию.
         */
        private String operationsSorter = "method";

        /**
         * Теги по умолчанию.
         */
        private String tagsSorter = "alpha";
    }

    @Data
    public static class InfoProperties {
        /**
         * Заголовок API.
         */
        private String title = "Product Catalog API";

        /**
         * Описание API.
         */
        private String description = "REST API Documentation";

        /**
         * Версия API.
         */
        private String version = "1.0.0";

        /**
         * Контактная информация.
         */
        private ContactProperties contact = new ContactProperties();

        @Data
        public static class ContactProperties {
            private String name = "Product Catalog Team";
            private String email = "support@productcatalog.com";
            private String url = "";
        }
    }
}