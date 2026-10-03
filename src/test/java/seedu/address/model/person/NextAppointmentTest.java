package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class NextAppointmentTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NextAppointment(null));
    }

    @Test
    public void constructor_invalidNextAppointment_throwsIllegalArgumentException() {
        String invalidNextAppointment = "2026-02-30";
        assertThrows(IllegalArgumentException.class, () -> new NextAppointment(invalidNextAppointment));
    }

    @Test
    public void isValidNextAppointment() {
        // null next appointment
        assertThrows(NullPointerException.class, () -> NextAppointment.isValidNextAppointment(null));

        // invalid next appointments
        assertFalse(NextAppointment.isValidNextAppointment("")); // empty string
        assertFalse(NextAppointment.isValidNextAppointment(" ")); // spaces only
        assertFalse(NextAppointment.isValidNextAppointment("2026/09/20")); // wrong separator
        assertFalse(NextAppointment.isValidNextAppointment("20-09-2026")); // wrong order
        assertFalse(NextAppointment.isValidNextAppointment("2026-9-20")); // month not zero-padded
        assertFalse(NextAppointment.isValidNextAppointment("2026-13-01")); // month out of range
        assertFalse(NextAppointment.isValidNextAppointment("2026-02-30")); // day does not exist
        assertFalse(NextAppointment.isValidNextAppointment("2026-02-29")); // not a leap year
        assertFalse(NextAppointment.isValidNextAppointment("2026-09-20 24:00")); // hour out of range
        assertFalse(NextAppointment.isValidNextAppointment("2026-09-20 2pm")); // 12-hour time
        assertFalse(NextAppointment.isValidNextAppointment("2026-09-20 9:00")); // hour not zero-padded
        assertFalse(NextAppointment.isValidNextAppointment("2026-09-20T14:00")); // ISO 'T' separator
        assertFalse(NextAppointment.isValidNextAppointment("2026-09-20 14:00 extra")); // trailing text
        assertFalse(NextAppointment.isValidNextAppointment("tomorrow")); // not a date

        // valid next appointments
        assertTrue(NextAppointment.isValidNextAppointment("2026-09-20")); // date only
        assertTrue(NextAppointment.isValidNextAppointment("2026-09-20 14:00")); // date and time
        assertTrue(NextAppointment.isValidNextAppointment("2026-09-20 00:00")); // midnight
        assertTrue(NextAppointment.isValidNextAppointment("2028-02-29")); // leap day
        assertTrue(NextAppointment.isValidNextAppointment(" 2026-09-20   14:00 ")); // extra whitespace is ignored
    }

    @Test
    public void constructor_dateOnly_noTime() {
        NextAppointment appointment = new NextAppointment("2026-09-20");
        assertEquals(LocalDate.of(2026, 9, 20), appointment.getDate());
        assertEquals(Optional.empty(), appointment.getTime());
        assertEquals("2026-09-20", appointment.value);
    }

    @Test
    public void constructor_dateAndTime_hasTime() {
        NextAppointment appointment = new NextAppointment("  2026-09-20   14:00 ");
        assertEquals(LocalDate.of(2026, 9, 20), appointment.getDate());
        assertEquals(Optional.of(LocalTime.of(14, 0)), appointment.getTime());
        assertEquals("2026-09-20 14:00", appointment.value);
    }

    @Test
    public void equals() {
        NextAppointment appointment = new NextAppointment("2026-09-20 14:00");

        // same values -> returns true
        assertTrue(appointment.equals(new NextAppointment("2026-09-20 14:00")));

        // same values with extra whitespace -> returns true
        assertTrue(appointment.equals(new NextAppointment(" 2026-09-20  14:00")));

        // same object -> returns true
        assertTrue(appointment.equals(appointment));

        // null -> returns false
        assertFalse(appointment.equals(null));

        // different types -> returns false
        assertFalse(appointment.equals(5.0f));

        // different time -> returns false
        assertFalse(appointment.equals(new NextAppointment("2026-09-20 15:00")));

        // same date without time -> returns false
        assertFalse(appointment.equals(new NextAppointment("2026-09-20")));
    }
}
