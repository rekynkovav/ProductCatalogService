package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест для проверки загрузки контекста Spring Boot приложения.
 * Проверяет, что все необходимые бины создаются корректно и контекст приложения загружается.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see SpringBootTest
 */
@SpringBootTest
@ActiveProfiles("test")
class ProductCatalogApplicationTests {

    /**
     * Тест проверяет успешную загрузку контекста Spring Boot.
     * Если контекст не загружается, тест падает с исключением.
     */
    @Test
    void contextLoads() {
        // Context loading is successful if no exception is thrown
        assertThat(true).isTrue(); // Простая проверка для демонстрации
    }

    /**
     * Тест проверяет, что основное приложение успешно запускается.
     * Может использоваться для проверки базовой функциональности.
     */
    @Test
    void applicationStartsSuccessfully() {
        ProductCatalogApplication.main(new String[]{});
        // Если метод main не выбрасывает исключений, тест проходит
    }
}
