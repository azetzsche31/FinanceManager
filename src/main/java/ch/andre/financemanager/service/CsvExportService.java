package ch.andre.financemanager.service;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvExportService {

    public void exportTransactions(
            Path file,
            Account account
    ) throws IOException {
        Objects.requireNonNull(
                file,
                "Der Dateipfad darf nicht null sein."
        );

        Objects.requireNonNull(
                account,
                "Das Konto darf nicht null sein."
        );

        List<String> lines = new ArrayList<>();

        lines.add(
                "date;amount;description;type;category"
        );

        for (Transaction transaction : account.getTransactions()) {
            String line = createCsvLine(transaction);
            lines.add(line);
        }

        Files.write(file, lines);
    }

    private String createCsvLine(Transaction transaction) {
        return "%s;%s;%s;%s;%s".formatted(
                transaction.getDate(),
                transaction.getAmount(),
                escapeCsvValue(transaction.getDescription()),
                transaction.getType(),
                transaction.getCategory()
        );
    }

    private String escapeCsvValue(String value) {
        if (value.contains(";") || value.contains("\"")) {
            String escapeValue = value.replace("\"", "\"\"" );
            return "\"" + escapeValue + "\"";
        }

        return value;
    }
}
