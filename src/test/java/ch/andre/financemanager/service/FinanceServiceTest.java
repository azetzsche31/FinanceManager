package ch.andre.financemanager.service;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.AccountType;
import ch.andre.financemanager.model.Category;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FinanceServiceTest {

    private FinanceService financeService;
    private Account account;


    @BeforeEach
    void setUp() {
        financeService = new FinanceService();

        account = new Account(
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
                "Lebensmittel",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        Transaction rent = new Transaction(
                LocalDate.of(2026, 7, 6),
                new BigDecimal("1350.00"),
                "Miete",
                TransactionType.EXPENSE,
                Category.HOUSING,
                account
        );

        account.addTransaction(salary);
        account.addTransaction(groceries);
        account.addTransaction(rent);
    }

    @Test
    void calculateBalanceReturnsCorrectBalance() {

        BigDecimal result = financeService.calculateBalance(
                account);

        assertEquals(
                new BigDecimal("5297.70"),
                result
        );
    }

    @Test
    void calculateTotalIncomeReturnsCorrectAmount() {

        BigDecimal result = financeService.calculateTotalIncome(
                account);

        assertEquals(
                new BigDecimal("4200.00"),
                result
        );


    }

    @Test
    void creatingTransactionWithNegativeAmountThrowsException() {
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

        BigDecimal result =
                financeService.calculateTotalExpenses(
                        account,
                        Month.JULY,
                        2026
                );

        assertEquals(
                new BigDecimal("1402.30"),
                result
        );
    }

    @Test
    void calculateTotalExpensesReturnsExpensesForSelectedYear() {

        BigDecimal result =
                financeService.calculateTotalExpenses(
                        account,
                        2026
                );
        assertEquals(
                new BigDecimal("1402.30"),
                result
        );
    }

    @Test
    void calculateTotalIncomeReturnsIncomeForSelectedMonth() {

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
    void calculateNetResultReturnsResultForSelectedMonth() {

        BigDecimal result =
                financeService.calculateNetResult(
                        account,
                        Month.JULY,
                        2026
                );

        assertEquals(
                new BigDecimal("2797.70"),
                result
        );
    }

    @Test
    void calculateTotalIncomeReturnsIncomeForSelectedYear() {

        BigDecimal result =
                financeService.calculateTotalIncome(
                        account,
                        2026
                );

        assertEquals(
                new BigDecimal("4200.00"),
                result
        );
    }

    @Test
    void calculateNetResultReturnsResultForSelectedYear() {

        BigDecimal result =
                financeService.calculateNetResult(
                        account,
                        2026
                );

        assertEquals(
                new BigDecimal("2797.70"),
                result
        );
    }

    @Test
    void calculateTotalExpensesReturnsExpensesForCategory() {

        BigDecimal result =
                financeService.calculateTotalExpenses(
                        account,
                        Category.GROCERIES
                );

        assertEquals(
                new BigDecimal("52.30"),
                result
        );
    }


}
