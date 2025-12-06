package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест для конфигурации OpenAPI (SpringDoc).
 * Проверяет корректность создания и настройки OpenAPI бина.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see OpenApiConfig
 */
@SpringBootTest
@TestPropertySource(properties = {
        "server.url=http://test-server:8080",
        "spring.application.name=Test Product Catalog",
        "application.version=2.0.0"
})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class OpenApiConfigTest {

    @Autowired
    private OpenAPI openAPI;

    /**
     * Тест проверяет создание OpenAPI бина.
     */
    @Test
    void shouldCreateOpenApiBean() {
        assertThat(openAPI).isNotNull();
    }

    /**
     * Тест проверяет корректность настроек информации об API.
     */
    @Test
    void shouldHaveCorrectApiInfo() {
        Info info = openAPI.getInfo();

        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Product Catalog API");
        assertThat(info.getVersion()).isEqualTo("2.0.0");
        assertThat(info.getDescription()).contains("REST API for Product Catalog Management");
    }

    /**
     * Тест проверяет наличие серверов в конфигурации.
     */
    @Test
    void shouldHaveServersConfigured() {
        assertThat(openAPI.getServers()).isNotEmpty();
        assertThat(openAPI.getServers()).hasSize(2);

        assertThat(openAPI.getServers().get(0).getUrl())
                .isEqualTo("http://test-server:8080");
        assertThat(openAPI.getServers().get(0).getDescription())
                .isEqualTo("Production Server");
    }

    /**
     * Тест проверяет наличие контактной информации.
     */
    @Test
    void shouldHaveContactInformation() {
        Info info = openAPI.getInfo();

        assertThat(info.getContact()).isNotNull();
        assertThat(info.getContact().getName()).isEqualTo("Y_Lab_Student");
        assertThat(info.getContact().getEmail()).isEqualTo("Rekynkovav@yandex.ru");
    }
}
