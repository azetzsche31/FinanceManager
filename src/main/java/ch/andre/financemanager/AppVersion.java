package ch.andre.financemanager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppVersion {

    public static final String CURRENT_VERSION = "1.0.0";

    private AppVersion() {
        // Verhinder die Erstellung von Objekten
    }

    private static String loadVersion() {

        Properties properties = new Properties();

        try (InputStream input = AppVersion.class
                .getResourceAsStream("/version.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "version.properties wurde nicht gefunden"
                );
            }

            properties.load(input);

            String version = properties.getProperty("app.version");

            if (version == null || version.isBlank()) {
                throw new IllegalStateException(
                        "Programmversion wurde nicht gefunden"
                );
            }

            return version;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Programmversion konnte nicht geladen werden", e
            );
        }
    }
}
