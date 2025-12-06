package org.example.config;

import org.example.audit.autoconfig.config.AuditProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест для свойств конфигурации аудита.
 * Проверяет загрузку и значения свойств из application.yml.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see AuditProperties
 */
@SpringBootTest
@EnableConfigurationProperties(AuditProperties.class)
@TestPropertySource(properties = {
        "product-catalog.audit.enabled=true",
        "product-catalog.audit.logging.enabled=true",
        "product-catalog.audit.logging.entry-level=INFO",
        "product-catalog.audit.logging.exit-level=INFO",
        "product-catalog.audit.logging.exception-level=WARN",
        "product-catalog.audit.operations.enabled=false",
        "product-catalog.audit.operations.create=true",
        "product-catalog.audit.operations.update=false",
        "product-catalog.audit.operations.delete=true",
        "product-catalog.audit.operations.log-entity-content=true"
})
class AuditPropertiesTest {

    @Autowired
    private AuditProperties auditProperties;

    /**
     * Тест проверяет загрузку свойств аудита.
     */
    @Test
    void shouldLoadAuditProperties() {
        assertThat(auditProperties).isNotNull();
    }

    /**
     * Тест проверяет общее включение аудита.
     */
    @Test
    void shouldHaveCorrectEnabledValue() {
        assertThat(auditProperties.isEnabled()).isTrue();
    }

    /**
     * Тест проверяет свойства логирования.
     */
    @Test
    void shouldHaveCorrectLoggingProperties() {
        AuditProperties.LoggingProperties logging = auditProperties.getLogging();

        assertThat(logging.isEnabled()).isTrue();
        assertThat(logging.getEntryLevel()).isEqualTo("INFO");
        assertThat(logging.getExitLevel()).isEqualTo("INFO");
        assertThat(logging.getExceptionLevel()).isEqualTo("WARN");
    }

    /**
     * Тест проверяет свойства операций аудита.
     */
    @Test
    void shouldHaveCorrectOperationsProperties() {
        AuditProperties.OperationProperties operations = auditProperties.getOperations();

        assertThat(operations.isEnabled()).isFalse();
        assertThat(operations.isCreate()).isTrue();
        assertThat(operations.isUpdate()).isFalse();
        assertThat(operations.isDelete()).isTrue();
        assertThat(operations.isLogEntityContent()).isTrue();
    }
}
