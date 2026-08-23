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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AccountRepositoryTest {

    @TempDir
    Path tempDirectory;



    @Test
    void accountCanBeSaved() throws SQLException {
        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager(databaseFile.toString());

        databaseManager.createAccountsTable();

        AccountRepository accountRepository =
                new AccountRepository(databaseManager);

        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        accountRepository.save(account);

        try(Connection connection = databaseManager.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM accounts"
            )) {
            assertTrue(resultSet.next());

            assertEquals(
                    account.getId().toString(),
                    resultSet.getString("id")
            );

            assertEquals(
                    "Privatkonto",
                    resultSet.getString("name")
            );

            assertEquals(
                    "CHECKING",
                    resultSet.getString("type")
            );

            assertEquals(
                    0,
                    new BigDecimal("2500.00")
                            .compareTo(resultSet.getBigDecimal("opening_balance"))
            );

            assertEquals(
                    "CHF",
                    resultSet.getString("currency")
            );

            assertFalse(resultSet.next());
        }
    }

    @Test
    void accountCanBeLoaded() throws SQLException {

        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager(databaseFile.toString());

        databaseManager.createAccountsTable();

        AccountRepository accountRepository =
                new AccountRepository(databaseManager);

        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        accountRepository.save(account);

        Account loadedAccount =
                accountRepository.findById(account.getId());

        assertNotNull(loadedAccount);

        assertEquals(
                account.getId(),
                loadedAccount.getId()
        );
        assertEquals(
                account.getName(),
                loadedAccount.getName()
        );
        assertEquals(
                account.getType(),
                loadedAccount.getType()
        );
        assertEquals(
                0,
                account.getOpeningBalance()
                        .compareTo(loadedAccount.getOpeningBalance())
        );
        assertEquals(
                account.getCurrency(),
                loadedAccount.getCurrency()
        );

    }

    @Test
    void accountCanBeLoadedWithTransactions() throws SQLException {

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

        Transaction salary = new Transaction(
                LocalDate.of(2026,8,1),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        Transaction groceries = new Transaction(
                LocalDate.of(2026,8,5),
                new BigDecimal("85.40"),
                "Wocheneinkauf",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        transactionRepository.save(salary);
        transactionRepository.save(groceries);

        Account loadedAccount =
                accountRepository.findById(account.getId());

        List<Transaction> transactions =
                transactionRepository.findByAccount(loadedAccount);

        loadedAccount.addTransactions(transactions);

        assertNotNull(loadedAccount);

        assertEquals(
                account.getId(),
                loadedAccount.getId()
        );

        assertEquals(
                account.getName(),
                loadedAccount.getName()
        );

        assertEquals(
                2,
                loadedAccount.getTransactions().size()
        );

        assertEquals(
                salary.getId(),
                loadedAccount.getTransactions().get(0).getId()
        );

        assertEquals(
                groceries.getId(),
                loadedAccount.getTransactions().get(1).getId()
        );

        assertEquals(
                loadedAccount.getId(),
                loadedAccount.getTransactions()
                        .get(0)
                        .getAccount()
                        .getId()
        );
    }
}
