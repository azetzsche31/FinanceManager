package ch.andre.financemanager.service;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.AccountType;
import ch.andre.financemanager.model.Category;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
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
}
