package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class AppointmentScheduleTest {

    private static final Appointment EARLY = new Appointment("2026-10-05");
    private static final Appointment MIDDLE = new Appointment("2026-10-05 09:30");
    private static final Appointment LATE = new Appointment("2026-11-02 14:00");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AppointmentSchedule(null));
    }

    @Test
    public void constructor_unsortedWithDuplicates_sortedWithoutDuplicates() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(LATE, EARLY, MIDDLE, LATE));
        assertEquals(List.of(EARLY, MIDDLE, LATE), appointments.asList());
    }

    @Test
    public void empty() {
        assertTrue(AppointmentSchedule.empty().isEmpty());
        assertFalse(new AppointmentSchedule(List.of(EARLY)).isEmpty());
    }

    @Test
    public void add() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(LATE));

        // added in order
        assertEquals(List.of(EARLY, LATE), appointments.add(EARLY).asList());

        // adding an existing appointment has no effect
        assertEquals(appointments, appointments.add(LATE));

        // original is unchanged
        assertEquals(List.of(LATE), appointments.asList());
    }

    @Test
    public void remove() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY, LATE));

        assertEquals(List.of(LATE), appointments.remove(EARLY).asList());

        // removing a missing appointment has no effect
        assertEquals(appointments, appointments.remove(MIDDLE));

        // original is unchanged
        assertEquals(List.of(EARLY, LATE), appointments.asList());
    }

    @Test
    public void contains() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY));
        assertTrue(appointments.contains(new Appointment("2026-10-05")));
        assertFalse(appointments.contains(MIDDLE));
    }

    @Test
    public void asList_modified_throwsUnsupportedOperationException() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY));
        assertThrows(UnsupportedOperationException.class, () -> appointments.asList().add(LATE));
    }

    @Test
    public void toStringMethod() {
        assertEquals("", AppointmentSchedule.empty().toString());
        assertEquals("2026-10-05, 2026-11-02 14:00", new AppointmentSchedule(List.of(LATE, EARLY)).toString());
    }

    @Test
    public void equals() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY, LATE));

        // same appointments in a different order -> returns true
        assertTrue(appointments.equals(new AppointmentSchedule(List.of(LATE, EARLY))));

        // same object -> returns true
        assertTrue(appointments.equals(appointments));

        // null -> returns false
        assertFalse(appointments.equals(null));

        // different types -> returns false
        assertFalse(appointments.equals(5.0f));

        // different appointments -> returns false
        assertFalse(appointments.equals(new AppointmentSchedule(List.of(EARLY))));

        // equal objects have equal hash codes
        assertEquals(appointments.hashCode(), new AppointmentSchedule(List.of(LATE, EARLY)).hashCode());
    }
}
