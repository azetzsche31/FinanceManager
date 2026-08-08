package ch.andre.financemanager.persistence;

import ch.andre.financemanager.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRepositoryTest {

    @TempDir
    Path tempDirectory;

    @Test
    void transactionCanBeSaved() throws SQLException {
        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager(databaseFile.toString());

        databaseManager.createAccountsTable();

        databaseManager.createTransactionsTable();

        AccountRepository accountRepository =
                new AccountRepository(databaseManager);

        TransactionRepository transactionRepository =
                new TransactionRepository(databaseManager);

        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        accountRepository.save(account);

        Transaction transaction = new Transaction(
                LocalDate.of(2026,7,1),
                new BigDecimal("5000.00"),
                "Lohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        transactionRepository.save(transaction);

        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT * FROM transactions"
             )) {

            assertTrue(resultSet.next());

            assertEquals(
                    transaction.getId().toString(),
                    resultSet.getString("id")
            );

            assertEquals(
                    account.getId().toString(),
                    resultSet.getString("account_id")
            );

            assertEquals(
                    "2026-07-01",
                    resultSet.getString("date")
            );

            assertEquals(
                    0,
                    new BigDecimal("5000.00")
                            .compareTo(resultSet.getBigDecimal("amount"))
            );

            assertEquals(
                    "Lohn",
                    resultSet.getString("description")
            );

            assertEquals(
                    "INCOME",
                    resultSet.getString("type")
            );

            assertEquals(
                    "SALARY",
                    resultSet.getString("category")
            );

            assertFalse(resultSet.next());
        }
    }


    @Test

void transactionCanBeLoaded() throws SQLException {

    Path databaseFile =
            tempDirectory.resolve("finance-manager.db");

    DatabaseManager databaseManager =
            new DatabaseManager(databaseFile.toString());

    databaseManager.createAccountsTable();
    databaseManager.createTransactionsTable();

    AccountRepository accountRepository =
            new AccountRepository(databaseManager);

    TransactionRepository transactionRepository =
            new TransactionRepository(databaseManager);

    Account account = new Account(
            "Privatkonto",
            AccountType.CHECKING,
            new BigDecimal("2500.00"),
            Currency.getInstance("CHF")
    );

    accountRepository.save(account);

    Transaction transaction = new Transaction(
            LocalDate.of(2026,7,1),
            new BigDecimal("5000.00"),
            "Lohn",
            TransactionType.INCOME,
            Category.SALARY,
            account
    );

    transactionRepository.save(transaction);

    Transaction loadedTransaction =
            transactionRepository.findById(
                    transaction.getId(),
                    account
            );

    assertNotNull(loadedTransaction);

    assertEquals(
            transaction.getId(),
            loadedTransaction.getId()
    );

    assertEquals(
            transaction.getDate(),
            loadedTransaction.getDate()
    );

    assertEquals(
            0,
            transaction.getAmount().compareTo(loadedTransaction.getAmount())
    );

    assertEquals(
            transaction.getDescription(),
            loadedTransaction.getDescription()
    );

    assertEquals(transaction.getType(),
            loadedTransaction.getType()
    );

    assertEquals(transaction.getCategory(),
            loadedTransaction.getCategory()
    );

    assertEquals(account.getId(),
            loadedTransaction.getAccount().getId()
    );
    }
}
