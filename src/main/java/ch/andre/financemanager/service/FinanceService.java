package ch.andre.financemanager.service;

import ch.andre.financemanager.model.*;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.Month;

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

        return calculateTotalByType(
                account.getTransactions(),
                TransactionType.INCOME
        );
    }

    public BigDecimal calculateTotalIncome(
            Account account,
            Month month,
            int year
    ) {
        List<Transaction> transactions =
                getTransactionsForMonth(account, month, year);

        return calculateTotalByType(
                transactions,
                TransactionType.INCOME
        );
    }

    public BigDecimal calculateTotalIncome(
            Account account,
            int year
    ) {
        List<Transaction> transactions =
                getTransactionsForYear(account, year);

        return calculateTotalByType(
                transactions,
                TransactionType.INCOME
        );
    }



    public BigDecimal calculateTotalExpenses (
            Account account
    ) {

        Objects.requireNonNull(account, "Das Konto darf nicht null sein.");

        return calculateTotalByType(
                account.getTransactions(),
                TransactionType.EXPENSE
        );
    }

    public BigDecimal calculateTotalExpenses (
            Account account,
            Month month,
            int year
    ) {
        List<Transaction>transactions =
                getTransactionsForMonth(account, month, year);

        return calculateTotalByType(
                transactions,
                TransactionType.EXPENSE
        );
    }


    public BigDecimal calculateTotalExpenses (
            Account account,
            int year
    ) {
        List<Transaction> transactions =
                getTransactionsForYear(account, year);

        return calculateTotalByType(
                transactions,
                TransactionType.EXPENSE
        );
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

    public BigDecimal calculateNetResult(
            Account account,
            Month month,
            int year) {

        Objects.requireNonNull(
                account,
                "Das Konto darf nicht null sein."
        );
        BigDecimal income = calculateTotalIncome(account, month, year);
        BigDecimal expenses = calculateTotalExpenses(account, month, year);

        return income.subtract(expenses);
    }

    public BigDecimal calculateNetResult(
            Account account,
            int year) {

        Objects.requireNonNull(
                account,
                "Das Konto darf nicht null sein."
        );
        BigDecimal income = calculateTotalIncome(account, year);
        BigDecimal expenses = calculateTotalExpenses(account, year);

        return income.subtract(expenses);
    }

    private BigDecimal calculateTotalByType(
            List<Transaction> transactions,
            TransactionType type
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if(transaction.getType() == type) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    private List<Transaction> getTransactionsForMonth(
            Account account,
            Month month,
            int year
    ) {
        Objects.requireNonNull(
                account,
                "Das Konto darf nicht null sein."
        );

        Objects.requireNonNull(
                month,
                "Der Monat darf nicht null sein."
        );

        List<Transaction> filteredTransactions =
                new ArrayList<>();

        for (Transaction transaction : account.getTransactions()) {
            boolean isCorrectMonth =
                    transaction.getDate().getMonth() == month;

            boolean isCorrectYear =
                    transaction.getDate().getYear() == year;

            if(isCorrectMonth && isCorrectYear) {
                filteredTransactions.add(transaction);
            }
        }
        return filteredTransactions;
    }

    private List<Transaction> getTransactionsForYear (
            Account account,
            int year
    ) {
        Objects.requireNonNull(
                account,
                "Das Konto darf nicht null sein."
        );

        List<Transaction> filteredTransactions =
                new ArrayList<>();

        for (Transaction transaction : account.getTransactions()) {
            if (transaction.getDate().getYear() == year) {
                filteredTransactions.add(transaction);
            }
        }

        return filteredTransactions;
    }

    public BigDecimal calculateTotalExpenses(
            Account account,
            Category category
    ) {
        Objects.requireNonNull(
                account,
                "Das Konto darf nicht null sein."
        );

        Objects.requireNonNull(
                category,
                "Die Kategorie darf nicht null sein."
        );

        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Transaction transaction : account.getTransactions()) {
            boolean isExpense =
                    transaction.getType() == TransactionType.EXPENSE;

            boolean isCorrectCategory =
                    transaction.getCategory() == category;

            if(isExpense && isCorrectCategory) {
                totalExpenses =
                        totalExpenses.add(transaction.getAmount());
            }
        }

        return totalExpenses;

    }

    public MonthlyReport createMonthlyReport(
            Account account,
            Month month,
            int year
    ) {
        BigDecimal income = calculateTotalIncome(account, month, year);
        BigDecimal expenses = calculateTotalExpenses(account, month, year);
        BigDecimal net = calculateNetResult(account, month, year);

        return new MonthlyReport(
                account,
                month,
                year,
                income,
                expenses,
                net
        );
    }




}
