package ch.andre.financemanager.service;

import ch.andre.financemanager.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.classfile.instruction.StackInstruction;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvExportServiceTest {

    private CsvExportService csvExportService;
    private Account account;

    @TempDir
    Path tempDirectory;

    @BeforeEach
    void setUp() {
        csvExportService = new CsvExportService();

        account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction salary = new Transaction(
                LocalDate.of(2026, 7 ,1),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        Transaction groceries = new Transaction(
                LocalDate.of(2026, 7, 5),
                new BigDecimal("52.30"),
                "Lebensmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        account.addTransaction(salary);
        account.addTransaction(groceries);
    }


    @Test
    void exportTransactionsCreatesCsvFile()
        throws Exception {
        Path file = tempDirectory.resolve("transactions.csv");

        csvExportService.exportTransactions(file, account);

        assertTrue(Files.exists(file));

        List<String> lines = Files.readAllLines(file);

        assertEquals(3, lines.size());

        assertEquals(
                "date;amount;description;type;category",
                lines.get(0)
        );

        assertEquals(
                "2026-07-01;4200.00;Monatslohn;INCOME;SALARY",
                lines.get(1)
        );

        assertEquals(
                "2026-07-05;52.30;Lebensmittel;EXPENSE;GROCERIES",
                lines.get(2)
        );
    }

    @Test
    void exportTransactionsCreatesHeaderForEmptyAccount()
        throws IOException {

        Account emptyAccount = new Account(
                "Leeres Konto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Path file = tempDirectory.resolve("empty-transactions.csv");

        csvExportService.exportTransactions(file, emptyAccount);

        List<String> lines = Files.readAllLines(file);

        assertEquals(1, lines.size());

        assertEquals(
                "date;amount;description;type;category",
                lines.getFirst()
        );
    }


    @Test
    void exportTransactionsEscapesDescription()
        throws IOException {

        Account emptyAccount = new Account(
                "Testaccount",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction transaction = new Transaction(
                LocalDate.of(2026, 7, 1),
                new BigDecimal("52.30"),
                "Migros; Wocheneinkauf",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                emptyAccount
        );

        emptyAccount.addTransaction(transaction);

        Path file = tempDirectory.resolve("escaped-description.csv");

        csvExportService.exportTransactions(file, emptyAccount);

        List<String> lines = Files.readAllLines(file);

        assertEquals(2, lines.size());

        assertEquals(
                "2026-07-01;52.30;\"Migros; Wocheneinkauf\";EXPENSE;GROCERIES",
                lines.get(1)
        );

    }
}
