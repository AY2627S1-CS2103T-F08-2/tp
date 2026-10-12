package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

public class AppointmentsTest {

    private static final Appointment EARLY = new Appointment("2026-10-05");
    private static final Appointment MIDDLE = new Appointment("2026-10-05 09:30");
    private static final Appointment LATE = new Appointment("2026-11-02 14:00");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Appointments(null));
    }

    @Test
    public void constructor_unsortedWithDuplicates_sortedWithoutDuplicates() {
        Appointments appointments = new Appointments(List.of(LATE, EARLY, MIDDLE, LATE));
        assertEquals(List.of(EARLY, MIDDLE, LATE), appointments.asList());
    }

    @Test
    public void empty() {
        assertTrue(Appointments.empty().isEmpty());
        assertFalse(new Appointments(List.of(EARLY)).isEmpty());
    }

    @Test
    public void add() {
        Appointments appointments = new Appointments(List.of(LATE));

        // added in order
        assertEquals(List.of(EARLY, LATE), appointments.add(EARLY).asList());

        // adding an existing appointment has no effect
        assertEquals(appointments, appointments.add(LATE));

        // original is unchanged
        assertEquals(List.of(LATE), appointments.asList());
    }

    @Test
    public void remove() {
        Appointments appointments = new Appointments(List.of(EARLY, LATE));

        assertEquals(List.of(LATE), appointments.remove(EARLY).asList());

        // removing a missing appointment has no effect
        assertEquals(appointments, appointments.remove(MIDDLE));

        // original is unchanged
        assertEquals(List.of(EARLY, LATE), appointments.asList());
    }

    @Test
    public void contains() {
        Appointments appointments = new Appointments(List.of(EARLY));
        assertTrue(appointments.contains(new Appointment("2026-10-05")));
        assertFalse(appointments.contains(MIDDLE));
    }

    @Test
    public void asList_modified_throwsUnsupportedOperationException() {
        Appointments appointments = new Appointments(List.of(EARLY));
        assertThrows(UnsupportedOperationException.class, () -> appointments.asList().add(LATE));
    }

    @Test
    public void toStringMethod() {
        assertEquals("", Appointments.empty().toString());
        assertEquals("2026-10-05, 2026-11-02 14:00", new Appointments(List.of(LATE, EARLY)).toString());
    }

    @Test
    public void equals() {
        Appointments appointments = new Appointments(List.of(EARLY, LATE));

        // same appointments in a different order -> returns true
        assertTrue(appointments.equals(new Appointments(List.of(LATE, EARLY))));

        // same object -> returns true
        assertTrue(appointments.equals(appointments));

        // null -> returns false
        assertFalse(appointments.equals(null));

        // different types -> returns false
        assertFalse(appointments.equals(5.0f));

        // different appointments -> returns false
        assertFalse(appointments.equals(new Appointments(List.of(EARLY))));

        // equal objects have equal hash codes
        assertEquals(appointments.hashCode(), new Appointments(List.of(LATE, EARLY)).hashCode());
    }
}
