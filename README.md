# Finance Manager

**Windows-Paketversion: 1.0.0**  
**Projektstatus: In aktiver Entwicklung**

Finance Manager ist eine Java-Anwendung zur Verwaltung persönlicher Finanzen mit einer grafischen Benutzeroberfläche auf Basis von JavaFX und einer lokalen SQLite-Datenbank.

Das Projekt dient dazu, moderne Java-Entwicklung, objektorientiertes Design, Test-Driven Development (TDD), Datenbank-Persistenz und die Bereitstellung einer Windows-Desktopanwendung praktisch zu erlernen.

## Technologien

- Java 25 LTS
- JavaFX 25
- Maven
- JUnit
- SQLite und SQLite JDBC
- jpackage
- WiX Toolset 6
- Git und GitHub
- IntelliJ IDEA Community

## Projektstatus

Die Anwendung verfügt über eine funktionsfähige JavaFX-Oberfläche und eine SQLite-basierte Persistenz.

Ein Windows-Installer wurde erfolgreich erstellt und lokal getestet.

**Bereits umgesetzt:**
- Grafische Benutzeroberfläche mit JavaFX
- Verwaltung von Konten
- Erfassung von Einnahmen und Ausgaben
- Anzeige von Konten und Transaktionen
- Berechnung von Kontoständen
- Persistente Speicherung in SQLite
- CSV-Import und CSV-Export in der bisherigen Projektentwicklung
- Monats- und Jahresauswertungen in der bisherigen Projektentwicklung
- Unit- und Integrationstests
- Portable Windows-Anwendung
- Windows-Installer mit Startmenü-Eintrag und Desktop-Verknüpfung

Einige Funktionen der bisherigen Konsolenanwendung sind noch nicht vollständig in die JavaFX-Oberfläche integriert.

## Installation unter Windows

### Voraussetzungen

Für die Installation und Nutzung der bereitgestellten Windows-Version sind weder eine separate Java-Installation noch Maven oder IntelliJ erforderlich.

### Installation

1. `FinanceManager-1.0.0.exe` herunterladen.
2. Installationsprogramm ausführen.
3. Installationsanweisungen folgen.
4. Finance Manager über das Startmenü oder die Desktop-Verknüpfung starten.

Der Installer enthält die benötigte Java-Laufzeitumgebung.

### Portable Version

Alternativ kann die Anwendung ohne Installer verwendet werden.

Dazu muss der gesamte durch `jpackage` erzeugte Anwendungsordner kopiert werden.

Anschliessend kann `FinanceManager.exe` direkt gestartet werden.

## Datenspeicherung

Finance Manager verwendet SQLite zur lokalen Speicherung der Finanzdaten.

Die Datenbank befindet sich unter Windows im Verzeichnis:

`%LOCALAPPDATA%\FinanceManager\finance-manager.db`

Dieses Verzeichnis liegt ausserhalb des Installationsordners.

Dadurch bleiben die Finanzdaten bei einem regulären Anwendungsupdate grundsätzlich erhalten, sofern die Datenbank nicht gelöscht oder durch eine inkompatible Schemaänderung verändert wird.

**Hinweis:** Vor Updates wird empfohlen, eine Sicherung der Datenbank anzulegen.

### Datenbanktabellen

**Accounts**
- ID
- Name
- Kontotyp
- Eröffnungssaldo
- Währung

**Transactions**
- ID
- Konto
- Datum
- Betrag
- Beschreibung
- Transaktionstyp
- Kategorie

## Anwendung entwickeln

### Voraussetzungen

- JDK 25
- Apache Maven
- IntelliJ IDEA oder eine andere Java-IDE

### Projekt bauen

```powershell
mvn clean package
```

### Tests ausführen

```powershell
mvn test
```

### JavaFX-Anwendung starten

```powershell
mvn javafx:run
```

### Anwendung direkt über Java starten

Nach einem erfolgreichen Maven-Build:

```powershell
java -cp "target/finance-manager-1.0-SNAPSHOT.jar;target/libs/*" ch.andre.financemanager.FinanceManagerLauncher
```

## Architektur

Das Projekt ist in mehrere Verantwortungsbereiche unterteilt.

### Domain Model

Enthält die Finanzmodelle und zugehörigen Datentypen.

Beispiele:
- `Account`
- `Transaction`
- `AccountType`
- `TransactionType`
- `Category`
- `MonthlyReport`
- `YearlyReport`

### Service Layer

