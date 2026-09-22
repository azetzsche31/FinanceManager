package ch.andre.financemanager;

import ch.andre.financemanager.model.*;
import ch.andre.financemanager.persistence.AccountLoader;
import ch.andre.financemanager.persistence.AccountRepository;
import ch.andre.financemanager.persistence.DatabaseManager;
import ch.andre.financemanager.persistence.TransactionRepository;
import ch.andre.financemanager.service.CsvExportService;
import ch.andre.financemanager.service.FinanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
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

    @Test
    void monthlyReportContainsCorrectValues() {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction salary =
                new Transaction(
                        LocalDate.of(2026,7,1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        Transaction groceries =
                new Transaction(
                        LocalDate.of(2026,7,5),
                        new BigDecimal("250.00"),
                        "Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        Transaction augustSalary =
                new Transaction(
                        LocalDate.of(206,8,1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        account.addTransactions(
                List.of(
                salary,
                groceries,
                augustSalary
                )
        );

        FinanceService financeService =
                new FinanceService();


        MonthlyReport report =
                financeService.createMonthlyReport(
                        account,
                        Month.JULY,
                        2026
                );

        assertEquals(
                account,
                report.getAccount()
        );

        assertEquals(
                Month.JULY,
                report.getMonth()
        );

        assertEquals(
                2026,
                report.getYear()
        );

        assertEquals(
                0,
                new BigDecimal("5000.00")
                        .compareTo(report.getTotalIncome())
        );

        assertEquals(
                0,
                new BigDecimal("250.00")
                        .compareTo(report.getTotalExpenses())
        );

        assertEquals(
                0,
                new BigDecimal("4750.00")
                        .compareTo(report.getNetResult())
        );
    }


    @Test
    void yearlyReportContainsCorrectValues() {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction salary =
                new Transaction(
                        LocalDate.of(2026,7,1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        Transaction groceries =
                new Transaction(
                        LocalDate.of(2026,7,4),
                        new BigDecimal("250.00"),
                        "Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        Transaction augustSalary =
                new Transaction(
                        LocalDate.of(2026,8,1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        Transaction groceries2025 =
                new Transaction(
                        LocalDate.of(2025,7,2),
                        new BigDecimal("300.00"),
                        "Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        account.addTransactions(
                List.of(
                 salary,
                 groceries,
                 augustSalary,
                 groceries2025
                )
        );

        FinanceService financeService =
                new FinanceService();

        YearlyReport report =
                financeService.createYearlyReport(
                        account,
                        2026
                );

        assertEquals(
                account,
                report.getAccount()
        );

        assertEquals(
                2026,
                report.getYear()
        );

        assertEquals(
                0,
                new BigDecimal("10000.00")
                        .compareTo(report.getTotalIncome())
        );

        assertEquals(
                0,
                new BigDecimal("250.00")
                        .compareTo(report.getTotalExpenses())
        );

        assertEquals(
                0,
                new BigDecimal("9750.00")
                        .compareTo(report.getNetResult())
        );
    }

    @Test
    void monthlyReportWithNoTransactionsContainsZeroValues() {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction julySalary =
                new Transaction(
                        LocalDate.of(2026,7,1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        account.addTransactions(List.of(julySalary));

        FinanceService financeService =
                new FinanceService();

        MonthlyReport report =
                financeService.createMonthlyReport(
                        account,
                        Month.SEPTEMBER,
                        2026
                );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        report.getTotalIncome()
                )
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        report.getTotalExpenses()
                )
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        report.getNetResult()
                )
        );
    }

    @Test
    void yearlyReportWithNoTransactionsContainsZeroValues() {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction julySalary =
                new Transaction(
                        LocalDate.of(2026,7,1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        account.addTransactions(List.of(julySalary));

        FinanceService financeService =
                new FinanceService();

        YearlyReport report =
                financeService.createYearlyReport(
                        account,
                        2025
                );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        report.getTotalIncome()
                )
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        report.getTotalExpenses()
                )
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        report.getNetResult()
                )
        );
    }


    @Test
    void totalExpensesForCategoryContainsOnlyMatchingExpenses() {

        Account account =
                new Account(
                        "Testkonto",
                        AccountType.CHECKING,
                        BigDecimal.ZERO,
                        Currency.getInstance("CHF")
                );

        Transaction groceries =
                new Transaction(
                        LocalDate.of(2026,7,1),
                        new BigDecimal("50.00"),
                        "Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        Transaction groceries2 =
                new Transaction(
                        LocalDate.of(2026,7,5),
                        new BigDecimal("30.00"),
                        "Weitere Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        Transaction rent =
                new Transaction(
                        LocalDate.of(2026,7,1),
                        new BigDecimal("1500.00"),
                        "Miete",
                        TransactionType.EXPENSE,
                        Category.HOUSING,
                        account
                );

        Transaction salary =
                new Transaction(
                        LocalDate.of(2026,7,1),
                        new BigDecimal("5000.00"),
                        "Lohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        account.addTransactions(
                List.of(
                groceries,
                groceries2,
                rent,
                salary
                )
        );

        FinanceService financeService =
                new FinanceService();

        BigDecimal total =
                financeService.calculateTotalExpenses(
                        account,
                        Category.GROCERIES
                );

        assertEquals(
                0,
                new BigDecimal("80.00")
                        .compareTo(total)
        );


    }
}
