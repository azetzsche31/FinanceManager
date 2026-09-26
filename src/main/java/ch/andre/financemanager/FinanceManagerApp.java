package ch.andre.financemanager;

import ch.andre.financemanager.model.AccountType;
import ch.andre.financemanager.model.Transaction;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.persistence.AccountLoader;
import ch.andre.financemanager.persistence.AccountRepository;
import ch.andre.financemanager.persistence.DatabaseManager;
import ch.andre.financemanager.persistence.TransactionRepository;

import java.sql.SQLException;

import ch.andre.financemanager.service.FinanceService;

import java.math.BigDecimal;

public class FinanceManagerApp extends Application{

    @Override
    public void start(Stage stage) throws  SQLException {
        FinanceService financeService = new FinanceService();

        DatabaseManager databaseManager =
                new DatabaseManager("finanace-manager.db");

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

        Account account = accountLoader.loadOrCreateDefaultAccount();

        BorderPane root = new BorderPane();

        Label titleLabel = new Label("Finanace Manager");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox header = new HBox(titleLabel);
        header.setPadding(new Insets(20));

        root.setTop(header);

        Label balanceTitle = new Label("Kontostand");

        BigDecimal balance = financeService.calculateBalance(account);

        Label balanceValue = new Label(
                balance + " " + account.getCurrency().getCurrencyCode()
        );

        Label transactionTitle = new Label("Transaktionen");
        transactionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox transactionBox = new VBox(5);

        for (Transaction transaction : account.getTransactions()) {
            Label transactionLabel = new Label(
                    transaction.getDate()
                    + " | "
                    + transaction.getDescription()
                    + " | "
                    + transaction.getSignedAmount()
                    + " | "
                    + account.getCurrency().getCurrencyCode()
            );

            transactionBox.getChildren().add(transactionLabel);
        }

        VBox balanceBox = new VBox(10);
        balanceBox.getChildren().addAll(
                balanceTitle,
                balanceValue,
                transactionTitle,
                transactionBox
        );

        balanceBox.setPadding(new Insets(20));

        root.setCenter(balanceBox);

        Scene scene = new Scene(root, 800, 500);

        stage.setTitle("Finance Manager");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
