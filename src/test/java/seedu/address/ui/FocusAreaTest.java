package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FocusAreaTest {

    @Test
    public void next_eachArea_followsTabOrderAndWraps() {
        assertEquals(FocusArea.PATIENT_LIST, FocusArea.COMMAND_BOX.next());
        assertEquals(FocusArea.PATIENT_DETAILS, FocusArea.PATIENT_LIST.next());
        assertEquals(FocusArea.COMMAND_BOX, FocusArea.PATIENT_DETAILS.next());
    }

    @Test
    public void previous_eachArea_reversesTabOrderAndWraps() {
        assertEquals(FocusArea.PATIENT_DETAILS, FocusArea.COMMAND_BOX.previous());
        assertEquals(FocusArea.COMMAND_BOX, FocusArea.PATIENT_LIST.previous());
        assertEquals(FocusArea.PATIENT_LIST, FocusArea.PATIENT_DETAILS.previous());
    }

    @Test
    public void nextThenPrevious_eachArea_returnsSameArea() {
        for (FocusArea area : FocusArea.values()) {
            assertEquals(area, area.next().previous());
        }
    }
}
