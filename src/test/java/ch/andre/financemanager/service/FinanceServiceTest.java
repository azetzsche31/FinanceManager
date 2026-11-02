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
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.atomic.LongAccumulator;

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

    @Test
    void shouldReturnTransactonsForMonth() {

        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        Transaction octoberTransaction =
                new Transaction(
                        LocalDate.of(2026, 10, 15),
                        new BigDecimal("50.00"),
                        "Oktober",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        Transaction novemberTransaction =
                new Transaction(
                        LocalDate.of(2026, 11, 1),
                        new BigDecimal("100.00"),
                        "November",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        account.addTransaction(octoberTransaction);
        account.addTransaction(novemberTransaction);

        FinanceService financeService =
                new FinanceService();

        List<Transaction> result =
                financeService.getTransactionsForMonth(
                        account,
                        YearMonth.of(2026, 11)
                );

        assertEquals(1, result.size());
        assertEquals(
                novemberTransaction,
                result.getFirst()
        );
    }

    @Test
    void shouldCalculateTotalIncomeForMonth() {

        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        Transaction octoberSalary =
                new Transaction(
                        LocalDate.of(2026, 10, 25),
                        new BigDecimal("4000.00"),
                        "Oktoberlohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        Transaction novemberSalary =
                new Transaction(
                        LocalDate.of(2026, 11, 25),
                        new BigDecimal("4200.00"),
                        "Novemberlohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        Transaction novemberGroceries =
                new Transaction(
                        LocalDate.of(2026, 11, 28),
                        new BigDecimal("52.30"),
                        "Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        account.addTransaction(octoberSalary);
        account.addTransaction(novemberSalary);
        account.addTransaction(novemberGroceries);

        FinanceService financeService =
                new FinanceService();

        BigDecimal result =
                financeService.calculateTotalIncomeForMonth(
                        account,
                        YearMonth.of(2026, 11)
                );

        assertEquals(
                new BigDecimal("4200.00"),
                result
        );
    }

    @Test
    void shouldCalculateTotalExpensesForMonth() {

        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        Transaction octoberGroceries =
                new Transaction(
                        LocalDate.of(2026, 10, 20),
                        new BigDecimal("80.00"),
                        "Oktober Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        Transaction novemberGroceries =
                new Transaction(
                        LocalDate.of(2026, 11, 5),
                        new BigDecimal("52.30"),
                        "November Lebenmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        Transaction novemberSalary =
                new Transaction(
                        LocalDate.of(2026, 11, 25),
                        new BigDecimal("4200.00"),
                        "Novemberlohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        account.addTransaction(octoberGroceries);
        account.addTransaction(novemberGroceries);
        account.addTransaction(novemberSalary);

        FinanceService financeService =
                new FinanceService();

        BigDecimal result =
                financeService.calculateTotalExpensesForMonth(
                        account,
                        YearMonth.of(2026, 11)
                );

        assertEquals(
                new BigDecimal("52.30"),
                result
        );
    }

    @Test
    void shouldCalculateBalanceForMonth() {

        Account account = new Account(
                "Privatkonot",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        Transaction salary =
                new Transaction(
                        LocalDate.of(2026, 11, 25),
                        new BigDecimal("4200.00"),
                        "Novemberlohn",
                        TransactionType.INCOME,
                        Category.SALARY,
                        account
                );

        Transaction groceries =
                new Transaction(
                        LocalDate.of(2026, 11, 28),
                        new BigDecimal("52.30"),
                        "Lebensmittel",
                        TransactionType.EXPENSE,
                        Category.GROCERIES,
                        account
                );

        account.addTransaction(salary);
        account.addTransaction(groceries);

        FinanceService financeService =
                new FinanceService();

        BigDecimal result =
                financeService.calculateBalanceForMonth(
                        account,
                        YearMonth.of(2026, 11)
                );

        assertEquals(
                new BigDecimal("4147.70"),
                result
        );
    }


}
