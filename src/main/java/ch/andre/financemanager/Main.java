package ch.andre.financemanager;


import ch.andre.financemanager.model.*;
import ch.andre.financemanager.service.CsvExportService;
import ch.andre.financemanager.service.CsvImportService;
import ch.andre.financemanager.service.FinanceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private final Scanner scanner = new Scanner(System.in);

    private final FinanceService financeService =
            new FinanceService();

    private final CsvImportService csvImportService =
            new CsvImportService();

    private final CsvExportService csvExportService =
            new CsvExportService();

    private final Account account = new Account(
            "Privatkonto",
            AccountType.CHECKING,
            BigDecimal.ZERO,
            Currency.getInstance("CHF")
    );

    public static void main(String[] args) {
      Main application = new Main();
      application.start();
    }

    private void start()  {
        boolean running = true;

        while (running) {
            printMenu();

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    showBalance();
                    break;

                case "2":
                    showTransactions();
                    break;

                case "3":
                    addTransaction();
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Ungültige Auswahl. Bitte erneut versuchen."
                    );
            }
        }

        System.out.println("Finance Manager wurde beendet.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=====================================");
        System.out.println("               Finance Manager");
        System.out.println("=====================================");
        System.out.println("1. Kontostand anzeigen");
        System.out.println("2. Transaktion anzeigen");
        System.out.println("3. Transaktion erfassen");
        System.out.println("4. CSV importieren");
        System.out.println("5. CSV exportieren");
        System.out.println("6. Monatsbericht anzeigen");
        System.out.println("7. Jahresbericht anzeigen");
        System.out.println();
        System.out.println("0. Programm beenden");
        System.out.println("Auswahl: ");
    }

    private void    showBalance() {
        BigDecimal balance =
                financeService.calculateBalance(account);

        System.out.println();
        System.out.println("Kontostand: "
                + balance
                + " "
                + account.getCurrency().getCurrencyCode());
    }

    private void showTransactions() {

        System.out.println();
        System.out.println("===== Transaktion =====");

        if (account.getTransactions().isEmpty()) {
            System.out.println("Keine Transaktionen vorhanden.");
            return;
        }

        for (Transaction transaction : account.getTransactions()) {
            System.out.println(transaction);
        }
    }


    private void addTransaction() {

        System.out.println();
        System.out.println("===== Neue Transaktion =====");

        System.out.println("Datum (YYYY-MM-DD: ");
        String dateInput = scanner.nextLine();

        System.out.println("Betrag: ");
        String amountInput = scanner.nextLine();

        System.out.println("Beschreibung: ");
        String description = scanner.nextLine();

        System.out.println();
        System.out.println("Verfügbare Typen: ");

        for (TransactionType type : TransactionType.values()) {
            System.out.println("- "+ type);
        }

        System.out.println("Typ: ");
        String typeInput = scanner.nextLine();

        System.out.println();
        System.out.println("Verfügbare Kategorien");

        for (Category category : Category.values()) {
            System.out.println("- "+ category);
        }

        System.out.println("Kategorie: ");
        String categoryInput = scanner.nextLine();

        LocalDate date = LocalDate.parse(dateInput);

        BigDecimal amount = new BigDecimal(amountInput);

        TransactionType type =
                TransactionType.valueOf(typeInput.toUpperCase());

        Category category =
                Category.valueOf(categoryInput.toUpperCase());


        Transaction transaction = new Transaction(
                date,
                amount,
                description,
                type,
                category,
                account
        );

        account.addTransaction(transaction);

        System.out.println("Transaktion erfolgreich hinzugefügt.");

        BigDecimal balacne =
                financeService.calculateBalance(account);

        System.out.println(
                "Neuer Kontostand: "
                    + balacne
                    + " "
                    +account.getCurrency().getCurrencyCode()
        );

        System.out.println("-----------------------------------------");

    }
}
