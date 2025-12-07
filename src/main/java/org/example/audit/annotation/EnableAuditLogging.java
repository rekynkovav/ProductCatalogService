package org.example.audit.annotation;

import org.example.audit.autoconfig.config.AuditAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для явного включения поддержки аудита и логирования в приложении.
 * Применяется к классам конфигурации Spring для активации модуля аудита.
 *
 * <p>Пример использования:</p>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableAuditLogging
 * public class ProductCatalogApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(ProductCatalogApplication.class, args);
 *     }
 * }
 * }</pre>
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see Import
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AuditAutoConfiguration.class)
public @interface EnableAuditLogging {

    /**
     * Включение/отключение аудита операций.
     *
     * @return true если аудит операций включен (по умолчанию true)
     */
    boolean enableOperations() default true;

    /**
     * Включение/отключение логирования методов.
     *
     * @return true если логирование методов включено (по умолчанию true)
     */
    boolean enableLogging() default true;
}