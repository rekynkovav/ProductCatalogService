package com.productCatalogService.audit.autoconfig.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Свойства конфигурации для модуля аудита и логирования.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see ConfigurationProperties
 */
@Data
@ConfigurationProperties(prefix = "product-catalog.audit")
public class AuditProperties {

    /**
     * Включение/отключение всего модуля аудита.
     */
    private boolean enabled = true;

    /**
     * Свойства для логирования.
     */
    private LoggingProperties logging = new LoggingProperties();

    /**
     * Свойства для аудита операций.
     */
    private OperationProperties operations = new OperationProperties();

    @Data
    public static class LoggingProperties {
        /**
         * Включение/отключение логирования методов.
         */
        private boolean enabled = true;

        /**
         * Уровень логирования для входа в методы.
         */
        private String entryLevel = "DEBUG";

        /**
         * Уровень логирования для выхода из методов.
         */
        private String exitLevel = "DEBUG";

        /**
         * Уровень логирования для исключений.
         */
        private String exceptionLevel = "ERROR";
    }

    @Data
    public static class OperationProperties {
        /**
         * Включение/отключение аудита операций.
         */
        private boolean enabled = true;

        /**
         * Аудит операций создания.
         */
        private boolean create = true;

        /**
         * Аудит операций обновления.
         */
        private boolean update = true;

        /**
         * Аудит операций удаления.
         */
        private boolean delete = true;

        /**
         * Логировать содержимое сущностей (может содержать конфиденциальные данные).
         */
        private boolean logEntityContent = false;
    }
}