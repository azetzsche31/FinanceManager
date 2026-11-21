package ch.andre.financemanager.update;

import java.io.IOException;

@FunctionalInterface
public interface ReleaseProvider {

    String fetchLatestRelease() throws IOException, InterruptedException;
}
