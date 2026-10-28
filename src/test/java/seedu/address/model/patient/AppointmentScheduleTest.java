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
    public void constructor_anyNumbers_numbersFromOneInDateOrder() {
        // numbers that are not in date order, with a gap, and one appointment with no number
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(new Appointment("2026-11-01").withNumber(1),
                new Appointment("2026-09-01").withNumber(7), new Appointment("2026-10-01")));
        assertEquals(List.of("1 2026-09-01", "2 2026-10-01", "3 2026-11-01"), numbered(schedule));

        // no time comes first on its date
        assertEquals(List.of("1 2026-10-05", "2 2026-10-05 09:30", "3 2026-11-02 14:00"),
                numbered(new AppointmentSchedule(List.of(LATE, MIDDLE, EARLY))));
    }

    @Test
    public void constructor_sameDateAndTime_numberedFirstThenInInputOrder() {
        Appointment first = new Appointment("2026-10-05 09:30", Optional.of(new AppointmentDetails("First")));
        Appointment second = new Appointment("2026-10-05 09:30", Optional.of(new AppointmentDetails("Second")));
        Appointment numbered = new Appointment("2026-10-05 09:30", Optional.of(new AppointmentDetails("Numbered")))
                .withNumber(9);
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(new Appointment("2026-11-02"), first, second,
                numbered));
        assertEquals(List.of(numbered.withoutNumber().withNumber(1), first.withNumber(2), second.withNumber(3),
                new Appointment("2026-11-02").withNumber(4)), schedule.asList());
    }

    @Test
    public void empty() {
        assertTrue(AppointmentSchedule.empty().isEmpty());
        assertFalse(new AppointmentSchedule(List.of(EARLY)).isEmpty());
    }

    @Test
    public void add() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(MIDDLE, LATE));

        // an earlier appointment renumbers the later ones
        assertEquals(List.of(EARLY, MIDDLE, LATE), appointments.add(EARLY).asList());

        // the number of the added appointment is ignored, and one at the same date and time comes after
        AppointmentSchedule withSecond = appointments.add(new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Second"))).withNumber(1));
        assertEquals(List.of("1 2026-10-05 09:30", "2 2026-10-05 09:30 SECOND", "3 2026-11-02 14:00"),
                numbered(withSecond));
        assertEquals("SECOND", withSecond.getLastAt(MIDDLE).getDetails().orElseThrow().value);

        // original is unchanged
        assertEquals(List.of("1 2026-10-05 09:30", "2 2026-11-02 14:00"), numbered(appointments));
    }

    @Test
    public void remove() {
        AppointmentSchedule appointments = new AppointmentSchedule(List.of(EARLY, MIDDLE, LATE));

        // the appointments after the removed one move up one number
        assertEquals(List.of("1 2026-10-05", "2 2026-11-02 14:00"), numbered(appointments.remove(MIDDLE)));

        // removing a missing appointment has no effect
        AppointmentSchedule one = new AppointmentSchedule(List.of(EARLY));
        assertEquals(one, one.remove(MIDDLE));

        // original is unchanged
        assertEquals(List.of(EARLY, MIDDLE, LATE), appointments.asList());
    }

    @Test
    public void replace_sameDateAndTime_keepsPlaceAndNumber() {
        Appointment second = new Appointment("2026-10-05 09:30").withNumber(3);
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(EARLY, MIDDLE, second, LATE.withoutNumber()));

        // new details and a completion mark keep the appointment before the other one at its time
        Appointment edited = new Appointment(2, "2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Review"))).withDone(true);
        AppointmentSchedule replaced = schedule.replace(MIDDLE, edited);
        assertEquals(edited, replaced.findByNumber(2).orElseThrow());
        assertEquals(List.of("1 2026-10-05", "2 2026-10-05 09:30 REVIEW", "3 2026-10-05 09:30", "4 2026-11-02 14:00"),
                numbered(replaced));
    }

    @Test
    public void replace_otherDateOrTime_movesAfterTheAppointmentsThere() {
        Appointment second = new Appointment("2026-10-05 09:30").withNumber(3);
        AppointmentSchedule schedule = new AppointmentSchedule(List.of(EARLY, MIDDLE, second, LATE.withoutNumber()));

        // the first appointment at 09:30 moves after the one that is already at 2026-11-02 14:00
        AppointmentSchedule moved = schedule.replace(MIDDLE, new Appointment(2, "2026-11-02 14:00",
                Optional.of(new AppointmentDetails("Moved"))));
        assertEquals(List.of("1 2026-10-05", "2 2026-10-05 09:30", "3 2026-11-02 14:00", "4 2026-11-02 14:00 MOVED"),
                numbered(moved));
        assertEquals(4, moved.getLastAt(LATE).getNumber());

        // moving the date-only appointment after the others renumbers them
        assertEquals(List.of("1 2026-10-05 09:30", "2 2026-10-05 09:30", "3 2026-11-02 14:00", "4 2026-12-01"),
                numbered(schedule.replace(EARLY, new Appointment("2026-12-01"))));

        assertThrows(IllegalArgumentException.class, () -> new AppointmentSchedule(List.of(EARLY))
                .replace(MIDDLE, LATE));
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

    /** Returns each appointment of {@code schedule} as its number, date and time, and details, if any. */
    private static List<String> numbered(AppointmentSchedule schedule) {
        return schedule.asList().stream()
                .map(appointment -> appointment.getNumber() + " " + appointment
                        + appointment.getDetails().map(details -> " " + details).orElse(""))
                .toList();
    }
}
