package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public void getNextOnOrAfter_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> AppointmentSchedule.empty().getNextOnOrAfter(null));
    }

    @Test
    public void getNextOnOrAfter_noAppointments_returnsEmpty() {
        assertEquals(Optional.empty(), AppointmentSchedule.empty().getNextOnOrAfter(LocalDate.of(2026, 10, 1)));
    }

    @Test
    public void getNextOnOrAfter_allInThePast_returnsEmpty() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY, MIDDLE, LATE));
        assertEquals(Optional.empty(), appointments.getNextOnOrAfter(LocalDate.of(2026, 11, 3)));
    }

    @Test
    public void getNextOnOrAfter_pastAndFuture_returnsEarliestOnOrAfterDate() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY, MIDDLE, LATE));

        // before all appointments -> the earliest one
        assertEquals(Optional.of(EARLY), appointments.getNextOnOrAfter(LocalDate.of(2026, 10, 1)));

        // on the same day, a date-only appointment counts and comes before a timed one
        assertEquals(Optional.of(EARLY), appointments.getNextOnOrAfter(LocalDate.of(2026, 10, 5)));

        // after some appointments -> skips the past ones
        assertEquals(Optional.of(LATE), appointments.getNextOnOrAfter(LocalDate.of(2026, 10, 6)));

        // on the day of a timed appointment -> it counts, whatever its time
        assertEquals(Optional.of(LATE), appointments.getNextOnOrAfter(LocalDate.of(2026, 11, 2)));
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
