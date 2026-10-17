package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FocusAreaTest {

    @Test
    public void isInView_onlyTabsAndList() {
        assertFalse(FocusArea.COMMAND_BOX.isInView());
        assertTrue(FocusArea.VIEW_TABS.isInView());
        assertTrue(FocusArea.LIST.isInView());
    }

    @Test
    public void afterTab_inView_movesBetweenTabsAndList() {
        assertEquals(FocusArea.LIST, FocusArea.VIEW_TABS.afterTab());
        assertEquals(FocusArea.VIEW_TABS, FocusArea.LIST.afterTab());
    }

    @Test
    public void afterTab_commandBox_staysInCommandBox() {
        // Tab completes a patient ID in the command box, so it never moves the focus from there
        assertEquals(FocusArea.COMMAND_BOX, FocusArea.COMMAND_BOX.afterTab());
    }

    @Test
    public void afterTab_twice_returnsToStart() {
        for (FocusArea area : FocusArea.values()) {
            assertEquals(area, area.afterTab().afterTab());
        }
    }
}
