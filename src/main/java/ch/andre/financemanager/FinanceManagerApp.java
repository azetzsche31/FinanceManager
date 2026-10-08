package ch.andre.financemanager;

import ch.andre.financemanager.model.Account;
import ch.andre.financemanager.model.Category;
import ch.andre.financemanager.model.Transaction;
import ch.andre.financemanager.model.TransactionType;
import ch.andre.financemanager.persistence.AccountLoader;
import ch.andre.financemanager.persistence.AccountRepository;
import ch.andre.financemanager.persistence.DatabaseManager;
import ch.andre.financemanager.persistence.TransactionRepository;
import ch.andre.financemanager.service.FinanceService;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;


public class FinanceManagerApp extends Application {

    @Override
    public void start(Stage stage) throws SQLException {

        FinanceService financeService = new FinanceService();

        DatabaseManager databaseManager =
                new DatabaseManager("finance-manager.db");

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

        // Hauptlayout
        BorderPane root = new BorderPane();

        // Header
        Label titleLabel = new Label("Finanace Manager");

        titleLabel.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold;"
        );

        HBox header = new HBox(titleLabel);
        header.setPadding(new Insets(20));

        root.setTop(header);


        // Kontostand
        Label balanceTitle = new Label("Kontostand");

        BigDecimal balanace =
                financeService.calculateBalance(account);

        Label balanceValue = new Label(
                balanace
                            + " "
                            + account.getCurrency().getCurrencyCode()
        );

        // Transaktionen
        Label transactionTitle =
                new Label("Transaktionen");

        transactionTitle.setStyle(
                "-fx-font-size: 18px; -fx-font-weigth: bold;"
        );

        // Button zum Erfassen einer Transaktion
        Button addTransactionButton =
                new Button("Transaktion hinzufügen");

        addTransactionButton.setOnAction(event -> {

            Optional<Transaction> result =
                    showTransactionDialog(account);

            result.ifPresent(transaction ->
                    System.out.println(
                            "Neue Transaktion: "
                            + transaction.getDescription()
                            + " | "
                            + transaction.getSignedAmount()
                    )
            );
        });

        // Vorhandene Transaktionen anzeigen
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

