package ch.andre.financemanager.model;

import java.util.List;

public class CsvImportResult {

    private final List<Transaction> transactions;
    private final List<CsvImportError> errors;

    public CsvImportResult(
            List<Transaction> transactions,
            List<CsvImportError> errors
    ) {
        this.transactions = List.copyOf(transactions);
        this.errors = List.copyOf(errors);
    }

    public  List<Transaction> getTransactions() {
        return transactions;
    }

    public List<CsvImportError> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return  !errors.isEmpty();
    }

    public int getImportedCount() {
        return transactions.size();
    }

    public int getErrorCount() {
        return errors.size();
    }
}
