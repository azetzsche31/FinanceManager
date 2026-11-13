package ch.andre.financemanager.persistence;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AppPaths {

    public static Path getDatabasePath() {

        String localAppData =
                System.getenv("LOCALAPPDATA");

        Path appDirectory =
                Path.of(localAppData, "FinanceManager");

        return appDirectory.resolve("finance-manager.db");
    }

    public static void createDatabaseDirectory() throws IOException {

        Path databasePath = getDatabasePath();

        Files.createDirectories(databasePath.getParent());
    }

    public static void migrateDatabase() throws IOException {

        Path newDatabasePath = getDatabasePath();

        Path oldDatabasePath =
                Path.of("finance-manager.db");

        //Zielverzeichnis erstellen
        createDatabaseDirectory();

        // Bestehende Databank niemals überschreiben
        if (Files.exists(newDatabasePath)) {
            return;
        }

        // Alte Datenbank übernehmen, falls vorhanden
        if (Files.exists(oldDatabasePath)) {

            Files.copy(
                    oldDatabasePath,
                    newDatabasePath
            );

            System.out.println(
                    "Datenbank wurde übernommen: "
                    + newDatabasePath
            );
        }
    }
}
