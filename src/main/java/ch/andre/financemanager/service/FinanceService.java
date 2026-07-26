package ch.andre.financemanager.service;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;

import java.math.BigDecimal;
import java.util.Objects;

public class FinanceService {

    public BigDecimal calculateBalance(
            Account account) {

        Objects.requireNonNull(account, "Das Konto darf nicht null sein.");

        BigDecimal balance = account.getOpeningBalance();

        for (Transaction transaction : account.getTransactions()) {
            balance = balance.add(transaction.getSignedAmount());
        }

        return balance;
    }


    public BigDecimal calculateTotalIncome(
            Account account) {
        Objects.requireNonNull(account, "Das Konto darf nicht null sein.");

        BigDecimal totalIncome = BigDecimal.ZERO;

        for (Transaction transaction : account.getTransactions()) {

            boolean isIncome =
                    transaction.getType() == TransactionType.INCOME;

            if(isIncome) {
                totalIncome = totalIncome.add(transaction.getAmount());
            }
        }

        return totalIncome;
    }

    public BigDecimal calculateTotalExpenses (
            Account account) {

        Objects.requireNonNull(account, "Das Konto darf nicht null sein.");

        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : account.getTransactions()) {

            boolean isExpense =
                    transaction.getType() == TransactionType.EXPENSE;

            if(isExpense) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }
        return totalExpense;
    }

    public BigDecimal calculateNetResult(
            Account account) {

        Objects.requireNonNull(
                account,
                "Das Konto darf nicht null sein."
        );
        BigDecimal income = calculateTotalIncome(account);
        BigDecimal expenses = calculateTotalExpenses(account);

        return income.subtract(expenses);
    }

}
