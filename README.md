# Finance Manager

🚀 **Aktuelle Version: 1.1 – in Entwicklung**

Eine Java-Konsolenanwendung zur Verwaltung persönlicher Finanzen.

Das Projekt dient dazu, moderne Java-Entwicklung mit objektorientiertem Design, Test-Driven Development (TDD), JUnit-Tests, Maven und Datenbank-Persistenz praktisch zu erlernen.

## Technologien

* Java 25 LTS
* Maven
* JUnit 5
* SQLite
* SQLite JDBC
* Git
* GitHub
* IntelliJ IDEA Community

### Geplante Technologien

* JavaFX

## Projektstatus

🚀 **Version 1.0 abgeschlossen**

🔧 **Version 1.1 in Entwicklung**

Die Anwendung verfügt inzwischen über eine SQLite-basierte Persistenz für Konten und Transaktionen.

Die Konsolenanwendung verwendet beim Start einen persistenten Default-Account und kann Transaktionen dauerhaft in der Datenbank speichern und wieder laden.

Zusätzlich stehen CSV-Import und CSV-Export sowie Monats- und Jahresberichte zur Verfügung.

Die Entwicklung erfolgt schrittweise nach dem **Test-Driven-Development-Prinzip**.

## Bereits implementierte Funktionen

### Finanzverwaltung

* Konten verwalten
* Einnahmen und Ausgaben erfassen
* Kontostand berechnen
* Transaktionen anzeigen
* Einnahmen berechnen
* Ausgaben berechnen
* Auswertungen nach Kategorie
* Monatsberichte erstellen
* Jahresberichte erstellen
* Nettoergebnis berechnen

### CSV

* CSV-Import
* Validierung der importierten Daten
* Fehlerprotokoll beim Import
* Persistenter CSV-Import in die SQLite-Datenbank
* CSV-Export
* Roundtrip-Test (CSV-Export → CSV-Import)
* Unterstützung für Semikolon in CSV-Werten
* Unterstützung für Anführungszeichen in CSV-Werten

### Persistenz

* SQLite-Datenbank integriert
* Datenbankschema für Konten
* Datenbankschema für Transaktionen
* `AccountRepository` für die Konten-Persistenz
* `TransactionRepository` für die Transaktions-Persistenz
* `AccountLoader` zum Laden bzw. Erzeugen des Default-Accounts
* Transaktionen können über die Anwendung in SQLite gespeichert werden
* Gespeicherte Transaktionen können wieder aus SQLite geladen werden
* Importierte CSV-Transaktionen werden persistent gespeichert
* Persistenz durch Integrationstests abgesichert

### Berichte und Auswertungen

* Monatsberichte
* Jahresberichte
* Einnahmen nach Zeitraum
* Ausgaben nach Zeitraum
* Nettoergebnis nach Zeitraum
* Ausgaben nach Kategorie
* Berichte für Zeiträume ohne Transaktionen

### Anwendung

* Konsolenmenü
* Transaktionen über die Anwendung erfassen
* Kontostand nach dem Erfassen einer Transaktion anzeigen
* Persistente Daten beim Programmstart laden
* CSV-Import über das Menü
* CSV-Export über das Menü

## Geplante Funktionen

* JavaFX-Benutzeroberfläche
* Dashboard
* Finanzübersichten visualisieren
* Diagramme
* Such- und Filterfunktionen
* Budgetplanung
* Sparziele
* Erweiterte Auswertungen
* Mehrere Konten komfortabel verwalten

## Versionshistorie

### Version 0.1

* Maven-Projekt eingerichtet
* Git-Repository erstellt
* Projektstruktur aufgebaut

### Version 0.2

* Domänenmodell erstellt
* `Account` und `Transaction` implementiert
* Enums eingeführt

### Version 0.3

* `FinanceService` implementiert
* Kontostand, Einnahmen und Ausgaben berechnen
* Erster JUnit-Test

### Version 0.4

* CSV-Import mit Validierung
* Fehlerprotokoll
* Refactoring

### Version 0.5

* CSV-Export
* Roundtrip-Test
* Unterstützung für Sonderzeichen in CSV

### Version 1.0

* Konsolenmenü
* Transaktionen erfassen
* Kontostand anzeigen
* Monats- und Jahresberichte
* CSV-Import und CSV-Export über das Menü

### Version 1.1 – in Entwicklung

* SQLite integriert
* Datenbankschema für Konten und Transaktionen
* `AccountRepository` implementiert
* `TransactionRepository` implementiert
* `AccountLoader` implementiert
* Persistenter Default-Account
* Transaktionen über `Main` in SQLite speichern
* Transaktionen aus SQLite laden
* CSV-Import persistent in SQLite speichern
* Monats- und Jahresberichte erweitert
* Nettoergebnis berechnen
* Ausgaben nach Kategorie auswerten
* Persistenztests mit JUnit
* Integrationstests für CSV und Persistenz
* TDD-basierte Weiterentwicklung

## Qualität

