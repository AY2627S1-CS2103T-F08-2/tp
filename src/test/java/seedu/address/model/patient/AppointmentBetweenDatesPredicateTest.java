package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PatientBuilder;

public class AppointmentBetweenDatesPredicateTest {

    @Test
    public void constructor_nullStartDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
            -> new AppointmentBetweenDatesPredicate(null, LocalDate.of(2026, 10, 20)));
    }

    @Test
    public void constructor_nullEndDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, ()
            -> new AppointmentBetweenDatesPredicate(LocalDate.of(2026, 10, 15), null));
    }

    @Test
    public void constructor_startDateAfterEndDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, ()
            -> new AppointmentBetweenDatesPredicate(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 15)));
    }

    @Test
    public void equals() {
        LocalDate firstStartDate = LocalDate.of(2026, 10, 15);
        LocalDate firstEndDate = LocalDate.of(2026, 10, 20);
        LocalDate secondStartDate = LocalDate.of(2026, 10, 25);
        LocalDate secondEndDate = LocalDate.of(2026, 10, 30);

        AppointmentBetweenDatesPredicate firstPredicate =
                new AppointmentBetweenDatesPredicate(firstStartDate, firstEndDate);
        AppointmentBetweenDatesPredicate secondPredicate =
                new AppointmentBetweenDatesPredicate(secondStartDate, secondEndDate);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        AppointmentBetweenDatesPredicate firstPredicateCopy =
                new AppointmentBetweenDatesPredicate(firstStartDate, firstEndDate);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different date range -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_appointmentBetweenDates_returnsTrue() {
        // Appointment on same date as start date
        LocalDate startDate = LocalDate.of(2026, 10, 15);
        LocalDate endDate = LocalDate.of(2026, 10, 20);
        AppointmentBetweenDatesPredicate predicate = new AppointmentBetweenDatesPredicate(startDate, endDate);
        assertTrue(predicate.test(new PatientBuilder().withNextAppointment("2026-10-15").build()));

        // Timed appointment on same date as start date
        assertTrue(predicate.test(new PatientBuilder().withNextAppointment("2026-10-15 09:30").build()));

        // Appointment on same date as end date
        assertTrue(predicate.test(new PatientBuilder().withNextAppointment("2026-10-20").build()));

        // Timed appointment on same date as end date
        assertTrue(predicate.test(new PatientBuilder().withNextAppointment("2026-10-20 23:59").build()));

        // Appointment in the middle of the range
        assertTrue(predicate.test(new PatientBuilder().withNextAppointment("2026-10-17").build()));

        // Timed appointment in the middle of the range
        assertTrue(predicate.test(new PatientBuilder().withNextAppointment("2026-10-17 14:00").build()));
    }

    @Test
    public void test_appointmentOutsideDateRange_returnsFalse() {
        LocalDate startDate = LocalDate.of(2026, 10, 15);
        LocalDate endDate = LocalDate.of(2026, 10, 20);
        AppointmentBetweenDatesPredicate predicate = new AppointmentBetweenDatesPredicate(startDate, endDate);

        // Appointment before start date
        assertFalse(predicate.test(new PatientBuilder().withNextAppointment("2026-10-14").build()));

        // Appointment after end date
        assertFalse(predicate.test(new PatientBuilder().withNextAppointment("2026-10-21").build()));

        // Patient with no next appointment
        assertFalse(predicate.test(new PatientBuilder().build()));
    }

    @Test
    public void toStringMethod() {
        LocalDate startDate = LocalDate.of(2026, 10, 15);
        LocalDate endDate = LocalDate.of(2026, 10, 20);
        AppointmentBetweenDatesPredicate predicate = new AppointmentBetweenDatesPredicate(startDate, endDate);

        String expected = AppointmentBetweenDatesPredicate.class.getCanonicalName()
                + "{startDate=" + startDate + ", endDate=" + endDate + "}";
        assertEquals(expected, predicate.toString());
    }
}
