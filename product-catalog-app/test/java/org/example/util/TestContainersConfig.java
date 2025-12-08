package com.productcatalogservice.util;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Конфигурация Testcontainers для интеграционных тестов.
 * Создает и управляет контейнерами с базами данных для тестирования.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see TestConfiguration
 */
@TestConfiguration
public class TestContainersConfig {

    /**
     * Создает контейнер PostgreSQL для интеграционных тестов.
     * Использует легковесный образ PostgreSQL 15 Alpine.
     *
     * @return контейнер PostgreSQL
     */
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);  // Позволяет переиспользовать контейнер между тестами
    }

    /**
     * Создает контейнер PostgreSQL с кастомными настройками.
     *
     * @param databaseName имя базы данных
     * @param username имя пользователя
     * @param password пароль
     * @return контейнер PostgreSQL с указанными настройками
     */
    public static PostgreSQLContainer<?> createPostgreSQLContainer(
            String databaseName, String username, String password) {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
                .withDatabaseName(databaseName)
                .withUsername(username)
                .withPassword(password)
                .withInitScript("init-test.sql")  // Можно добавить скрипт инициализации
                .withReuse(false);  // Не переиспользуем для изоляции тестов
    }
}
