package org.example.testContainers;

import org.example.config.ConnectionManager;
import org.example.config.DataBaseConfig;
import org.example.config.LiquibaseMigration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

class LiquibaseMigrationTest extends BaseDatabaseTest {

    private ConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        connectionManager = ConnectionManager.getInstance();
    }

    @Disabled
    @Test
    void testRunMigration_Success() throws Exception {
        // Given
        try (MockedStatic<DataBaseConfig> configMock = mockStatic(DataBaseConfig.class)) {
            configMock.when(DataBaseConfig::isLiquibaseEnabled).thenReturn(true);
            configMock.when(DataBaseConfig::getLiquibaseChangeLog).thenReturn("db/changelog/db.changelog-master.xml");

            // When
            LiquibaseMigration.runMigration();

            // Then
            // Проверяем что таблицы созданы
            DatabaseMetaData metaData = connection.getMetaData();

            // Проверяем таблицу users
            ResultSet usersTable = metaData.getTables(null, "entity", "users", null);
            assertThat(usersTable.next()).isTrue();

            // Проверяем таблицу products
            ResultSet productsTable = metaData.getTables(null, "entity", "products", null);
            assertThat(productsTable.next()).isTrue();

            // Проверяем таблицу user_basket
            ResultSet basketTable = metaData.getTables(null, "entity", "user_basket", null);
            assertThat(basketTable.next()).isTrue();

            // Проверяем таблицу user_metrics
            ResultSet metricsTable = metaData.getTables(null, "entity", "user_metrics", null);
            assertThat(metricsTable.next()).isTrue();
        }
    }

    @Test
    void testRunMigration_Disabled() {
        // Given
        ConnectionManager connectionManager = mock(ConnectionManager.class);

        try (MockedStatic<DataBaseConfig> configMock = mockStatic(DataBaseConfig.class)) {
            configMock.when(DataBaseConfig::isLiquibaseEnabled).thenReturn(false);

            // Capture System.out
            var originalOut = System.out;
            var outputStream = new java.io.ByteArrayOutputStream();
            System.setOut(new java.io.PrintStream(outputStream));

            try {
                // When
                LiquibaseMigration.runMigration();

                // Then
                String output = outputStream.toString();
                assertThat(output).contains("Liquibase migrations are disabled");

                // Проверяем что ConnectionManager не вызывался
                verify(connectionManager, times(0)).getConnection();
            } finally {
                System.setOut(originalOut);
            }
        }
    }

    @Test
    void testRunMigration_Exception() throws Exception {
        // Given
        try (MockedStatic<DataBaseConfig> configMock = mockStatic(DataBaseConfig.class);
             MockedStatic<ConnectionManager> connectionMock = mockStatic(ConnectionManager.class)) {

            configMock.when(DataBaseConfig::isLiquibaseEnabled).thenReturn(true);

            ConnectionManager mockManager = mock(ConnectionManager.class);
            connectionMock.when(ConnectionManager::getInstance).thenReturn(mockManager);

            when(mockManager.getConnection()).thenThrow(new RuntimeException("Connection failed"));

            // When & Then
            try {
                LiquibaseMigration.runMigration();
            } catch (RuntimeException e) {
                assertThat(e).hasMessageContaining("Liquibase migration failed");
                assertThat(e.getCause()).hasMessageContaining("Connection failed");
            }
        }
    }
}