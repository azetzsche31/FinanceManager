package ch.andre.financemanager.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.xml.crypto.Data;
import java.awt.image.DataBuffer;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {

    @TempDir
    Path tempDirectory;


    @Test
    void connectionCanBeEstablished() throws SQLException {

        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager(databaseFile.toString());

        try (Connection connection =
                databaseManager.getConnection()) {

            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void accountsTableCanBeCreated() throws SQLException {

        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager(databaseFile.toString());

        try (Connection connection =
                     databaseManager.getConnection()) {

            databaseManager.createAccountsTable();


            try(ResultSet tables =
                        connection.getMetaData().getTables(
                        null,
                   null,
                 "accounts",
                          null
                        )) {

            assertTrue(tables.next());
        }
        }
    }


    @Test
    void transactionsTableCanBeCreated() throws SQLException {

        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager(databaseFile.toString());

        try (Connection connection =
                databaseManager.getConnection()) {

            databaseManager.createTransactionsTable();

            try (ResultSet tables =
                    connection.getMetaData().getTables(
                            null,
                            null,
                            "transactions",
                            null
                    )) {

                assertTrue(tables.next());
            }
        }
    }


}