            transactionBox
                    .getChildren()
                    .add(transactionLabel);
        }

        // Dashboard-Inhalt
        VBox balanceBox = new VBox(10);

        balanceBox.getChildren().addAll(
                balanceTitle,
                balanceValue,
                addTransactionButton,
                transactionTitle,
                transactionBox
        );

        balanceBox.setPadding(new Insets(20));

        root.setCenter(balanceBox);


        // Fenster
        Scene scene =
                new Scene(root, 800, 500);

        stage.setTitle("Finanace Manager");
        stage.setScene(scene);
        stage.show();
    }

    private Optional<Transaction> showTransactionDialog(
            Account account
    ) {

        Dialog<Transaction> dialog =
                new Dialog<>();

        dialog.setTitle("Neue Transaktion");
        dialog.setHeaderText("Transaktion erfassen");


        // Button
        ButtonType saveButtonType =
                new ButtonType(
                        "Speichern",
                        ButtonBar.ButtonData.OK_DONE
                );

        ButtonType cancelButtonType =
                new ButtonType(
                        "Abbrechen",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        saveButtonType,
                        cancelButtonType
                );


        // Datum
        DatePicker datePicker =
                new DatePicker();


        // Betrag
        TextField amountField =
                new TextField();

        amountField.setPromptText(
                "z.B. 52.30"
        );

        // Beschreibung
        TextField descriptionField =
                new TextField();

        descriptionField.setPromptText(
                "z.B. Lebensmitteleinkauf"
        );

        // Transaktionstyp
        ComboBox<TransactionType> typeComboBox =
                new ComboBox<>();

        typeComboBox.getItems().addAll(
                TransactionType.INCOME,
                TransactionType.EXPENSE
        );

        typeComboBox.setPromptText(
                "Type auswählen"
        );;

        typeComboBox.setConverter(
                new StringConverter<TransactionType>() {

                    @Override
                    public String toString(
                            TransactionType type
                    ) {

                        if (type == null) {
                            return "";
                        }

                        return switch (type) {
                            case INCOME -> "Einnahme";
                            case EXPENSE -> "Ausgabe";
                        };
                    }

                    @Override
                    public TransactionType fromString(
                            String string
                    ) {
                        return null;
                    }
                }
        );

        // Kategorie
        ComboBox<Category> categoryComboBox =
                new ComboBox<>();

        categoryComboBox.getItems().addAll(
                Category.values()
        );

        categoryComboBox.setPromptText(
                "Kategorie auswählen"
        );

        categoryComboBox.setConverter(
                new StringConverter<Category>() {

                    @Override
                    public String toString(
                            Category category
                    ) {

                        if(category == null) {
                            return "";
                        }

                        return switch (category) {
                            case SALARY -> "Lohn";
                            case GROCERIES -> "Lebensmittel";
                            case HOUSING -> "Wohnen";
                            case TRANSPORT -> "Transport";
                            case INSURANCE -> "Versicherung";
                            case HEALTH -> "Gesundheit";
                            case LEISURE -> "Freizeit";
                            case SUBSCRIPTIONS -> "Abonnements";
                            case TAXES -> "Steuern";
                            case SAVINGS -> "Sparen";
                            case OTHER -> "Sonstige";
                        };
                    }

                    @Override
                    public Category fromString(
                            String string
                    ) {
                        return null;
                    }
                }
        );

        // Formular
        GridPane form   =
                new GridPane();

        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        form.add(
                new Label("Datum:"),
                        0,
                        0
                );

        form.add(
                datePicker,
                1,
                0
        );

        form.add(
                new Label("Betrag"),
                0,
                1
        );

        form.add(
                amountField,
                1,
                1
        );

        form.add(
                new Label("Beschreibung"),
                0,
                2
        );

        form.add(
                descriptionField,
                1,
                2
        );

        form.add(
                new Label("Typ"),
                0,
                3
        );

        form.add(
                typeComboBox,
                1,
                3
        );

        form.add(
                new Label("Kategorie"),
                0,
                4
        );

        form.add(
                categoryComboBox,
                1,
                4
        );

        dialog.getDialogPane()
                .setContent(form);


        // Transaction erzeugen
        dialog.setResultConverter(buttonType -> {

            if (buttonType == saveButtonType) {

                BigDecimal amount =
                        new BigDecimal(
                                amountField.getText()
                        );

                return new Transaction(
                        datePicker.getValue(),
                        amount,
                        descriptionField.getText(),
                        typeComboBox.getValue(),
                        categoryComboBox.getValue(),
                        account
                );
            }

            return null;
        });

        // Speichern-Button für Validierung holen
        Button saveButton =
                (Button) dialog
                        .getDialogPane()
                        .lookupButton(saveButtonType);


        // Eingaben validieren
        saveButton.addEventFilter(
                ActionEvent.ACTION,
                saveEvent -> {

                    // Datum
                    if (datePicker.getValue() == null)  {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle(
                                "Ungültige Eingabe"
                        );

                        alert.setHeaderText(
                                "Datum fehlt"
                        );

                        alert.setContentText(
                                "Bitte wählen Sie ein Datum aus."
                        );

                        alert.showAndWait();

                        saveEvent.consume();
                        return;
                    }

                    // Beschreibung
                    if (descriptionField
                            .getText()
                            .isBlank()) {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle(
                                "Ungültige Eingabe"
                        );

                        alert.setHeaderText(
                                "Beschreibung fehlt"
                        );

                        alert.setContentText(
                                "Bitte geben Sie eine Beschreibung ein."
                        );

                        alert.showAndWait();

                        saveEvent.consume();
                        return;
                    }

                    // Typ
                    if (typeComboBox.getValue() == null) {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle(
                                "Ungültige Eingabe"
                        );

                        alert.setHeaderText(
                                "Transaktiontyp fehlt"
                        );

                        alert.setContentText(
                                "Bitte wählen Sie Einnahme oder Ausgabe aus."
                        );

                        alert.showAndWait();

                        saveEvent.consume();
                        return;
                    }

                    // Kategorie
                    if (categoryComboBox.getValue() == null) {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle(
                                "Ungültige Eingabe"
                        );

                        alert.setHeaderText(
                                "Kategorie fehlt"
                        );

                        alert.setContentText(
                                "Bitte wählen Sie eine Kategorie aus."
                        );

                        alert.showAndWait();

                        saveEvent.consume();
                        return;
                    }

                    // Betrag
                    BigDecimal amount;

                    try {

                        amount =
                                new BigDecimal(
                                        amountField.getText()
                                );
                    } catch (NumberFormatException exception) {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle(
                                "Ungültige Eingabe"
                        );

                        alert.setHeaderText(
                                "Ungültige Betrag"
                        );

                        alert.setContentText(
                                "Bitte geben Sie einen gültigen Betrag ein."
                        );

                        alert.showAndWait();

                        saveEvent.consume();
                        return;
                    }

                    if (amount.signum() <= 0) {

                        Alert alert =
                                new Alert(
                                        Alert.AlertType.ERROR
                                );

                        alert.setTitle(
                                "Ungültige Eingabe"
                        );

                        alert.setHeaderText(
                                "Ungütiger Betrag"
                        );

                        alert.setContentText(
                                "Der Betrag muss grösser als 0 sein."
                        );

                        alert.showAndWait();

                        saveEvent.consume();
                    }

                }
        );

        return dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}