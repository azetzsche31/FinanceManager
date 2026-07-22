package ch.andre.financemanager;

import ch.andre.financemanager.model.*;
import ch.andre.financemanager.service.FinanceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        Transaction salary = new Transaction(
                LocalDate.of(2026, 7, 1),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        Transaction groceries = new Transaction(
                LocalDate.of(2026, 7, 5),
                new BigDecimal("52.30"),
                "Lebensmitteleinkauf",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction rent = new Transaction(
                LocalDate.of(2026, 7, 6),
                new BigDecimal("1350.00"),
                "Monatsmiete",
                TransactionType.EXPENSE,
                Category.HOUSING,
                account
        );

        List<Transaction> transactions = List.of(
                salary,
                groceries,
                rent
        );

        FinanceService financeService = new FinanceService();

        BigDecimal balance = financeService.calculateBalance(
                account,
                transactions
        );

        System.out.println("Aktueller Kontostand: "
                + balance
                + " "
                + account.getCurrency().getCurrencyCode());


        BigDecimal totalIncome = financeService.calculateTotalIncome (account, transactions);

        BigDecimal totalExpense = financeService.calculateTotalExpense(account, transactions);

        BigDecimal netResult = financeService.calculateNetResult(account, transactions);

        String currencyCode = account.getCurrency().getCurrencyCode();

        System.out.println("Einnahmen: " + totalIncome + " " + currencyCode);
        System.out.println("Ausgaben: " + totalExpense + " " + currencyCode);
        System.out.println("Nettoergebnis: " + netResult + " " + currencyCode);
    }




}
