package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class HelpBookUtilTest {

    @Test
    public void getBundledUserGuide_success() throws IOException {
        String guide = HelpBookUtil.getBundledUserGuide();
        assertNotNull(guide);
        assertFalse(guide.strip().isEmpty());
    }
}
