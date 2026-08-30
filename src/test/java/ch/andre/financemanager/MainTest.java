package ch.andre.financemanager;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.persistence.AccountLoader;
import ch.andre.financemanager.persistence.AccountRepository;
import ch.andre.financemanager.persistence.DatabaseManager;
import ch.andre.financemanager.persistence.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @TempDir
    Path tempDirectory;

    @Test
    void accountIsAvailableWhenApplicationStarts() throws SQLException {

        Path databaseFile =
                tempDirectory.resolve("finance-manager.db");

        DatabaseManager databaseManager =
                new DatabaseManager(databaseFile.toString());

        databaseManager.createAccountsTable();
        databaseManager.createTransactionsTable();

        AccountRepository accountRepository =
                new AccountRepository(databaseManager);

        TransactionRepository transactionRepository =
                new TransactionRepository(databaseManager);

        AccountLoader accountLoader =
                new AccountLoader(
                        accountRepository,
                        transactionRepository
                );

        Account account =
                accountLoader.loadOrCreateDefaultAccount();

        assertNotNull(account);
    }
}
