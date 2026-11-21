package ch.andre.financemanager.update;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.andre.financemanager.AppVersion;

public class UpdateService {

    private static final String GITHUB_API_URL =
            "https://api.github.com/repos/azetzsche31/FinanceManager/releases/latest";

    private final HttpClient client;

    private final ReleaseProvider releaseProvider;

    public UpdateService() {
        this(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build());
    }

    public UpdateService(HttpClient client) {
        this.client = Objects.requireNonNull(client);
        this.releaseProvider = this::fetchLatestRelease;
    }

    public UpdateService(ReleaseProvider releaseProvider) {
        this.client = null;
        this.releaseProvider = Objects.requireNonNull(releaseProvider);
    }

    public String fetchLatestRelease() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "FinanceManager")
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "GitHub API Fehler: HTTP " + response.statusCode()
            );
        }

        return response.body();
    }

    public String extractVersion(String json) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(json);

            JsonNode tagName = root.path("tag_name");

            if (!tagName.isTextual() || tagName.asText().isBlank()) {
                throw new IllegalArgumentException(
                        "Versionsnummer in JSON nicht gefunden."
                );
            }

            return tagName.asText();

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Ungültiges JSON-Format", e
            );
        }
    }

    public String extractReleaseUrl(String json) {

        try {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(json);
            JsonNode releaseUrl = root.path("html_url");

            if (!releaseUrl.isTextual() ||releaseUrl.asText().isBlank()) {
                throw new IllegalArgumentException(
                        "Release-URL in JSON nicht gefunden"
                );
            }

            String url = releaseUrl.asText();

            if (!url.startsWith(
                    "https://github.com/azetzsche31/FinanceManager/releases/")) {
                throw new IllegalArgumentException(
                        "Ungültige Release-URL"
                );
            }

            return url;

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Ungültiges JSON-Format", e
            );
        }
    }

    public boolean isNewerVersion(String currentVersion, String latestVersion) {

        int[] current = parseVersion(currentVersion);
        int[] latest = parseVersion(latestVersion);

        for (int i = 0; i < 3; i++) {
            if (latest[i] > current[i]) {
                return true;
            }

            if (latest[i] < current[i]) {
                return false;
            }
        }

        return false;

    }

    private int[] parseVersion(String version) {

        if (version == null) {
            throw new IllegalArgumentException("Versionsnummer darf nicht null sein");
        }

        String normalized = version
                .replaceFirst("^v", "")
                .replaceFirst("-SNAPSHOT$","");

        if (!normalized.matches("\\d+\\.\\d+(\\.\\d+)?")) {
            throw new IllegalArgumentException(
                    "Ungültige Versionsnummer: " + version
            );
        }

        String[] parts = normalized.split("\\.");

        return new int[]{
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                parts.length == 3 ? Integer.parseInt(parts[2]) : 0
        };
    }


    public boolean isUpdateAvailable() throws IOException, InterruptedException {

        String json = releaseProvider.fetchLatestRelease();

        return isUpdateAvailable(json);
    }

    public boolean isUpdateAvailable(String releaseJson) {

        String latestVersion = extractVersion(releaseJson);

        return isNewerVersion(
                AppVersion.CURRENT_VERSION,
                latestVersion
        );
    }
}
