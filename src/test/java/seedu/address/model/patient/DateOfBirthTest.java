package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class DateOfBirthTest {

    @Test
    public void constructor_invalidDateOfBirth_throwsIllegalArgumentException() {
        // not a date in the format yyyy-MM-dd
        String[] invalidDates = {"", " ", "16-11-1990", "1990-11-16 09:30", "1990-2-16", "1990-02-30"};
        for (String invalid : invalidDates) {
            assertThrows(IllegalArgumentException.class, DateOfBirth.MESSAGE_CONSTRAINTS, () ->
                    new DateOfBirth(invalid));
        }
        // a date of birth is in the past: nobody is born after they are cared for
        String[] futureDates = {LocalDate.now().plusDays(1).toString(), LocalDate.now().plusYears(5).toString()};
        for (String future : futureDates) {
            assertThrows(IllegalArgumentException.class, DateOfBirth.MESSAGE_CONSTRAINTS, () ->
                    new DateOfBirth(future));
        }
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DateOfBirth(null));
    }

    @Test
    public void isValidDateOfBirth() {
        assertTrue(DateOfBirth.isValidDateOfBirth("1990-11-16"));
        assertTrue(DateOfBirth.isValidDateOfBirth(" 1990-11-16 "));
        assertTrue(DateOfBirth.isValidDateOfBirth(LocalDate.now().toString()));

        assertFalse(DateOfBirth.isValidDateOfBirth(""));
        assertFalse(DateOfBirth.isValidDateOfBirth("1990-2-16"));
        assertFalse(DateOfBirth.isValidDateOfBirth("1990-02-30"));
        assertFalse(DateOfBirth.isValidDateOfBirth(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    public void constructor_validDateOfBirth_holdsValue() {
        assertEquals(LocalDate.parse("1990-11-16"), new DateOfBirth("1990-11-16").value);
        assertEquals(LocalDate.parse("1990-11-16"), new DateOfBirth(" 1990-11-16 ").value);
    }

    @Test
    public void isOn_matchesTheMonthAndDay_whateverTheYear() {
        DateOfBirth dob = new DateOfBirth("1990-11-16");
        assertTrue(dob.isOn(LocalDate.parse("2026-11-16")));
        assertTrue(dob.isOn(LocalDate.parse("1990-11-16")));

        assertFalse(dob.isOn(LocalDate.parse("2026-11-17")));
        assertFalse(dob.isOn(LocalDate.parse("2026-12-16")));
        assertFalse(dob.isOn(LocalDate.parse("2026-10-16")));
    }

    @Test
    public void isOn_feb29_matchesA29FebOnly() {
        DateOfBirth leapDay = new DateOfBirth("2000-02-29");
        assertTrue(leapDay.isOn(LocalDate.parse("2028-02-29")));

        // a year without a 29 Feb has no such birthday, and neither the day before nor after it is one
        assertFalse(leapDay.isOn(LocalDate.parse("2027-02-28")));
        assertFalse(leapDay.isOn(LocalDate.parse("2027-03-01")));
    }

    @Test
    public void equals() {
        DateOfBirth dob = new DateOfBirth("1990-11-16");

        // same values -> returns true
        assertEquals(dob, new DateOfBirth("1990-11-16"));

        // same object -> returns true
        assertEquals(dob, dob);

        // null -> returns false
        assertNotEquals(dob, null);

        // different type -> returns false
        assertNotEquals(dob, 5);

        // different date -> returns false
        assertNotEquals(dob, new DateOfBirth("1990-11-17"));
    }

    @Test
    public void hashCode_equalDatesOfBirth_equalHashCodes() {
        assertEquals(new DateOfBirth("1990-11-16").hashCode(), new DateOfBirth("1990-11-16").hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("1990-11-16", new DateOfBirth("1990-11-16").toString());
    }

}
