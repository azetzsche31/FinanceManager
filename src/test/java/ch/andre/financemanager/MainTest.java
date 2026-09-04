package ch.andre.financemanager;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.Category;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;
import ch.andre.financemanager.persistence.AccountLoader;
import ch.andre.financemanager.persistence.AccountRepository;
import ch.andre.financemanager.persistence.DatabaseManager;
import ch.andre.financemanager.persistence.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @TempDir
    Path tempDirectory;

    @Test
    void accountIsAvailableWhenApplicationStarts() throws SQLException {

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

        AccountLoader accountLoader =
                new AccountLoader(
                        accountRepository,
                        transactionRepository
                );

        Account account =
                accountLoader.loadOrCreateDefaultAccount();

        assertNotNull(account);
    }

    @Test
    void transactionCanBeSavedThroughMain() throws SQLException {

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

        AccountLoader accountLoader =
                new AccountLoader(
                        accountRepository,
                        transactionRepository
                );

        Main main =
                new Main(accountLoader,
                        transactionRepository
                );

    }

    @Test
    void transactionIsSavedToDatabase() throws SQLException {

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

        AccountLoader accountLoader =
                new AccountLoader(
                        accountRepository,
                        transactionRepository
                );

        Account account =
                accountLoader.loadOrCreateDefaultAccount();

        Main main =
                new Main(
                        accountLoader,
                        transactionRepository
                );

        Transaction transaction =
                main.createTransaction(
                        LocalDate.of(2026,9,2),
                        new BigDecimal("100.00"),
                        "Testtransaction",
                        TransactionType.INCOME,
                        Category.SALARY
                );

        main.saveTransaction(transaction);

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
                transaction.getDescription(),
                loadedTransaction.getDescription()
        );

        assertEquals(
                0,
                transaction.getAmount().compareTo(
                        loadedTransaction.getAmount()
                )
        );

        assertEquals(
                transaction.getDate(),
                loadedTransaction.getDate()
        );

        assertEquals(
                transaction.getType(),
                loadedTransaction.getType()
        );

        assertEquals(
                transaction.getCategory(),
                loadedTransaction.getCategory()
        );




    }
}
