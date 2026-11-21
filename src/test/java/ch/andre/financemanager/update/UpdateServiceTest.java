package ch.andre.financemanager.update;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateServiceTest {


    @Test
    void shouldExtractVersionFromJson() {

        UpdateService updateService = new UpdateService();

        String json = "{\"tag_name\":\"v1.0.0\"}";

        String version = updateService.extractVersion(json);

        assertEquals("v1.0.0", version);
    }

    @Test
    void shouldCompareVersionsCorrectly() {

        UpdateService updateService = new UpdateService();

        assertFalse(updateService.isNewerVersion("1.0.0", "v1.0.0"));

        assertTrue(updateService.isNewerVersion("1.0.0", "v1.1.0"));

        assertFalse(updateService.isNewerVersion("1.2.0", "v1.1.0"));

        assertTrue(updateService.isNewerVersion("1.9.0", "v1.10.0"));

        assertFalse(updateService.isNewerVersion("2.0.0", "v1.9.0"));
    }

    @Test
    void shouldExtractReleaseUrlFromJson() {

        UpdateService updateService = new UpdateService();

        String json = """
                {
                    "tag_name": "v1.0.0",
                    "html_url": "https://github.com/azetzsche31/FinanceManager/releases/tag/v1.0.0"
                    }
                """;

        String releaseUrl = updateService.extractReleaseUrl(json);

        assertEquals(
                "https://github.com/azetzsche31/FinanceManager/releases/tag/v1.0.0",
                releaseUrl
        );
    }


    @Test
    void shouldCompareSnapshotVersions() {

        UpdateService updateService = new UpdateService();

        assertFalse(
                updateService.isNewerVersion("1.0-SNAPSHOT", "v1.0.0")
        );

        assertTrue(
                updateService.isNewerVersion("1.0-SNAPSHOT", "v1.1.0")
        );

        assertTrue(
                updateService.isNewerVersion("1.0-SNAPSHOT", "v1.0.1")
        );

        assertFalse(
                updateService.isNewerVersion("2.0-SNAPSHOT", "v1.9.0")
        );
    }

    @Test
    void shouldDetectUpdateFromJson() {

        UpdateService updateService = new UpdateService();

        String json = """
                {
                    "tag_name": "v1.1.0",
                    "html_url": "https://github.com/azetzsche31/FinanceManager/releases/tag/v1.1.0"
                }
                """;

        assertTrue(updateService.isUpdateAvailable(json));
    }

    @Test
    void shouldRejectInvalidVersions() {

        UpdateService updateService = new UpdateService();

        assertThrows(IllegalArgumentException.class,
                () -> updateService.isNewerVersion("1.0.0", "v.1.1.0"));

        assertThrows(IllegalArgumentException.class,
                () -> updateService.isNewerVersion("abc", "v1.1.0"));

        assertThrows(IllegalArgumentException.class,
                () -> updateService.isNewerVersion(null, "v1.1.0"));
    }

    @Test
    void shouldDetectUpdateWithFakeReleaseProvider() throws Exception {

        ReleaseProvider fakeProvider = () -> """
            {
                "tag_name": "v1.1.0",
                "html_url": "https://github.com/azetzsche31/FinanceManager/releases/tag/v1.1.0"
            }
            """;

        UpdateService updateService = new UpdateService(fakeProvider);

        assertTrue(updateService.isUpdateAvailable());
    }

    @Test
    void shouldNotDetectUpdateWithFakeReleaseProvider() throws Exception {

        ReleaseProvider fakeProvider = () -> """
            {
                "tag_name": "v1.0.0",
                "html_url": "https://github.com/azetzsche31/FinanceManager/releases/tag/v1.0.0"
            }
            """;

        UpdateService updateService = new UpdateService(fakeProvider);

        assertFalse(updateService.isUpdateAvailable());
    }
}
