package org.example.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

/**
 * Класс для выполнения миграций базы данных с помощью Liquibase.
 * Выполняет SQL-скрипты из changelog для приведения схемы БД к актуальному состоянию.
 */
public class LiquibaseMigration {
    private final ConnectionManager connectionManager;

    public LiquibaseMigration(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Выполняет миграции базы данных с помощью Liquibase.
     *
     * @throws RuntimeException если выполнение миграций завершилось ошибкой
     */
    public void runMigration() {
        if (!DataBaseConfig.isLiquibaseEnabled()) {
            System.out.println("Liquibase migrations are disabled");
            return;
        }

        try {
            Connection connection = connectionManager.getConnection();
            executeLiquibaseUpdate(connection);
            System.out.println("Liquibase migrations completed successfully");
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration failed", e);
        }
    }

    private void executeLiquibaseUpdate(Connection connection) throws Exception {
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        createLiquibaseInstance(database).update();

    }

    private Liquibase createLiquibaseInstance(Database database) {
        return new Liquibase(
                DataBaseConfig.getLiquibaseChangeLog(),
                new ClassLoaderResourceAccessor(),
                database
        );
    }
}