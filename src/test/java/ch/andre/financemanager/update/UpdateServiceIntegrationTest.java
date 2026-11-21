package ch.andre.financemanager.update;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
class UpdateServiceIntegrationTest {

    @Test
    void shouldFetchLatestReleaseFromGitHub() throws Exception {

        UpdateService updateService = new UpdateService();

        String json = updateService.fetchLatestRelease();

        assertTrue(json.contains("\"tag_name\""));
        assertTrue(json.contains("\"html_url\""));
    }
}
