package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class FocusAreaTest {

    @Test
    public void isInView_onlyTabsAndList() {
        assertFalse(FocusArea.COMMAND_BOX.isInView());
        assertTrue(FocusArea.VIEW_TABS.isInView());
        assertTrue(FocusArea.LIST.isInView());
    }

    @Test
    public void afterTab_fromView_goesToCommandBox() {
        assertEquals(FocusArea.COMMAND_BOX, FocusArea.VIEW_TABS.afterTab(FocusArea.LIST));
        assertEquals(FocusArea.COMMAND_BOX, FocusArea.LIST.afterTab(FocusArea.VIEW_TABS));
    }

    @Test
    public void afterTab_fromCommandBox_goesBackToLastViewArea() {
        assertEquals(FocusArea.VIEW_TABS, FocusArea.COMMAND_BOX.afterTab(FocusArea.VIEW_TABS));
        assertEquals(FocusArea.LIST, FocusArea.COMMAND_BOX.afterTab(FocusArea.LIST));
    }

    @Test
    public void afterTab_twice_returnsToStart() {
        for (FocusArea area : FocusArea.values()) {
            FocusArea lastViewArea = area.isInView() ? area : FocusArea.LIST;
            assertEquals(area, area.afterTab(lastViewArea).afterTab(lastViewArea));
        }
    }

    @Test
    public void afterTab_invalidLastViewArea_throws() {
        assertThrows(NullPointerException.class, () -> FocusArea.LIST.afterTab(null));
        assertThrows(IllegalArgumentException.class, () -> FocusArea.COMMAND_BOX.afterTab(FocusArea.COMMAND_BOX));
    }
}
