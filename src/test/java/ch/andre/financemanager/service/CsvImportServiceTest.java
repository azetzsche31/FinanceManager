package ch.andre.financemanager.service;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.AccountType;
import ch.andre.financemanager.model.CsvImportError;
import ch.andre.financemanager.model.CsvImportResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvImportServiceTest {

    private CsvImportService csvImportService;
    private Account account;

    @BeforeEach
    void setup() {
        csvImportService = new CsvImportService();

        account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );
    }

    @Test
    void importTransactionsImportsAllValidTransactions()
            throws URISyntaxException {

        URL resource = getClass()
                .getClassLoader()
                .getResource("csv/valid-transactions.csv");

        assertNotNull(
                resource,
                "Die Testdatei valid-transactions.csv wurde nicht gefunden"
        );

        Path file = Path.of(resource.toURI());

        CsvImportResult result =
                csvImportService.importTransactions(file,account);

        assertEquals(3, result.getImportedCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(0, account.getTransactions().size());
    }


    @Test
    void importTransactionsReturnsErrorForInvalidCategory()
        throws URISyntaxException {

        URL resource = getClass()
                .getClassLoader()
                .getResource("csv/invalid-category.csv");

        assertNotNull(
                resource,
                "Die Testdatei invalid-category.csv wurde nicht gefunden."
        );

        Path file = Path.of(resource.toURI());

        CsvImportResult result =
                csvImportService.importTransactions(file, account);

        assertEquals(2,result.getImportedCount());
        assertEquals(1, result.getErrorCount());

        CsvImportError error = result.getErrors().getFirst();

        assertEquals(3, error.getLineNumber());

        assertEquals("Unbekannte Kategorie: Essen",
                error.getReason()
        );
    }

    @Test
    void importTransactionsReturnsErrorForInvalidAmount()
        throws URISyntaxException {

        URL resource = getClass()
                .getClassLoader()
                .getResource("csv/invalid-amount.csv");

        assertNotNull(
                resource,
                "Die Testdatei invalid-amount.csv wurde nicht gefunden."
        );

        Path file = Path.of(resource.toURI());

        CsvImportResult result =
                csvImportService.importTransactions(file, account);

        assertEquals(2, result.getImportedCount());
        assertEquals(1, result.getErrorCount());

        CsvImportError error = result.getErrors().getFirst();

        assertEquals(2, error.getLineNumber());

        assertEquals("Unbekannter Betrag: abc",
                error.getReason()
        );
    }

    @Test
    void importTransactionsReturnsErrorForInvalidDate()
        throws URISyntaxException {

        URL resource = getClass()
                .getClassLoader()
                .getResource("csv/invalid-date.csv");

        assertNotNull(
                resource,
                "Die Testdatei invalid-date.csv wurde nicht gefunden."
        );

        Path file = Path.of(resource.toURI());

        CsvImportResult result =
                csvImportService.importTransactions(file, account);

        assertEquals(2, result.getImportedCount());
        assertEquals(1, result.getErrorCount());

        CsvImportError error = result.getErrors().getFirst();

        assertEquals(4, error.getLineNumber());

        assertEquals("Ungültiges Datum: 06.07.2026",
                error.getReason()
        );
    }

    @Test
    void importTransactionsReturnsErrorForInvalidTransactionType()
        throws URISyntaxException {

        URL resource = getClass()
                .getClassLoader()
                .getResource("csv/invalid-transactiontype.csv");

        assertNotNull(
                resource,
                "Die Testdatei invalid-transactiontype.csv wurde nicht gefunden."
        );

        Path file = Path.of(resource.toURI());

        CsvImportResult result =
                csvImportService.importTransactions(file, account);

        assertEquals(2, result.getImportedCount());
        assertEquals(1, result.getErrorCount());

        CsvImportError error = result.getErrors().getFirst();

        assertEquals(3, error.getLineNumber());

        assertEquals("Unbekannter Transaktionstyp: Ausgaben",
                error.getReason()
        );
    }



}
