package ch.andre.financemanager.persistence;

import java.sql.*;

public class DatabaseManager {

    private final String url;

    public DatabaseManager(String databasePath) {
        this.url = "jdbc:sqlite:" + databasePath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void createAccountsTable() throws SQLException {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                         CREATE TABLE IF NOT EXISTS accounts (
                         id TEXT PRIMARY KEY,
                         name TEXT NOT NULL,
                         type TEXT NOT NULL,
                         opening_balance DECIMAL NOT NULL,
                         currency TEXT NOT NULL
                         );
                    """
            );
        }
    }

    public void createTransactionsTable() throws SQLException {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS transactions (
                    id TEXT PRIMARY KEY,
                    account_id TEXT NOT NULL,
                    date TEXT NOT NULL,
                    amount DECIMAL NOT NULL,
                    description TEXT NOT NULL,
                    type TEXT NOT NULL,
                    category TEXT NOT NULL,
                    FOREIGN KEY (account_id) REFERENCES accounts(id)
                    );
                    """
            );

        }
    }
}
