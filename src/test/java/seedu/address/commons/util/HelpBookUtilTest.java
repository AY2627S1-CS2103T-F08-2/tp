package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class HelpBookUtilTest {

    @Test
    public void getBundledUserGuide_returnsNonEmptyContent() {
        String content = HelpBookUtil.getBundledUserGuide();
        assertNotNull(content);
        assertFalse(content.isBlank());
    }

    @Test
    public void getAllCommandsSummary_containsCoreCommands() {
        String summary = HelpBookUtil.getAllCommandsSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("add"));
        assertTrue(summary.contains("delete"));
        assertTrue(summary.contains("list"));
    }

    @Test
    public void getSpecificCommandHelp_validCommand_returnsHelp() {
        Optional<String> deleteHelp = HelpBookUtil.getSpecificCommandHelp("delete");
        assertTrue(deleteHelp.isPresent());
        assertTrue(deleteHelp.get().contains("Command: delete"));
        assertTrue(deleteHelp.get().contains("Format:"));
    }

    @Test
    public void getSpecificCommandHelp_invalidCommand_returnsEmpty() {
        Optional<String> invalidHelp = HelpBookUtil.getSpecificCommandHelp("unknowncommand123");
        assertTrue(invalidHelp.isEmpty());
    }
}
