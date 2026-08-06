package ch.andre.financemanager.service;

import ch.andre.financemanager.model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvImportExportTest {

    private CsvExportService csvExportService;
    private CsvImportService csvImportService;

    private Account exportAccount;
    private Account importAccount;


    @TempDir
    Path tempDirectory;

    @BeforeEach
    void setUp() {
        csvExportService = new CsvExportService();
        csvImportService = new CsvImportService();

        exportAccount = new Account(
                "Exprotkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        importAccount = new Account(
                "Importkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );
    }

    @Test
    void exportedTransactionsCanBeImportedAgain()
        throws IOException {

        Transaction transaction = new Transaction(
                LocalDate.of(2026, 7, 1),
                new BigDecimal("52.30"),
                "Migros; Wocheneinkauf",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                exportAccount
        );

        exportAccount.addTransaction(transaction);

        Path file =
                tempDirectory.resolve("transaction.csv");

        csvExportService.exportTransactions(
                file,
                exportAccount
        );

        CsvImportResult result =
                csvImportService.importTransactions(
                        file,
                        importAccount
                );

        assertEquals(0, result.getErrorCount());

        assertEquals(1, result.getImportedCount());


        Transaction importedTransaction =
                result.getTransactions().getFirst();

        assertEquals(
                "Migros; Wocheneinkauf",
                importedTransaction.getDescription()
        );

        assertEquals(new BigDecimal("52.30"),
                importedTransaction.getAmount()
        );

        assertEquals(
                TransactionType.EXPENSE,
                importedTransaction.getType()
        );

        assertEquals(
                Category.GROCERIES,
                importedTransaction.getCategory()
        );
    }
}
