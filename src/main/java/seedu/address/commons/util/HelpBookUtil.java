package seedu.address.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;

/**
 * Utility class for loading bundled documentation resources.
 */
public class HelpBookUtil {

    private static final Logger logger = LogsCenter.getLogger(HelpBookUtil.class);
    private static final String USER_GUIDE_RESOURCE = "/docs/UserGuide.md";

    /**
     * Reads and returns the bundled User Guide markdown content.
     *
     * @return The raw markdown content of the User Guide.
     * @throws IOException If the resource cannot be found or read.
     */
    public static String getBundledUserGuide() throws IOException {
        InputStream is = HelpBookUtil.class.getResourceAsStream(USER_GUIDE_RESOURCE);
        if (is == null) {
            logger.warning("Resource not found: " + USER_GUIDE_RESOURCE);
            throw new IOException("Resource not found: " + USER_GUIDE_RESOURCE);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            logger.warning("Failed to read " + USER_GUIDE_RESOURCE + ": " + e.getMessage());
            throw e;
        }
    }
}
