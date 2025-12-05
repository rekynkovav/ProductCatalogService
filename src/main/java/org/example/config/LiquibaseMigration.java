package org.example.config;

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

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiquibaseMigration {

    private final DataSource dataSource;
    private final Environment environment;

    @Value("${liquibase.enabled:true}")
    private boolean liquibaseEnabled;

    @Value("${liquibase.change-log:classpath:db/changelog/db.changelog-master.xml}")
    private String changelogPath;

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