package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.PatientScope;
import seedu.address.ui.PatientListPanel.Count;

/**
 * Tests the text of the list headers: which patients and appointments a list shows, and how many.
 */
public class ListHeaderTextTest {

    @Test
    public void describeTitle_scopeAndFilter() {
        assertEquals("All patients", PatientListPanel.describeTitle(PatientScope.UNARCHIVED, false));
        assertEquals("All patients", PatientListPanel.describeTitle(PatientScope.ALL, false));
        assertEquals("Archived patients", PatientListPanel.describeTitle(PatientScope.ARCHIVED, false));
        assertEquals("Filtered patients", PatientListPanel.describeTitle(PatientScope.ALL, true));
        assertEquals("Filtered archived patients", PatientListPanel.describeTitle(PatientScope.ARCHIVED, true));
    }

    @Test
    public void describeCount_eachScope_countThenArchivedInBrackets() {
        Count unarchived = new Count(109, 109);
        Count archived = new Count(2, 2);
        assertEquals("109", PatientListPanel.describeCount(PatientScope.UNARCHIVED, unarchived,
                new Count(0, 2)));
        assertEquals("109 (2)", PatientListPanel.describeCount(PatientScope.ALL, unarchived,
                archived));
        assertEquals("(2)", PatientListPanel.describeCount(PatientScope.ARCHIVED, new Count(0, 109),
                archived));
        assertEquals("1 (0)", PatientListPanel.describeCount(PatientScope.ALL, new Count(1, 1),
                new Count(0, 0)));
        assertEquals("0", PatientListPanel.describeCount(PatientScope.UNARCHIVED, new Count(0, 0),
                new Count(0, 0)));
    }

    @Test
    public void describeCount_filtered_shownOfEach() {
        assertEquals("3 of 109", PatientListPanel.describeCount(PatientScope.UNARCHIVED, new Count(3, 109),
                new Count(0, 2)));
        assertEquals("3 of 109 (1 of 2)", PatientListPanel.describeCount(PatientScope.ALL, new Count(3, 109),
                new Count(1, 2)));
        assertEquals("(1 of 2)", PatientListPanel.describeCount(PatientScope.ARCHIVED, new Count(0, 109),
                new Count(1, 2)));
    }

    @Test
    public void describeAppointmentCount_sameFormAsPatients() {
        assertEquals("51", AppointmentListPanel.describeCount(51, 0, PatientScope.UNARCHIVED));
        assertEquals("49 (2)", AppointmentListPanel.describeCount(49, 2, PatientScope.ALL));
        assertEquals("(2)", AppointmentListPanel.describeCount(0, 2, PatientScope.ARCHIVED));
        assertEquals("0", AppointmentListPanel.describeCount(0, 0, PatientScope.UNARCHIVED));
        assertEquals("1 (0)", AppointmentListPanel.describeCount(1, 0, PatientScope.ALL));
    }
}
