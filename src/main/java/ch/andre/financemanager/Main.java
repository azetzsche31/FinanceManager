package ch.andre.financemanager;


import ch.andre.financemanager.model.*;
import ch.andre.financemanager.service.CsvExportService;
import ch.andre.financemanager.service.CsvImportService;
import ch.andre.financemanager.service.FinanceService;

import javax.lang.model.element.NestingKind;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;
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

                case "4":
                    csvImport();
                    break;

                case "5":
                    csvExport();
                    break;

                case "6":
                    createMonthlyReport();
                    break;

                case "7":
                    createYearlyReport();
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
        System.out.print("Auswahl: ");
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

        System.out.print("Datum (YYYY-MM-DD: ");
        String dateInput = scanner.nextLine();

        System.out.print("Betrag: ");
        String amountInput = scanner.nextLine();

        System.out.print("Beschreibung: ");
        String description = scanner.nextLine();

        TransactionType type = readTransactionType();

        Category category = readCategory();

        LocalDate date = LocalDate.parse(dateInput);

        BigDecimal amount = new BigDecimal(amountInput);



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

    private TransactionType readTransactionType() {

        while (true) {
            System.out.println();
            System.out.println("Verfügbare Typen:");

            for (TransactionType type : TransactionType.values()) {
                System.out.println("- " + type);
            }

            System.out.print("Typ: ");
            String input = scanner.nextLine();

            try {
                return TransactionType.valueOf(
                        input.trim().toUpperCase()
                );
            } catch (IllegalArgumentException exception) {
                System.out.println();
                System.out.println(
                        "Ungültiger Transaktionstyp. Bitte erneut eingeben."
                );
            }
        }
    }

    private Category readCategory() {

        while (true) {
            System.out.println();
            System.out.println("Verfügbare Kategorien:");

            for (Category category : Category.values()) {
                System.out.println("- "+ category);
            }

            System.out.print("Kategorie: ");
            String input = scanner.nextLine();

            try {
                return Category.valueOf(
                        input.trim().toUpperCase()
                );
            }   catch (IllegalArgumentException exception) {
                System.out.println();
                System.out.println(
                        "Ungültige Kategorie. Bitte erneut eingeben."
                );
            }
        }
    }

    private void csvImport() {

        System.out.println();
        System.out.println("===== CSV Import =====");
        System.out.println();
        System.out.print("Pfad zur CSV-Datei: ");
        String path = scanner.nextLine();

        Path file = Path.of(path);

        CsvImportResult result =
                csvImportService.importTransactions(
                        file,
                        account
                );

        account.addTransactions(
                result.getTransactions()
        );

        System.out.println();
        System.out.println("CSV-Import abgeschlossen.");
        System.out.println();

        System.out.println(
                "Importierte Transaktionen: "
                    + result.getImportedCount()
        );

        System.out.println(
                "Fehler: "
                    + result.getErrorCount()
        );
    }

    private void csvExport() {

        System.out.println();
        System.out.println("===== CSV Export =====");
        System.out.println();

        System.out.print("Pfad für die CSV-Datei: ");
        String pathInput = scanner.nextLine();

        Path file = Path.of(pathInput);

        try {
            csvExportService.exportTransactions(
                    file,
                    account
            );

            System.out.println();
            System.out.printf("CSV-Export erfolgreich abgeschlossen.");
            System.out.println(
                    "Exportierte Transaktionen: "
                        + account.getTransactions().size()
            );
            System.out.println("Datei: " + file.toAbsolutePath());
        } catch (IOException exception) {
            System.out.println();
            System.out.println(
                    "Die CSV-Datei konnte nicht exportiert werden."
            );
            System.out.println(
                    "Grund: " + exception.getMessage()
            );
        }
    }

    private void createMonthlyReport() {


        System.out.println();
        System.out.println("===== Monatsbericht ======");
        System.out.print("Monat: (1-12): ");
        int monthNumber = Integer.parseInt(scanner.nextLine());
        System.out.print("Jahr: ");
        int year = Integer.parseInt(scanner.nextLine());

        Month month = Month.of(monthNumber);


        MonthlyReport report =
                financeService.createMonthlyReport(
                        account,
                        month,
                        year
                );
        System.out.println(report);

    }

    private void createYearlyReport() {

        System.out.println();
        System.out.println("===== Jahresbericht =====");
        System.out.print("Jahr: ");
        int year = Integer.parseInt(scanner.nextLine());

        YearlyReport report =
                financeService.createYearlyReport(
                        account,
                        year
                );

        System.out.println(report);
    }


}