Die Entwicklung erfolgt schrittweise nach dem **Test-Driven-Development-Prinzip**.

Aktuell eingesetzt:

* Unit-Tests mit JUnit 5
* Tests für `FinanceService`
* Tests für Monats- und Jahresberichte
* Tests für Auswertungen nach Kategorie
* Tests für CSV-Import
* Tests für CSV-Export
* Tests für CSV-Sonderzeichen
* Roundtrip-Tests für CSV-Export und CSV-Import
* Persistenztests für SQLite
* Tests für `AccountRepository`
* Tests für `TransactionRepository`
* Tests für `AccountLoader`
* Integrationstests für die Kombination aus Anwendung und Persistenz

Ziel ist es, neue Funktionen zunächst durch Tests abzusichern und anschließend die Implementierung weiterzuentwickeln.

## Projektstruktur

```text
src/
├── main/
│   ├── java/
│   │   └── ch/andre/financemanager/
│   │       ├── Main.java
│   │       │
│   │       ├── model/
│   │       │   ├── Account.java
│   │       │   ├── Transaction.java
│   │       │   ├── MonthlyReport.java
│   │       │   ├── YearlyReport.java
│   │       │   ├── CsvImportResult.java
│   │       │   ├── CsvImportError.java
│   │       │   ├── AccountType.java
│   │       │   ├── TransactionType.java
│   │       │   └── Category.java
│   │       │
│   │       ├── persistence/
│   │       │   ├── DatabaseManager.java
│   │       │   ├── AccountRepository.java
│   │       │   ├── TransactionRepository.java
│   │       │   └── AccountLoader.java
│   │       │
│   │       └── service/
│   │           ├── FinanceService.java
│   │           ├── CsvImportService.java
│   │           └── CsvExportService.java
│   │
│   └── resources/
│
└── test/
    ├── java/
    │   └── ch/andre/financemanager/
    │       ├── MainTest.java
    │       │
    │       ├── persistence/
    │       │   ├── AccountRepositoryTest.java
    │       │   ├── TransactionRepositoryTest.java
    │       │   └── AccountLoaderTest.java
    │       │
    │       └── service/
    │           ├── FinanceServiceTest.java
    │           ├── CsvImportServiceTest.java
    │           ├── CsvExportServiceTest.java
    │           └── CsvImportExportTest.java
    │
    └── resources/
        └── csv/
            └── transaction-import.csv
```
## Architektur
```text
Main
 │
 ├── Service Layer
 │   ├── FinanceService
 │   ├── CsvImportService
 │   └── CsvExportService
 │
 ├── Persistence Layer
 │   ├── AccountLoader
 │   ├── AccountRepository
 │   ├── TransactionRepository
 │   └── DatabaseManager
 │
 └── Domain Model
     ├── Account
     ├── Transaction
     ├── MonthlyReport
     ├── YearlyReport
     ├── Category
     └── weitere Modelle
```

## Datenbank

Die Anwendung verwendet ***SQLite*** zur persistenten Speicherung der Finanzdaten.

Aktuell werden folgende Daten gespeichert:

### **Accounts**

- ID
- Name
- Kontotyp
- Eröffnungssaldo
- Währung

### **Transactions**

- ID
- Konto
- Datum
- Betrag
- Beschreibung
- Transaktionstyp
- Kategorie

Die Datenbank wird beim Start der Anwendung geladen bzw. der Default-Account wird bei Bedarf automatisch angelegt.

## CSV-Format

Der CSV-Import und -Export verwendet Semikolon als Trennzeichen.

Beispiel

```text

date;amount;description;type;category
2026-07-01;5000.00;Lohn;INCOME;SALARY
2026-07-05;2500;Miete;EXPENSE;HOUSING

```

CSV-Werte mit Semikolon oder Anführungszeichen werden entsprechend den CSV-Regeln maskiert.

Beispiel

```text

2026-07-10;45.50;"Restaurant; Abendessen";EXPENSE;GROCERIES
2026-07-10;45.50;"Restaurant ""Abendessen""";EXPENSE;GROCERIES
```

## Test-DRiven Development

Das Projekt wird schrittweise nach dem **TDD-Prinzip** entwickelt:

```text
Test schreiben
      ↓
Test schlägt fehl
      ↓
Implementierung
      ↓
Test wird grün
      ↓
Refactoring
      ↓
Nächste Funktion

```
Dabei werden sowohl einzelne Services und Modelle als auch die Zusammenarbeit
zwischen Anwendung, CSV-Verarbeitung und SQLite getestet.

## Nächste Entwicklungsschritte
1. Architektur für die Benutzeroberfläche vorbereiten
2. JavaFX in das Maven-Projekt integrieren
3. JavaFX-Anwendung als separaten UI-Einstiegspunkt aufbauen
4. Hauptfenster und Navigation erstellen 
5. Konten- und Transaktionsübersicht als UI umsetzen 
6. Dashboard und Finanzübersicht visualisieren
7. Diagramme und weitere Visualisierungen hinzufügen

## Autor
Andreas

