# Finance Manager

🚀 **Aktuelle Version: 1.0**

Eine Java-Konsolenanwendung zur Verwaltung persönlicher Finanzen.

Das Projekt dient dazu, moderne Java-Entwicklung mit objektorientiertem Design, Test-Driven Development (TDD), JUnit-Tests und Maven praktisch zu erlernen.

## Technologien

- Java 25 LTS
- Maven
- JUnit 5
- Git
- IntelliJ IDEA Community

### Geplante Technologien

- JavaFX
- SQLite

## Projektstatus

🚀 Version 1.0 abgeschlossen – Weiterentwicklung zu Version 1.1 geplant.

## Bereits implementierte Funktionen

- Konten verwalten
- Einnahmen und Ausgaben erfassen
- Kontostand berechnen
- Transaktionen anzeigen
- Monatsberichte erstellen
- Jahresberichte erstellen
- CSV-Import mit Validierung und Fehlerprotokoll
- CSV-Export
- Konsolenmenü
- Roundtrip-Test (CSV-Export → CSV-Import)

## Geplante Funktionen

- SQLite-Datenbank
- JavaFX-Benutzeroberfläche
- Budgetplanung
- Sparziele
- Diagramme
- Such- und Filterfunktionen

## Versionshistorie

### Version 0.1
- Maven-Projekt eingerichtet
- Git-Repository erstellt
- Projektstruktur aufgebaut

### Version 0.2
- Domänenmodell erstellt
- Account und Transaction implementiert
- Enums eingeführt

### Version 0.3
- FinanceService implementiert
- Kontostand, Einnahmen und Ausgaben berechnen
- Erster JUnit-Test

### Version 0.4
- CSV-Import mit Validierung
- Fehlerprotokoll
- Refactoring

### Version 0.5
- CSV-Export
- Roundtrip-Test
- Unterstützung für Sonderzeichen in CSV

### Version 1.0
- Konsolenmenü
- Transaktionen erfassen
- Kontostand anzeigen
- Monats- und Jahresberichte
- CSV-Import und CSV-Export über das Menü

## Qualität

- Unit-Tests mit JUnit 5
- Integrationstest für CSV-Import und CSV-Export (Roundtrip)

## Projektstruktur

```text
src/
├── main/
│   ├── java/
│   │   └── ch/andre/financemanager/
│   │       ├── Main.java
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
│   │       └── service/
│   │           ├── FinanceService.java
│   │           ├── CsvImportService.java
│   │           └── CsvExportService.java
│   └── resources/
│
└── test/
    ├── java/
    │   └── ch/andre/financemanager/service/
    │       ├── FinanceServiceTest.java
    │       ├── CsvImportServiceTest.java
    │       ├── CsvExportServiceTest.java
    │       └── CsvImportExportTest.java
    └── resources/
        └── csv/

## Autor

Andreas