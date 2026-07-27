package ch.andre.financemanager.service;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.AccountType;
import ch.andre.financemanager.model.Category;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FinanceServiceTest {

    @Test
    void calculateBalanceReturnsCorrectBalance() {
        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        Transaction salary = new Transaction(
                LocalDate.of(2026,7,1),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        Transaction groceries = new Transaction(
                LocalDate.of(2026,7,5),
                new BigDecimal("52.30"),
                "Lebensmitteleinkauf",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        account.addTransaction(salary);
        account.addTransaction(groceries);

        FinanceService financeService = new FinanceService();

        BigDecimal result = financeService.calculateBalance(
                account);

        assertEquals(
                new BigDecimal("6647.70"),
                result
        );
    }

    @Test
    void calculateTotalIncomeReturnsCorrectAmount() {
        // Arrange
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
                LocalDate.of(2026,7,5),
                new BigDecimal("52.30"),
                "Lebensmitteleinkauf",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        account.addTransaction(salary);
        account.addTransaction(groceries);

        // Act
        FinanceService financeService = new FinanceService();

        BigDecimal result = financeService.calculateTotalIncome(
                account);
        // Assert
        assertEquals(
                new BigDecimal("4200.00"),
                result
        );


    }

    @Test
    void creatingTransactionWithNegativeAmountThrowsException() {
        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Transaction(
                        LocalDate.now(),
                        new BigDecimal("-50.00"),
                        "Lustige Sachen",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                )
        );
    }

    @Test
    void addingTransactionFromAnotherAccountThrowsException() {
        Account firstAccount = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Account secondAccount = new Account(
                "Sparkonto",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction transaction = new Transaction(
                LocalDate.now(),
                new BigDecimal("50.00"),
                "Testbuchung",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                secondAccount
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> firstAccount.addTransaction(transaction)
        );
    }

    @Test
    void calculateTotalExpensesReturnsExpensesForSelectedMonth() {
        Account account = new Account(
                "Lohnkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction expense1 = new Transaction(
                LocalDate.of(2026,7,1),
                new BigDecimal("100.00"),
                "Kaffee",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction expense2 = new Transaction(
                LocalDate.of(2026,7,15),
                new BigDecimal("50.00"),
                "Lebenmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction expense3 = new Transaction(
                LocalDate.of(2026,8,3),
                new BigDecimal("200.00"),
                "Sport",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        account.addTransaction(expense1);
        account.addTransaction(expense2);
        account.addTransaction(expense3);

        FinanceService financeService = new FinanceService();

        BigDecimal result =
                financeService.calculateTotalExpenses(
                        account,
                        Month.JULY,
                        2026

                );

        assertEquals(
                new BigDecimal("150.00"),
                result
        );
    }

    @Test
    void calculateTotalExpensesReturnsExpensesForSelectedYear() {
        Account account = new Account(
                "Lohnkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction expense1 = new Transaction(
                LocalDate.of(2026, 1,10),
                new BigDecimal("100.00"),
                "Lebensmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction expense2 = new Transaction(
                LocalDate.of(2026,7,15),
                new BigDecimal("50.00"),
                "Restaurant",
                TransactionType.EXPENSE,
                Category.LEISURE,
                account
        );

        Transaction expense3 = new Transaction(
                LocalDate.of(2025,12,20),
                new BigDecimal("200.00"),
                "Versicherung",
                TransactionType.EXPENSE,
                Category.INSURANCE,
                account
        );

        Transaction income = new Transaction(
                LocalDate.of(2026,7,25),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        account.addTransaction(expense1);
        account.addTransaction(expense2);
        account.addTransaction(expense3);
        account.addTransaction(income);

        FinanceService financeService = new FinanceService();

        BigDecimal result =
                financeService.calculateTotalExpenses(
                        account,
                        2026

                );

        assertEquals(
                new BigDecimal("150.00"),
                result
        );
    }

    @Test
    void calculateTotalIncomeReturnsIncomeForSelectedMonth() {
        Account account = new Account(
                "Lohnkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction expense1 = new Transaction(
                LocalDate.of(2026, 1,10),
                new BigDecimal("100.00"),
                "Lebensmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction expense2 = new Transaction(
                LocalDate.of(2026,7,15),
                new BigDecimal("50.00"),
                "Restaurant",
                TransactionType.EXPENSE,
                Category.LEISURE,
                account
        );

        Transaction expense3 = new Transaction(
                LocalDate.of(2025,12,20),
                new BigDecimal("200.00"),
                "Versicherung",
                TransactionType.EXPENSE,
                Category.INSURANCE,
                account
        );

        Transaction income = new Transaction(
                LocalDate.of(2026,7,25),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        account.addTransaction(expense1);
        account.addTransaction(expense2);
        account.addTransaction(expense3);
        account.addTransaction(income);

        FinanceService financeService = new FinanceService();

        BigDecimal result =
                financeService.calculateTotalIncome(
                        account,
                        Month.JULY,
                        2026
                );

        assertEquals(
                new BigDecimal("4200.00"),
                result
        );
    }


    @Test
    void calculateTotalNetResultReturnsForSelectedMonth() {
        Account account = new Account(
                "Lohnkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction expense1 = new Transaction(
                LocalDate.of(2026, 1,10),
                new BigDecimal("100.00"),
                "Lebensmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction expense2 = new Transaction(
                LocalDate.of(2026,7,15),
                new BigDecimal("50.00"),
                "Restaurant",
                TransactionType.EXPENSE,
                Category.LEISURE,
                account
        );

        Transaction expense3 = new Transaction(
                LocalDate.of(2025,12,20),
                new BigDecimal("200.00"),
                "Versicherung",
                TransactionType.EXPENSE,
                Category.INSURANCE,
                account
        );

        Transaction income = new Transaction(
                LocalDate.of(2026,7,25),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        Transaction income1 = new Transaction(
                LocalDate.of(2025, 12, 30),
                new BigDecimal("1000.00"),
                "Börse",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        account.addTransaction(expense1);
        account.addTransaction(expense2);
        account.addTransaction(expense3);
        account.addTransaction(income);
        account.addTransaction(income1);

        FinanceService financeService = new FinanceService();

        BigDecimal result =
                financeService.calculateNetResult(
                        account,
                        Month.JULY,
                        2026
                );

        assertEquals(
                new BigDecimal("4150.00"),
                result
        );
    }

    @Test
    void calculateTotalIncomeReturnsIncomeForSelectedYear() {
        Account account = new Account(
                "Lohnkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction expense1 = new Transaction(
                LocalDate.of(2026, 1,10),
                new BigDecimal("100.00"),
                "Lebensmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction expense2 = new Transaction(
                LocalDate.of(2026,7,15),
                new BigDecimal("50.00"),
                "Restaurant",
                TransactionType.EXPENSE,
                Category.LEISURE,
                account
        );

        Transaction expense3 = new Transaction(
                LocalDate.of(2025,12,20),
                new BigDecimal("200.00"),
                "Versicherung",
                TransactionType.EXPENSE,
                Category.INSURANCE,
                account
        );

        Transaction income = new Transaction(
                LocalDate.of(2026,7,25),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        Transaction income1 = new Transaction(
                LocalDate.of(2025, 12, 30),
                new BigDecimal("1000.00"),
                "Börse",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        account.addTransaction(expense1);
        account.addTransaction(expense2);
        account.addTransaction(expense3);
        account.addTransaction(income);
        account.addTransaction(income1);

        FinanceService financeService = new FinanceService();

        BigDecimal result =
                financeService.calculateTotalIncome(
                        account,
                        Month.JULY,
                        2026
                );

        assertEquals(
                new BigDecimal("4200.00"),
                result
        );
    }

    @Test
    void calculateTotalNetResultReturnsForSelectedYear() {
        Account account = new Account(
                "Lohnkonto",
                AccountType.CHECKING,
                BigDecimal.ZERO,
                Currency.getInstance("CHF")
        );

        Transaction expense1 = new Transaction(
                LocalDate.of(2026, 1,10),
                new BigDecimal("100.00"),
                "Lebensmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction expense2 = new Transaction(
                LocalDate.of(2026,7,15),
                new BigDecimal("50.00"),
                "Restaurant",
                TransactionType.EXPENSE,
                Category.LEISURE,
                account
        );

        Transaction expense3 = new Transaction(
                LocalDate.of(2025,12,20),
                new BigDecimal("200.00"),
                "Versicherung",
                TransactionType.EXPENSE,
                Category.INSURANCE,
                account
        );

        Transaction income = new Transaction(
                LocalDate.of(2026,7,25),
                new BigDecimal("4200.00"),
                "Monatslohn",
                TransactionType.INCOME,
                Category.SALARY,
                account
        );

        account.addTransaction(expense1);
        account.addTransaction(expense2);
        account.addTransaction(expense3);
        account.addTransaction(income);

        FinanceService financeService = new FinanceService();

        BigDecimal result =
                financeService.calculateNetResult(
                        account,
                        2026
                );

        assertEquals(
                new BigDecimal("4050.00"),
                result
        );
    }



}
