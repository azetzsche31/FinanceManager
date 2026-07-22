package ch.andre.financemanager.service;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class FinanceService {

    public BigDecimal calculateBalance(
            Account account,
            List<Transaction> transactions
    ) {
        Objects.requireNonNull(account, "Das Konto darf nicht null sein.");
        Objects.requireNonNull(transactions, "Die Buchungsliste darf nicht null sein.");

        BigDecimal balance = account.getOpeningBalance();

        for (Transaction transaction : transactions) {
            if (belongsToAccount(transaction, account)) {
                balance = balance.add(transaction.getSignedAmount());
            }
        }

        return balance;
    }


    public BigDecimal calculateTotalIncome(
            Account account,
            List<Transaction> transactions
    ) {
        Objects.requireNonNull(account, "Das Konto darf nicht null sein.");
        Objects.requireNonNull(transactions, "Die Buchungsliste darf nicht null sein.");

        BigDecimal totalIncome = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            boolean isIncome =
                    transaction.getType() == TransactionType.INCOME;

            if (belongsToAccount(transaction, account) && isIncome) {
                totalIncome = totalIncome.add(transaction.getAmount());
            }
        }

        return totalIncome;
    }

    public BigDecimal calculateTotalExpense (
            Account account,
            List<Transaction> transactions
    ) {
        Objects.requireNonNull(account, "Das Konto darf nicht null sein.");
        Objects.requireNonNull(transactions, "Die Buchungsliste darf nicht null sein.");

        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            boolean isExpense =
                    transaction.getType() == TransactionType.EXPENSE;

            if (belongsToAccount(transaction, account) && isExpense) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        return totalExpense;
    }

    public BigDecimal calculateNetResult(
            Account account,
            List<Transaction> transactions
    ) {
        BigDecimal income = calculateTotalIncome(account, transactions);
        BigDecimal expense = calculateTotalExpense(account, transactions);

        return income.subtract(expense);
    }

    private boolean belongsToAccount(Transaction transaction, Account account){
        return transaction.getAccount().getId().equals(account.getId());
    }

}
