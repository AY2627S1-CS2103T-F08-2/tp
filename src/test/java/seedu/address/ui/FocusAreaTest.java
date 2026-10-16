package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FocusAreaTest {

    @Test
    public void next_eachArea_followsTabOrderAndWraps() {
        assertEquals(FocusArea.VIEW_TABS, FocusArea.COMMAND_BOX.next());
        assertEquals(FocusArea.LIST, FocusArea.VIEW_TABS.next());
        assertEquals(FocusArea.DETAILS, FocusArea.LIST.next());
        assertEquals(FocusArea.COMMAND_BOX, FocusArea.DETAILS.next());
    }

    @Test
    public void previous_eachArea_reversesTabOrderAndWraps() {
        assertEquals(FocusArea.DETAILS, FocusArea.COMMAND_BOX.previous());
        assertEquals(FocusArea.COMMAND_BOX, FocusArea.VIEW_TABS.previous());
        assertEquals(FocusArea.VIEW_TABS, FocusArea.LIST.previous());
        assertEquals(FocusArea.LIST, FocusArea.DETAILS.previous());
    }

    @Test
    public void nextThenPrevious_eachArea_returnsSameArea() {
        for (FocusArea area : FocusArea.values()) {
            assertEquals(area, area.next().previous());
        }
    }
}
