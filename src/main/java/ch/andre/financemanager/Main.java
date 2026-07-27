package ch.andre.financemanager;

import ch.andre.financemanager.model.*;
import ch.andre.financemanager.service.FinanceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;

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

        account.addTransaction(salary);
        account.addTransaction(groceries);
        account.addTransaction(rent);


        FinanceService financeService = new FinanceService();

        MonthlyReport julyReport =
                financeService.createMonthlyReport(
                        account,
                        Month.JULY,
                        2026
                );

        System.out.println();
        System.out.println(julyReport);
    }






}
