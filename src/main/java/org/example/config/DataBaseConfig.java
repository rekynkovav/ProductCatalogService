package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Конфигурация базы данных и настроек Liquibase.
 * Загружает настройки из файла liquibase.properties или использует значения по умолчанию.
 */
public class DataBaseConfig {

    /**
     * Свойства конфигурации базы данных.
     */
    private static Properties properties = new Properties();

    /**
     * Статический блок инициализации конфигурации.
     * Загружает настройки из liquibase.properties.
     */
    static {
        try (InputStream inputStream = DataBaseConfig.class.getClassLoader()
                .getResourceAsStream("liquibase.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("IOException for load liquibase.properties");
        }
    }

    /**
     * Возвращает URL базы данных.
     *
     * @return URL для подключения к БД
     */
    public static String getUrl() {
        return properties.getProperty("database.url");
    }

    /**
     * Возвращает пароль для подключения к базе данных.
     *
     * @return пароль БД
     */
    public static String getPassword() {
        return properties.getProperty("database.password");
    }

    /**
     * Возвращает имя пользователя для подключения к базе данных.
     *
     * @return имя пользователя БД
     */
    public static String getUserName() {
        return properties.getProperty("database.username");
    }

    /**
     * Возвращает класс драйвера базы данных.
     *
     * @return полное имя класса драйвера JDBC
     */
    public static String getDriver() {
        return properties.getProperty("database.driver");
    }

    /**
     * Проверяет включены ли миграции Liquibase.
     *
     * @return true если миграции Liquibase включены, иначе false
     */
    public static boolean isLiquibaseEnabled() {
        return Boolean.parseBoolean(properties.getProperty("liquibase.enabled"));
    }

    /**
     * Возвращает путь к файлу changelog Liquibase.
     *
     * @return путь к основному файлу миграций
     */
    public static String getLiquibaseChangeLog() {
        return properties.getProperty("liquibase.change-log");
    }
}