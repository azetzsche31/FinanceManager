package ch.andre.financemanager.persistence;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.AccountType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
import java.util.UUID;

public class AccountRepository {

    private final DatabaseManager databaseManager;

    public AccountRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Account account) throws SQLException {

        String sql = """
                INSERT INTO accounts (
                    id,
                    name,
                    type,
                    opening_balance,
                    currency
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, account.getId().toString());
            statement.setString(2, account.getName());
            statement.setString(3, account.getType().name());
            statement.setBigDecimal(4, account.getOpeningBalance());
            statement.setString(
                    5,
                    account.getCurrency().getCurrencyCode()
            );

            statement.executeUpdate();
        }
    }

    public Account findById(UUID id) throws SQLException {

        String sql = """
                SELECT *
                FROM accounts
                WHERE id = ?
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {


            statement.setString(1, id.toString());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    UUID loadedId = UUID.fromString(
                            resultSet.getString("id")
                    );

                    String name = resultSet.getString("name");

                    AccountType type =
                            AccountType.valueOf(
                                    resultSet.getString("type")
                            );

                    BigDecimal openingBalance =
                            resultSet.getBigDecimal("opening_balance");

                    Currency currency =
                            Currency.getInstance(
                                    resultSet.getString("currency")
                            );

                    return new Account(
                            loadedId,
                            name,
                            type,
                            openingBalance,
                            currency
                    );
                }
            }
        }
        return null;
    }
}
