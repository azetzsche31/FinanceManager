package ch.andre.financemanager.persistence;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.Transaction;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AccountLoader {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountLoader(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository
    ) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    public Account loadAccount(UUID id) throws SQLException {

        Account account =
                accountRepository.findById(id);

        if (account == null) {
            return null;
        }

        List<Transaction> transactions =
                transactionRepository.findByAccount(account);

        account.addTransactions(transactions);

        return account;
    }
}