Enthält die Geschäftslogik.

Beispiele:
- `FinanceService`
- `CsvImportService`
- `CsvExportService`

### Persistence Layer

Verwaltet die SQLite-Datenbank.

Beispiele:
- `DatabaseManager`
- `AccountRepository`
- `TransactionRepository`
- `AccountLoader`
- `AppPaths`

### Application Layer

Enthält die Einstiegspunkte und die grafische Benutzeroberfläche.

- `Main` – bisherige Konsolenanwendung
- `FinanceManagerApp` – JavaFX-Anwendung
- `FinanceManagerLauncher` – Startklasse für die paketierte Anwendung

## CSV-Format

Der CSV-Import und -Export verwendet Semikolon als Trennzeichen.

Beispiel:

```csv
date;amount;description;type;category
2026-07-01;5000.00;Lohn;INCOME;SALARY
2026-07-05;2500;Miete;EXPENSE;HOUSING
```

Semikolons und Anführungszeichen innerhalb von CSV-Werten werden entsprechend den CSV-Regeln behandelt.

## Qualität und Tests

Die Entwicklung erfolgt schrittweise nach dem Test-Driven-Development-Prinzip.

Der bisherige Testumfang umfasst unter anderem:

- Finanzberechnungen
- Monats- und Jahresberichte
- Kategorieauswertungen
- CSV-Import und CSV-Export
- CSV-Sonderzeichen und Roundtrip-Tests
- SQLite-Persistenz
- Repository-Tests
- Integrationstests

Beim letzten erfolgreichen Testlauf wurden 55 Tests ausgeführt.

### TDD-Ablauf

1. Test schreiben
2. Test schlägt fehl
3. Funktion implementieren
4. Test erfolgreich ausführen
5. Code refaktorieren
6. Nächste Funktion entwickeln

## Versionshistorie

### Version 0.1

- Maven-Projekt eingerichtet
- Git-Repository erstellt
- Projektstruktur aufgebaut

### Version 0.2

- Domänenmodell erstellt
- `Account` und `Transaction` implementiert
- Enums eingeführt

### Version 0.3

- `FinanceService` implementiert
- Kontostand, Einnahmen und Ausgaben berechnen
- Erste JUnit-Tests

### Version 0.4

- CSV-Import mit Validierung
- Fehlerprotokoll
- Refactoring

### Version 0.5

- CSV-Export
- Roundtrip-Test
- Unterstützung für CSV-Sonderzeichen

### Version 1.0 – Konsolenanwendung

- Konsolenmenü
- Transaktionen erfassen
- Kontostand anzeigen
- Monats- und Jahresberichte
- CSV-Import und CSV-Export

### Version 1.1 – Weiterentwicklung

- SQLite-Datenbank integriert
- Datenbankschema für Konten und Transaktionen
- Repository-Klassen implementiert
- Persistenter Default-Account
- Persistenter CSV-Import
- Erweiterte Auswertungen
- Persistenz- und Integrationstests
- JavaFX-Oberfläche eingeführt
- Konten und Transaktionen in der Oberfläche verwalten
- Datenbank in das lokale Windows-Benutzerverzeichnis verlegt
- Separate Startklasse für die paketierte Anwendung
- Portable Windows-Anwendung erstellt
- Windows-Installer mit jpackage und WiX erstellt

### Windows-Paketversion 1.0.0

- Erste lokal getestete installierbare Windows-Version
- Java-Laufzeitumgebung im Installationspaket enthalten
- Startmenü-Eintrag und Desktop-Verknüpfung
- SQLite-Daten ausserhalb des Installationsverzeichnisses

## Geplante Funktionen

- Dashboard
- Diagramme und Finanzvisualisierungen
- Such- und Filterfunktionen
- Budgetplanung
- Sparziele
- Erweiterte Auswertungen in der JavaFX-Oberfläche
- Weitere Verbesserungen der Kontenverwaltung
- Automatische Update-Prüfung
- Veröffentlichung über GitHub Releases
- Datenbankmigrationen bei zukünftigen Schemaänderungen

## Nächste Entwicklungsschritte

1. Git-Repository und `.gitignore` überprüfen
2. Aktuellen Entwicklungsstand committen
3. Windows-Version auf einem weiteren PC testen
4. GitHub Release vorbereiten
5. Versionsverwaltung für zukünftige Updates verbessern
6. Weitere JavaFX-Funktionen entwickeln

## Autor

Andreas
