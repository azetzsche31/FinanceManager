package ch.andre.financemanager;

import ch.andre.financemanager.model.*;
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
import java.util.Currency;
import java.util.List;
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


        List<Account> accounts =
                accountRepository.findAll();

        for (Account loadedAccount : accounts) {

            List<Transaction> transactions =
                    transactionRepository.findByAccount(
                            loadedAccount
                    );

            loadedAccount.addTransactions(
                    transactions
            );
        }

        ComboBox<Account> accountComboBox =
                new ComboBox<>();

        accountComboBox.getItems().addAll(accounts);

        if (!accounts.isEmpty()) {
            accountComboBox.setValue(
                    accounts.getFirst()
            );
        }

        accountComboBox.setCellFactory(accountListView ->
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Account item,
                            boolean empty
                    ) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                }
        );

        accountComboBox.setButtonCell(
                new ListCell<>() {

                    @Override
                    protected void updateItem(
                            Account item,
                            boolean empty
                    ) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                }
        );

        HBox accountSelectionBox =
                new HBox(10);

        accountSelectionBox.getChildren().addAll(
                new Label("Konto: "),
                accountComboBox
        );

        // Hauptlayout
        BorderPane root = new BorderPane();

        // Header
        Label titleLabel = new Label("Finance Manager");

        titleLabel.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold;"
        );

        HBox header = new HBox(titleLabel);
        header.setPadding(new Insets(20));

        root.setTop(header);


        // Kontostand
        Label balanceTitle = new Label("Kontostand");

        Account initialAccount =
                accountComboBox.getValue();

        Label balanceValue =
                new Label("Noch kein Konto vorhanden");

        if (initialAccount != null) {

            BigDecimal balance =
                    financeService.calculateBalance(
                            initialAccount
                    );

            balanceValue.setText(
                    balance
                            + " "
                            + initialAccount
                            .getCurrency()
                            .getCurrencyCode()
            );
        }


        // Transaktionen
        Label transactionTitle =
                new Label("Transaktionen");

        transactionTitle.setStyle(
                "-fx-font-size: 18px; -fx-font-weigth: bold;"
        );

        // Button zum Erfassen eines Kontos

        Button addAccountButton =
                new Button("Neues Konto");

        addAccountButton.setOnAction(event -> {

            Optional<Account> result =
                    showAccountDialog();

            result.ifPresent(newAccount -> {
                try {
                    accountRepository.save(newAccount);

                    accountComboBox
                            .getItems()
                            .add(newAccount);

                    accountComboBox.setValue(newAccount);

                    System.out.println(
                            "Konto gespeichert: "
                            + newAccount.getName()
                    );
                } catch (SQLException exception) {

                    Alert alert =
                            new Alert(Alert.AlertType.ERROR);

                    alert.setTitle("Fehler");
                    alert.setHeaderText(
                            "Konto konnte nicht gespeichert werden."
                    );
                    alert.setContentText(
                            exception.getMessage()
                    );

                    alert.showAndWait();
                }
            });
        });

        VBox transactionBox = new VBox(5);

        accountComboBox.setOnAction(event -> {

            Account selectedAccount =
                    accountComboBox.getValue();

            if (selectedAccount == null) {
                return;
            }

            updateDashboard(
                    selectedAccount,
                    balanceValue,
                    transactionBox,
                    financeService,
                    transactionRepository
            );
        });

        // Button zum Erfassen einer Transaktion
        Button addTransactionButton =
                new Button("Transaktion hinzufügen");

        addTransactionButton.setOnAction(event -> {

            Account selectedAccount =
                    accountComboBox.getValue();

            if (selectedAccount == null) {
                return;
            }

                    Optional<Transaction> result =
                            showTransactionDialog(selectedAccount);

                    result.ifPresent(transaction -> {
                        try {
                            transactionRepository.save(transaction);
                            selectedAccount.addTransaction(transaction);

                           HBox transactionRow =
                                   createTransactionRow(
                                           transaction,
                                           selectedAccount,
                                           transactionRepository,
                                           balanceValue,
                                           transactionBox,
                                           financeService
                                   );

                           transactionBox.getChildren().add(
                                   transactionRow
                           );

                            BigDecimal newBalance =
                                    financeService.calculateBalance(selectedAccount);

                            balanceValue.setText(
                                    newBalance
                                    + " "
                                    + selectedAccount.getCurrency().getCurrencyCode()
                            );

                            System.out.println(
                                    "Transaktion gespeichert: "
                                            + transaction.getDescription()
                                            + " | "
                                            + transaction.getSignedAmount()
                            );
                        } catch (SQLException exception) {
                            Alert alert =
                                    new Alert(Alert.AlertType.ERROR);

                            alert.setTitle("Fehler");
                            alert.setHeaderText(
                                    "Transaktion konnte nicht gespeichert werden"
                            );
                            alert.setContentText(
                                    exception.getMessage()
                            );

                            alert.showAndWait();
                        }
                    });
                });

        // Vorhandene Transaktionen anzeigen

        if (initialAccount != null) {

            for (Transaction transaction
                    : initialAccount.getTransactions()) {

                HBox transactionRow =
                        createTransactionRow(
                                transaction,
                                initialAccount,
                                transactionRepository,
                                balanceValue,
                                transactionBox,
                                financeService
                        );

                transactionBox.getChildren().add(transactionRow);
            }
        }


        // Dashboard-Inhalt
        VBox balanceBox = new VBox(10);

        balanceBox.getChildren().addAll(
                accountSelectionBox,
                balanceTitle,
                balanceValue,
                addAccountButton,
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

    private Optional<Account> showAccountDialog() {

        Dialog<Account> dialog = new Dialog<>();

        dialog.setTitle("Neues Konto");
        dialog.setHeaderText("Konto erstellen");

        ButtonType closeButtonType =
                new ButtonType(
                        "Schliessen",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        ButtonType saveButtonType =
                new ButtonType(
                        "Speichern",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        saveButtonType,
                        closeButtonType
                );

        TextField nameField = new TextField();
        nameField.setPromptText("z.B. Sparkonto");

        ComboBox<AccountType> accountTypeComboBox =
                new ComboBox<>();

        accountTypeComboBox.getItems().addAll(
                AccountType.values()
        );

        accountTypeComboBox.setPromptText(
                "Kontotyp auswählen"
        );

        TextField openingBalanceField =
                new TextField();

        openingBalanceField.setPromptText(
                "z.B. 10000.00"
        );

        TextField currencyField =
                new TextField("CHF");

        GridPane form = new GridPane();

        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));



        form.add(new Label("Name: "), 0, 0);
        form.add(nameField, 1,0);

        form.add(new Label("Kontotyp: "), 0, 1);
        form.add(accountTypeComboBox, 1,1);

        form.add(new Label("Anfangssaldo"), 0,2);
        form.add(openingBalanceField, 1, 2);

        form.add(new Label("Währung: "), 0,3);
        form.add(currencyField, 1, 3);

        dialog.getDialogPane().setContent(form);

        Button saveButton =
                (Button) dialog.getDialogPane()
                                .lookupButton(saveButtonType);

        saveButton.addEventFilter(
                ActionEvent.ACTION,
                event -> {

                    String errorMessage = null;

                    if (nameField.getText() == null
                        || nameField.getText().isBlank()) {

                        errorMessage =
                                "Bitte einen Kontonamen eingeben.";
                    } else if (accountTypeComboBox.getValue() == null) {

                        errorMessage =
                                "Bitte einen Kontotyp auswählen.";
                    } else if (openingBalanceField.getText() == null
                            || openingBalanceField.getText().isBlank()) {

                        errorMessage =
                                "Bitte einen Anfangssaldo eingeben.";
                    } else {

                        try {
                            new BigDecimal(
                                    openingBalanceField
                                            .getText()
                                            .trim()
                            );
                        } catch (NumberFormatException exception) {
                            errorMessage =
                                    "Der Anfangssaldo muss eine gültige Zahl sein.";
                        }
                    }

                    if (errorMessage == null) {

                        if (currencyField.getText() == null
                                || currencyField.getText().isBlank()) {

                            errorMessage =
                                    "Bitte eine Währung eingeben.";
                        } else {

                            try {
                                Currency.getInstance(
                                        currencyField
                                                .getText()
                                                .trim()
                                                .toUpperCase()
                                );
                            } catch (IllegalArgumentException exception) {
                                errorMessage =
                                        "Bitte einen gültigen Währungscode eingeben z.B. CHF.";
                            }
                        }
                    }

                    if (errorMessage != null) {

                        Alert alert =
                                new Alert(Alert.AlertType.ERROR);

                        alert.setTitle("Ungültige Eingabe");
                        alert.setHeaderText(
                                "Konto kann nicht erstellt werden"
                        );
                        alert.setContentText(errorMessage);

                        alert.showAndWait();

                        event.consume();
                    }
                }
        );


        dialog.setResultConverter(buttonType -> {

            if (buttonType == saveButtonType) {

                return new Account(
                        nameField.getText(),
                        accountTypeComboBox.getValue(),
                        new BigDecimal(
                                openingBalanceField.getText()
                        ),
                        Currency.getInstance(
                                currencyField.getText()
                                        .trim()
                                        .toUpperCase()
                        )
                );
            }

            return null;
        });

        return dialog.showAndWait();
    }

    private HBox createTransactionRow(
            Transaction transaction,
            Account account,
            TransactionRepository transactionRepository,
            Label balanceValue,
            VBox transactionBox,
            FinanceService financeService

    ) {

        Label transactionLabel =
                new Label(
                        transaction.getDate()
                        + " | "
                        + transaction.getDescription()
                        + " | "
                        + transaction.getSignedAmount()
                        + " | "
                        + account.getCurrency().getCurrencyCode()
                );

        Button deleteButton =
                new Button("Löschen");

        deleteButton.setOnAction(event -> {

            Alert confirmation =
                    new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Möchtest du diese Transaktion wirklich löschen?",
                            ButtonType.YES,
                            ButtonType.NO
                    );

            confirmation.setTitle("Transaktion löschen");
            confirmation.setHeaderText(
                    transaction.getDescription()
            );

            Optional<ButtonType> result =
                    confirmation.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.YES) {
                return;
            }

            try {
                transactionRepository.delete(transaction);

                account.removeTransaction(transaction);

                updateDashboard(
                        account,
                        balanceValue,
                        transactionBox,
                        financeService,
                        transactionRepository
                );
            } catch (SQLException exception) {

                Alert alert =
                        new Alert(Alert.AlertType.ERROR);

                alert.setTitle("Fehler");

                alert.setHeaderText(
                        "Transaktion konnte nicht gelöscht werden"
                );
                alert.setContentText(
                        exception.getMessage()
                );

                alert.showAndWait();
            }
        });


        HBox transactionRow =
                new HBox(10);

        transactionRow.getChildren().addAll(
                transactionLabel,
                deleteButton
        );

        return transactionRow;
    }

    private void updateDashboard(
            Account account,
            Label balanceValue,
            VBox transactionBox,
            FinanceService financeService,
            TransactionRepository transactionRepository
    ) {
        BigDecimal balance =
                financeService.calculateBalance(account);

        balanceValue.setText(
                balance
                        + " "
                        + account.getCurrency().getCurrencyCode()
        );

        transactionBox.getChildren().clear();

        for (Transaction transaction
                : account.getTransactions()) {

            transactionBox.getChildren().add(
                    createTransactionRow(
                            transaction,
                            account,
                            transactionRepository,
                            balanceValue,
                            transactionBox,
                            financeService
                    )
            );
        }
    }

    public static void main(String[] args) {
        launch();
    }
}