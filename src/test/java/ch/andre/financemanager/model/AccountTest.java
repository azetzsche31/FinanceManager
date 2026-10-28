package ch.andre.financemanager.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class AccountTest {

    @Test
    void transactionCanBeRemovedFromAccount() {

        Account account = new Account(
                "Privatkonto",
                AccountType.CHECKING,
                new BigDecimal("2500.00"),
                Currency.getInstance("CHF")
        );

        Transaction transaction = new Transaction(
                LocalDate.of(2026, 10, 28),
                new BigDecimal("50.00"),
                "Testausgabe",
                TransactionType.EXPENSE,
                Category.GROCERIES,
                account
        );

        account.addTransaction(transaction);

        account.removeTransaction(transaction);

        assertFalse(
                account.getTransactions().contains(transaction)
        );
    }
}
