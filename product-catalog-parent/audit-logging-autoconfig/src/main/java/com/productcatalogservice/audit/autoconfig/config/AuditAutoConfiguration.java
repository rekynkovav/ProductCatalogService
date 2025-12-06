package org.example.audit.autoconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productcatalogservice.audit.autoconfig.aspect.AuditAspect;
import com.productcatalogservice.audit.autoconfig.aspect.LoggingAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Автоконфигурация для аспектов аудита и логирования.
 * Автоматически активируется при наличии соответствующих зависимостей в classpath.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see Configuration
 */
@Configuration
@ConditionalOnClass({AuditAspect.class, LoggingAspect.class})
@EnableConfigurationProperties(AuditProperties.class)
public class AuditAutoConfiguration {

    /**
     * Создает бин LoggingAspect для автоматического логирования методов.
     * Бин создается только если отсутствует пользовательская реализация.
     *
     * @return настроенный аспект логирования
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "product-catalog.audit.logging",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    /**
     * Создает бин AuditAspect для автоматического аудита операций.
     * Бин создается только если отсутствует пользовательская реализация.
     *
     * @param objectMapper маппер для сериализации объектов в JSON
     * @return настроенный аспект аудита
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "product-catalog.audit",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    public AuditAspect auditAspect(ObjectMapper objectMapper) {
        return new AuditAspect(objectMapper);
    }
}