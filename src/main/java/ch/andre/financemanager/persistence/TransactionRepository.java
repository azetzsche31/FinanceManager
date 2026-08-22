package ch.andre.financemanager.persistence;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.Category;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TransactionRepository {

    private final DatabaseManager databaseManager;

    public TransactionRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Transaction transaction) throws SQLException {

        String sql = """
                INSERT INTO transactions (
                    id,
                    account_id,
                    date,
                    amount,
                    description,
                    type,
                    category
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, transaction.getId().toString());
            statement.setString(2, transaction.getAccount().getId().toString());
            statement.setString(3, transaction.getDate().toString());
            statement.setBigDecimal(4, transaction.getAmount());
            statement.setString(5, transaction.getDescription());
            statement.setString(6, transaction.getType().name());
            statement.setString(7, transaction.getCategory().name());

            statement.executeUpdate();
        }
    }

    public Transaction findById (
            UUID id,
            Account account
    ) throws SQLException {

        String sql = """
                SELECT *
                FROM transactions
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

                    LocalDate date = LocalDate.parse(
                            resultSet.getString("date")
                    );

                    BigDecimal amount =
                            resultSet.getBigDecimal("amount");

                    String description =
                            resultSet.getString("description");

                    TransactionType type =
                            TransactionType.valueOf(
                                    resultSet.getString("type")
                            );

                    Category category =
                            Category.valueOf(
                                    resultSet.getString("category")
                            );

                    return new Transaction(
                            loadedId,
                            date,
                            amount,
                            description,
                            type,
                            category,
                            account
                    );


                }
            }

        }
        return null;
    }


    public List<Transaction> findByAccount(Account account) throws SQLException {

        List<Transaction> transactions =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM transactions
                WHERE account_id = ?
                ORDER BY date
                """;

        try(Connection connection = databaseManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    account.getId().toString()
            );

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    UUID id = UUID.fromString(
                        resultSet.getString("id")
                    );

                    LocalDate date = LocalDate.parse(
                            resultSet.getString("date")
                    );

                    BigDecimal amount =
                            resultSet.getBigDecimal("amount");

                    String description =
                            resultSet.getString("description");

                    TransactionType type =
                        TransactionType.valueOf(
                            resultSet.getString("type")
                         );

                    Category category =
                            Category.valueOf(
                                    resultSet.getString("category")
                            );

                    Transaction transaction =
                            new Transaction(
                                    id,
                                    date,
                                    amount,
                                    description,
                                    type,
                                    category,
                                    account
                            );

                    transactions.add(transaction);


                }
            }
        }

        return transactions;

    }


}
