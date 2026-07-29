package ch.andre.financemanager.service;

import ch.andre.financemanager.model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CsvImportService {

    public CsvImportResult importTransactions(
            Path file,
            Account account
    ) {
        List<Transaction> transactions = new ArrayList<>();
        List<CsvImportError> errors = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(file);

            for (int index = 1; index < lines.size() ; index++) {
                String line = lines.get(index);
                int lineNumber = index + 1;

                try {
                    Transaction transaction =
                            createTransactionFromLine(line, account);

                    transactions.add(transaction);
                } catch (IllegalArgumentException exception) {
                    errors.add(
                            new CsvImportError(
                                    lineNumber,
                                    line,
                                    exception.getMessage()
                            )
                    );
                }

            }
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "Die CSV-Datei konnte nicht gelesen werden.",
                    exception
            );
        }

        return new CsvImportResult(
                transactions,
                errors
        );
    }

    private String cleanValue(String value) {
        return value.trim().replace("\"", "");
    }

    private Transaction createTransactionFromLine(
            String line,
            Account account
    ) {
        String[] values = line.split(";");

        LocalDate date = parseDate(values[0]);
        BigDecimal amount = parseAmount(values[1]);
        String description = cleanValue(values[2]);
        TransactionType type = parseTransactionType(values[3]);
        Category category = parseCategory(values[4]);

        return new Transaction(
                date,
                amount,
                description,
                type,
                category,
                account
        );
    }

    private Category parseCategory(String value) {
        String cleanedValue = cleanValue(value);

        try {
            return Category.valueOf(cleanedValue.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unbekannte Kategorie: " + cleanedValue
            );
        }
    }

    private BigDecimal parseAmount(String value) {
        String cleanedValue = cleanValue(value);

        try {
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Unbekannter Betrag: " + cleanedValue,
                    e
            );
        }
    }

    private LocalDate parseDate(String value) {
        String cleanedValue = cleanValue(value);

        try {
            return LocalDate.parse(cleanedValue);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Ungültiges Datum: " + cleanedValue,
                    e
            );
        }
    }

    private TransactionType parseTransactionType(String value) {
        String cleanedValue = cleanValue(value);

        try {
            return TransactionType.valueOf(cleanedValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unbekannter Transaktionstyp: " + cleanedValue,
                    e
            );
        }
    }


}
