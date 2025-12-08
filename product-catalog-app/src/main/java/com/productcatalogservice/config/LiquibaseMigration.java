package com.productcatalogservice.config;

import jakarta.annotation.PostConstruct;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Компонент для выполнения миграций базы данных с помощью Liquibase.
 * Автоматически запускается при инициализации контекста Spring.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Выполнение SQL миграций из changelog файлов</li>
 *   <li>Отслеживание выполненных миграций в таблице databasechangelog</li>
 *   <li>Возможность отключения миграций через конфигурацию</li>
 * </ul>
 *
 * @apiNote Миграции выполняются только один раз при запуске приложения
 * @see Component
 * @see PostConstruct
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LiquibaseMigration {

    /**
     * Источник данных для подключения к БД.
     */
    private final DataSource dataSource;

    /**
     * Окружение Spring для доступа к свойствам.
     */
    private final Environment environment;

    /**
     * Флаг включения/отключения миграций Liquibase. По умолчанию true.
     */
    @Value("${liquibase.enabled:true}")
    private boolean liquibaseEnabled;

    /**
     * Путь к основному changelog файлу Liquibase.
     */
    @Value("${liquibase.change-log:classpath:db/changelog/db.changelog-master.xml}")
    private String changelogPath;

    /**
     * Метод, выполняющий миграции базы данных.
     * Вызывается автоматически после инициализации бина.
     *
     * @throws RuntimeException если миграция не удалась
     * @apiNote Если liquibase.enabled=false, миграции пропускаются
     */
    @PostConstruct
    public void runMigration() {
        if (!liquibaseEnabled) {
            log.info("Liquibase migrations are disabled (liquibase.enabled = false)");
            return;
        }

        try (Connection connection = dataSource.getConnection()) {
            log.info("Starting Liquibase migrations...");
            log.info("Using changelog: " + changelogPath);
            log.info("Database URL: " + environment.getProperty("database.url"));

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            database.setDefaultSchemaName(environment.getProperty("liquibase.default-schema"));

            Liquibase liquibase = new Liquibase(
                    changelogPath,
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update("");
            log.info("✅ Liquibase migrations completed successfully");
        } catch (SQLException | LiquibaseException e) {
            log.error("❌ Liquibase migration failed");
            throw new RuntimeException("Liquibase migration failed", e);
        }
    }
}