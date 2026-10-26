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

    private static final Appointment EARLY = new Appointment("2026-10-05").withNumber(1);
    private static final Appointment MIDDLE = new Appointment("2026-10-05 09:30").withNumber(2);
    private static final Appointment LATE = new Appointment("2026-11-02 14:00").withNumber(3);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AppointmentSchedule(null));
    }

    @Test
    public void constructor_duplicateNumbers_throwsIllegalArgumentException() {
        List<Appointment> duplicates = List.of(LATE, EARLY, MIDDLE, LATE);
        String message = AppointmentSchedule.MESSAGE_DUPLICATE_NUMBER;
        assertThrows(IllegalArgumentException.class, message, () -> new AppointmentSchedule(duplicates));
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

        // duplicate numbers are rejected instead of silently dropping information
        assertThrows(IllegalArgumentException.class, () -> appointments.add(LATE));

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
        assertTrue(appointments.contains(EARLY));
        assertFalse(appointments.contains(MIDDLE));
    }

    @Test
    public void find() {
        Appointment middleWithDetails = new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Insulin review"))).withNumber(2);
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY, middleWithDetails));

        // the stored appointment is returned with its own details
        assertEquals(Optional.of(middleWithDetails), appointments.find(MIDDLE));
        assertEquals(Optional.of(EARLY), appointments.find(EARLY));

        // a date-only appointment does not match a timed one on the same day, nor midnight
        assertEquals(Optional.empty(), appointments.find(new Appointment("2026-10-05 00:00")));
        assertEquals(Optional.empty(), appointments.find(LATE));
        assertThrows(NullPointerException.class, () -> appointments.find(null));
    }

    @Test
    public void getOnDate() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(LATE, MIDDLE, EARLY));
        assertEquals(List.of(EARLY, MIDDLE), appointments.getOnDate(LocalDate.of(2026, 10, 5)));
        assertEquals(List.of(LATE), appointments.getOnDate(LocalDate.of(2026, 11, 2)));
        assertEquals(List.of(), appointments.getOnDate(LocalDate.of(2026, 10, 6)));
        assertThrows(NullPointerException.class, () -> appointments.getOnDate(null));
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

    @Test
    public void sameNumber_isSameAppointmentWhateverItsDetails() {
        Appointment middleWithDetails = new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Insulin review"))).withNumber(2);
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(EARLY, middleWithDetails));

        assertTrue(schedule.contains(MIDDLE));
        // duplicate identity is rejected, but a different number at the same time is allowed
        assertThrows(IllegalArgumentException.class, () -> schedule.add(MIDDLE));
        assertEquals(3, schedule.add(new Appointment("2026-10-05 09:30")).asList().size());
        // removing by date and time removes it, whatever its details
        assertEquals(new AppointmentSchedule(List.of(EARLY)), schedule.remove(MIDDLE));
    }
    @Test
    public void constructor_legacyDuplicates_preservesAndNumbersInTimeOrder() {
        Appointment first = new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("First")));
        Appointment second = new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Second")));
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(new Appointment("2026-11-02"), first, second));
        assertEquals(List.of(first.withNumber(1), second.withNumber(2),
                new Appointment("2026-11-02").withNumber(3)), schedule.asList());
    }

    @Test
    public void add_deleteAndMove_usesHighestRemainingNumber() {
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(EARLY, MIDDLE, LATE));
        schedule = schedule.remove(MIDDLE).add(new Appointment("2026-09-01"));
        assertEquals(4, schedule.findAtDateTime(new Appointment("2026-09-01")).getFirst().getNumber());
        assertEquals(1, schedule.asList().get(1).getNumber());
        schedule = schedule.remove(schedule.findByNumber(4).orElseThrow()).remove(LATE);
        assertEquals(2, schedule.getNextNumber());
        Appointment moved = new Appointment(1, "2027-01-01", Optional.empty());
        assertTrue(schedule.contains(moved));
        assertEquals(List.of(moved), schedule.remove(EARLY).add(moved).asList());
        assertEquals(1, AppointmentSchedule.empty().getNextNumber());
    }

    @Test
    public void constructor_mixedNumbers_preservesExplicitAndFillsGaps() {
        Appointment explicit = new Appointment("2026-09-01").withNumber(2);
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(new Appointment("2026-11-01"), explicit,
                new Appointment("2026-10-01")));
        assertEquals(List.of(2, 1, 3), schedule.asList().stream().map(Appointment::getNumber).toList());
    }

    @Test
    public void getNextNumber_maximumNumber_throwsWithoutOverflow() {
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(LATE.withNumber(3)));
        Appointment maximum = new Appointment("2026-10-05").withNumber(Integer.MAX_VALUE);
        schedule = schedule.add(maximum);
        AppointmentSchedule full = schedule;
        assertThrows(IllegalArgumentException.class, AppointmentSchedule.MESSAGE_NUMBER_EXHAUSTED,
                full::getNextNumber);
        assertThrows(IllegalArgumentException.class, () -> full.add(new Appointment("2026-10-06")));
    }

}
