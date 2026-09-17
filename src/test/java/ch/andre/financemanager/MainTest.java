package ch.andre.financemanager;

import ch.andre.financemanager.model.*;
import ch.andre.financemanager.persistence.AccountLoader;
import ch.andre.financemanager.persistence.AccountRepository;
import ch.andre.financemanager.persistence.DatabaseManager;
import ch.andre.financemanager.persistence.TransactionRepository;
import ch.andre.financemanager.service.CsvExportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

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

    @Test
    void importedTransactionIsSavedToDatabase() throws SQLException {

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

        Path csvFile =
                Path.of(
                        "src/test/resources/csv/transaction-import.csv"
                );

        CsvImportResult result =
                main.importTransaction(csvFile);

        Transaction importedTransaction =
                result.getTransactions().get(0);

        Transaction loadedTransaction =
                transactionRepository.findById(
                        importedTransaction.getId(),
                        account
                );

        assertNotNull(loadedTransaction);

        assertEquals(
                importedTransaction.getId(),
                loadedTransaction.getId()
        );

        assertEquals(
                importedTransaction.getDate(),
                loadedTransaction.getDate()
        );

        assertEquals(
                0,
                importedTransaction.getAmount()
                        .compareTo(loadedTransaction.getAmount())
        );

        assertEquals(
                importedTransaction.getDescription(),
                loadedTransaction.getDescription()
        );

        assertEquals(
                importedTransaction.getType(),
                loadedTransaction.getType()
        );

        assertEquals(
                importedTransaction.getCategory(),
                loadedTransaction.getCategory()
        );

    }

    @Test
    void transactionsAreExportedToVsv() throws IOException {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction transaction =
                new Transaction(
                        LocalDate.of(2026, 7, 1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        account.addTransaction(transaction);

        Path csvFile =
                tempDirectory.resolve("transactions.csv");

        CsvExportService csvExportService =
                new CsvExportService();

        csvExportService.exportTransactions(
                csvFile,
                account
        );

        assertTrue(Files.exists(csvFile));

        List<String> lines =
                Files.readAllLines(csvFile);

        assertEquals(
                "date;amount;description;type;category",
                lines.get(0)
        );

        assertEquals("2026-07-01;5000.00;Lohn;INCOME;SALARY",
                lines.get(1)
        );

    }

    @Test
    void semicolonInDescriptionIsEscaped() throws IOException {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction transaction =
                new Transaction(
                        LocalDate.of(2026,7,10),
                        new BigDecimal("45.50"),
                        "Restaurant; Abendessen",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        account.addTransactions(List.of(transaction));

        Path csvFile =
                tempDirectory.resolve("transactions.csv");

        CsvExportService csvExportService =
                new CsvExportService();

        csvExportService.exportTransactions(
                csvFile,
                account
        );

        List<String> lines =
                Files.readAllLines(csvFile);

        assertEquals(
                "2026-07-10;45.50;\"Restaurant; Abendessen\";EXPENSE;GROCERIES",
                lines.get(1)
        );
    }

    @Test
    void quotationMarkInDescriptionIsEscaped() throws IOException {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction transaction =
                new Transaction(
                        LocalDate.of(2026, 7 ,10),
                        new BigDecimal("45.50"),
                        "Restaurant \"Abendessen\"",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        account.addTransactions(List.of(transaction));

        Path csvFile =
                tempDirectory.resolve("transactions.csv");

        CsvExportService csvExportService =
                new CsvExportService();

        csvExportService.exportTransactions(
                csvFile,
                account
        );

        List<String> lines =
                Files.readAllLines(csvFile);

        assertEquals(
                "2026-07-10;45.50;\"Restaurant \"\"Abendessen\"\"\";EXPENSE;GROCERIES",
                lines.get(1)
        );
    }
}
