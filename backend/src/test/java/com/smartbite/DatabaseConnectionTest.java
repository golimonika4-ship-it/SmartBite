package com.smartbite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnectionEstablished() throws SQLException {
        assertNotNull(dataSource, "DataSource should not be null");

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Database connection should not be null");
            assertTrue(connection.isValid(5), "Database connection should be valid");

            DatabaseMetaData metaData = connection.getMetaData();
            assertNotNull(metaData, "DatabaseMetaData should not be null");

            String databaseProductName = metaData.getDatabaseProductName();
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            String catalog = connection.getCatalog();

            System.out.println("==================================================");
            System.out.println("Connected successfully to Database: " + catalog);
            System.out.println("Database Product Name: " + databaseProductName);
            System.out.println("Database Product Version: " + databaseProductVersion);
            System.out.println("==================================================");

            assertTrue(databaseProductName.toLowerCase().contains("mysql"),
                    "Database product should be MySQL, got: " + databaseProductName);
        }
    }
}
