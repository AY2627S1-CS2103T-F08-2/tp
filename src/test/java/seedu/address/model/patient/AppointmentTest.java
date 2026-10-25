package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class AppointmentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Appointment(null));
    }

    @Test
    public void constructor_invalidAppointment_throwsIllegalArgumentException() {
        String invalidAppointment = "2026-02-30";
        assertThrows(IllegalArgumentException.class, () -> new Appointment(invalidAppointment));
    }

    @Test
    public void isValidAppointment() {
        // null appointment
        assertThrows(NullPointerException.class, () -> Appointment.isValidAppointment(null));

        // invalid appointments
        assertFalse(Appointment.isValidAppointment("")); // empty string
        assertFalse(Appointment.isValidAppointment(" ")); // spaces only
        assertFalse(Appointment.isValidAppointment("2026/09/20")); // wrong separator
        assertFalse(Appointment.isValidAppointment("20-09-2026")); // wrong order
        assertFalse(Appointment.isValidAppointment("2026-9-20")); // month not zero-padded
        assertFalse(Appointment.isValidAppointment("2026-13-01")); // month out of range
        assertFalse(Appointment.isValidAppointment("2026-02-30")); // day does not exist
        assertFalse(Appointment.isValidAppointment("2026-02-29")); // not a leap year
        assertFalse(Appointment.isValidAppointment("2026-09-20 24:00")); // hour out of range
        assertFalse(Appointment.isValidAppointment("2026-09-20 2pm")); // 12-hour time
        assertFalse(Appointment.isValidAppointment("2026-09-20 9:00")); // hour not zero-padded
        assertFalse(Appointment.isValidAppointment("2026-09-20T14:00")); // ISO 'T' separator
        assertFalse(Appointment.isValidAppointment("2026-09-20 14:00 extra")); // trailing text
        assertFalse(Appointment.isValidAppointment("tomorrow")); // not a date

        // valid appointments
        assertTrue(Appointment.isValidAppointment("2026-09-20")); // date only
        assertTrue(Appointment.isValidAppointment("2026-09-20 14:00")); // date and time
        assertTrue(Appointment.isValidAppointment("2026-09-20 00:00")); // midnight
        assertTrue(Appointment.isValidAppointment("2028-02-29")); // leap day
        assertTrue(Appointment.isValidAppointment(" 2026-09-20   14:00 ")); // extra whitespace is ignored
    }

    @Test
    public void constructor_dateOnly_noTime() {
        Appointment appointment = new Appointment("2026-09-20");
        assertEquals(LocalDate.of(2026, 9, 20), appointment.getDate());
        assertEquals(Optional.empty(), appointment.getTime());
        assertEquals("2026-09-20", appointment.value);
    }

    @Test
    public void constructor_dateAndTime_hasTime() {
        Appointment appointment = new Appointment("  2026-09-20   14:00 ");
        assertEquals(LocalDate.of(2026, 9, 20), appointment.getDate());
        assertEquals(Optional.of(LocalTime.of(14, 0)), appointment.getTime());
        assertEquals("2026-09-20 14:00", appointment.value);
    }

    @Test
    public void equals() {
        Appointment appointment = new Appointment("2026-09-20 14:00");

        // same values -> returns true
        assertTrue(appointment.equals(new Appointment("2026-09-20 14:00")));

        // same values with extra whitespace -> returns true
        assertTrue(appointment.equals(new Appointment(" 2026-09-20  14:00")));

        // same object -> returns true
        assertTrue(appointment.equals(appointment));

        // null -> returns false
        assertFalse(appointment.equals(null));

        // different types -> returns false
        assertFalse(appointment.equals(5.0f));

        // different time -> returns false
        assertFalse(appointment.equals(new Appointment("2026-09-20 15:00")));

        // same date without time -> returns false
        assertFalse(appointment.equals(new Appointment("2026-09-20")));
    }

    @Test
    public void compareTo() {
        Appointment dateOnly = new Appointment("2026-10-05");
        Appointment morning = new Appointment("2026-10-05 09:30");
        Appointment afternoon = new Appointment("2026-10-05 14:00");
        Appointment nextDay = new Appointment("2026-10-06");

        // a date with no time comes before any time on the same date
        assertTrue(dateOnly.compareTo(morning) < 0);

        // earlier time on the same date comes first
        assertTrue(morning.compareTo(afternoon) < 0);

        // earlier date comes first, whatever the time
        assertTrue(afternoon.compareTo(nextDay) < 0);
        assertTrue(nextDay.compareTo(dateOnly) > 0);

        // equal appointments compare as equal
        assertEquals(0, morning.compareTo(new Appointment("2026-10-05 09:30")));
    }

    @Test
    public void constructor_nullDetails_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Appointment("2026-10-05", null));
    }

    @Test
    public void getDetails() {
        assertEquals(Optional.empty(), new Appointment("2026-10-05").getDetails());
        AppointmentDetails details = new AppointmentDetails("Insulin review");
        assertEquals(Optional.of(details), new Appointment("2026-10-05", Optional.of(details)).getDetails());
    }

    @Test
    public void isSameAppointment_sameNumber_ignoresTimeAndDetails() {
        Appointment plain = new Appointment("2026-10-05 09:30").withNumber(1);
        Appointment withDetails = new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Insulin review"))).withNumber(1);
        assertTrue(plain.isSameAppointment(withDetails));
        assertTrue(withDetails.isSameAppointment(plain));
        assertEquals(0, plain.compareTo(withDetails));

        assertFalse(plain.isSameAppointment(new Appointment("2026-10-05")));
        assertTrue(plain.isSameAppointment(new Appointment("2026-10-06 09:30").withNumber(1)));
        assertFalse(plain.isSameAppointment(new Appointment("2026-10-05 09:30").withNumber(2)));
        assertFalse(plain.isSameAppointment(null));
    }

    @Test
    public void equals_comparesDetails() {
        Appointment withDetails = new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Insulin review")));
        assertTrue(withDetails.equals(new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("insulin review")))));
        assertEquals(withDetails.hashCode(), new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Insulin review"))).hashCode());

        // the same date and time with other details, or none -> returns false
        assertFalse(withDetails.equals(new Appointment("2026-10-05 09:30",
                Optional.of(new AppointmentDetails("Blood test")))));
        assertFalse(withDetails.equals(new Appointment("2026-10-05 09:30")));
    }
    @Test
    public void number_assignmentValidationAndEquality() {
        Appointment input = new Appointment("2026-10-05");
        assertFalse(input.isNumbered());
        assertThrows(IllegalStateException.class, input::getNumber);
        assertThrows(IllegalArgumentException.class, () -> input.withNumber(0));
        assertThrows(IllegalArgumentException.class, () -> input.withNumber(-1));
        Appointment numbered = input.withNumber(1);
        assertEquals(1, numbered.getNumber());
        assertEquals(numbered, numbered.withNumber(1));
        assertThrows(IllegalArgumentException.class, () -> numbered.withNumber(2));
        assertFalse(numbered.equals(input));
        assertFalse(numbered.equals(input.withNumber(2)));
        assertTrue(numbered.hasSameDateTime(input.withNumber(2)));
        assertTrue(numbered.compareTo(input.withNumber(2)) < 0);
        assertFalse(input.hasSameDateTime(new Appointment("2026-10-05 00:00")));
    }

}
