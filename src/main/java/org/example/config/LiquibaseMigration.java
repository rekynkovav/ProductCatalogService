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

    /**
     * Выполняет миграции базы данных с помощью Liquibase.
     * Если миграции отключены в конфигурации, выводит сообщение и завершает работу.
     *
     * @throws RuntimeException если выполнение миграций завершилось ошибкой
     */
    public static void runMigration() {
        if (!DataBaseConfig.isLiquibaseEnabled()) {
            System.out.println("Liquibase migrations are disabled");
            return;
        }
        Connection connection = null;
        try {
            connection = ConnectionManager.getInstance().getConnection();
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    DataBaseConfig.getLiquibaseChangeLog(),
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update();
            System.out.println("Liquibase migrations completed successfully");
        } catch (Exception e) {
            throw new RuntimeException("Liquibase migration failed", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
}