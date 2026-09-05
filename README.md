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

Die Anwendung verfügt inzwischen über eine SQLite-basierte Persistenz für Konten und Transaktionen. Die Konsolenanwendung verwendet beim Start einen persistenten Default-Account und kann Transaktionen dauerhaft in der Datenbank speichern und wieder laden.

Die Entwicklung erfolgt schrittweise nach dem **Test-Driven-Development-Prinzip**.

## Bereits implementierte Funktionen

### Finanzverwaltung

* Konten verwalten
* Einnahmen und Ausgaben erfassen
* Kontostand berechnen
* Transaktionen anzeigen
* Monatsberichte erstellen
* Jahresberichte erstellen
* Auswertungen nach Kategorie

### CSV

* CSV-Import
* Validierung der importierten Daten
* Fehlerprotokoll beim Import
* CSV-Export
* Roundtrip-Test (CSV-Export → CSV-Import)
* Unterstützung für Sonderzeichen in CSV-Dateien

### Persistenz

* SQLite-Datenbank integriert
* Datenbankschema für Konten
* Datenbankschema für Transaktionen
* `AccountRepository` für die Konten-Persistenz
* `TransactionRepository` für die Transaktions-Persistenz
* `AccountLoader` zum Laden bzw. Erzeugen des Default-Accounts
* Transaktionen können über die Anwendung in SQLite gespeichert werden
* Gespeicherte Transaktionen können wieder aus SQLite geladen werden
* Persistenz durch Integrationstests abgesichert

### Anwendung

* Konsolenmenü
* Transaktionen über die Anwendung erfassen
* Kontostand nach dem Erfassen einer Transaktion anzeigen
* Persistente Daten beim Programmstart laden

## Geplante Funktionen

* Persistenter CSV-Import
* JavaFX-Benutzeroberfläche
* Budgetplanung
* Sparziele
* Diagramme
* Such- und Filterfunktionen
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
* Persistenztests mit JUnit
* TDD-basierte Weiterentwicklung

## Qualität

Die Entwicklung erfolgt schrittweise nach dem **Test-Driven-Development-Prinzip**.

Aktuell eingesetzt:

* Unit-Tests mit JUnit 5
* Integrationstests für CSV-Import und CSV-Export
* Persistenztests für SQLite
* Tests für `FinanceService`
* Tests für `AccountRepository`
* Tests für `TransactionRepository`
* Tests für `AccountLoader`
* Tests für die Integration von `Main` und der Persistenz

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
```

## Architektur

Die Anwendung ist in mehrere Bereiche aufgeteilt:

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
     ├── Category
     └── weitere Modelle
```

Die Trennung der Verantwortlichkeiten soll die Anwendung testbar und langfristig erweiterbar halten.

## Nächste Entwicklungsschritte

1. CSV-Import ebenfalls persistent speichern
2. Persistenz weiter durch Tests absichern
3. Architektur für die Benutzeroberfläche vorbereiten
4. JavaFX-Benutzeroberfläche entwickeln
5. Dashboard und Finanzübersichten visualisieren

## Autor

Andreas