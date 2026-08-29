package ch.andre.financemanager.persistence;

import ch.andre.financemanager.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.DataBuffer;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AccountLoaderTest {

    @TempDir
    Path tempDirectory;

    @Test
    void accountCanBeLoadedCompletely() throws SQLException {

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

        transactionRepository.save(salary);

        AccountLoader accountLoader =
                new AccountLoader(
                        accountRepository,
                        transactionRepository
                );

        Account loadedAccount =
                accountLoader.loadAccount(account.getId());

        assertNotNull(loadedAccount);

        assertEquals(
                account.getId(),
                loadedAccount.getId()
        );

        assertEquals(
                1,
                loadedAccount.getTransactions().size()
        );

        assertEquals(
                salary.getId(),
                loadedAccount.getTransactions()
                        .get(0)
                        .getId()
        );
    }

    @Test
    void loadingUnknownAccountReturnsNull() throws SQLException {

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

        UUID unknownId = UUID.randomUUID();

        Account loadedAccount =
                accountLoader.loadAccount(unknownId);

        assertNull(loadedAccount);
    }

    @Test
    void defaultAccountIsCreatedWhenNoAccountExists() throws SQLException {

        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager((databaseFile.toString()));

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

        assertEquals(
                "Privatkonto",
                account.getName()
        );

        assertEquals(
                AccountType.CHECKING,
                account.getType()
        );

        assertEquals(
                BigDecimal.ZERO,
                account.getOpeningBalance()
        );

        assertEquals(
                Currency.getInstance("CHF"),
                account.getCurrency()
        );
    }

    @Test
    void existingAccountIsLoadedInsteadOfCreatingNewOne() throws SQLException {

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

        Account firstAccount =
                accountLoader.loadOrCreateDefaultAccount();

        Account secondAccount =
                accountLoader.loadOrCreateDefaultAccount();

        assertNotNull(firstAccount);
        assertNotNull(secondAccount);

        assertEquals(
                firstAccount.getId(),
                secondAccount.getId()
        );
    }

    @Test
    void existingDefaultAccountIsLoadedWithTransactions() throws SQLException {

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

        //Default-Account beeim ersten Start erzeugen
        Account account =
                accountLoader.loadOrCreateDefaultAccount();

        // Eine Transaction für diesen Account anlagen
        Transaction transaction = new Transaction(
                LocalDate.of(2026, 8, 29),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        transactionRepository.save(transaction);

        // Account erneut laden
        Account loadedAccount =
                accountLoader.loadOrCreateDefaultAccount();

        assertNotNull(loadedAccount);

        assertEquals(
                account.getId(),
                loadedAccount.getId()
        );

        assertEquals(
                1,
                loadedAccount.getTransactions().size()
        );

        Transaction loadedTransaction =
                loadedAccount.getTransactions().get(0);

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
                transaction.getAmount()
                        .compareTo(loadedTransaction.getAmount())
        );

    }
}
