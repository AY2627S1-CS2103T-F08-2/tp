package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class AppointmentDetailsTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AppointmentDetails(null));
    }

    @Test
    public void constructor_invalidDetails_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AppointmentDetails(""));
    }

    @Test
    public void isValidAppointmentDetails() {
        // null details
        assertThrows(NullPointerException.class, () -> AppointmentDetails.isValidAppointmentDetails(null));

        // invalid details
        assertFalse(AppointmentDetails.isValidAppointmentDetails("")); // empty string
        assertFalse(AppointmentDetails.isValidAppointmentDetails("   ")); // spaces only

        // valid details
        assertTrue(AppointmentDetails.isValidAppointmentDetails("Insulin review"));
        assertTrue(AppointmentDetails.isValidAppointmentDetails("-")); // one character
        assertTrue(AppointmentDetails.isValidAppointmentDetails(
                "Dress the wound on the left foot; bring 2 boxes of gauze & tape")); // free text
    }

    @Test
    public void constructor_normalizesValue() {
        assertEquals("INSULIN REVIEW", new AppointmentDetails("  insulin\n review ").value);
    }

    @Test
    public void equals() {
        AppointmentDetails details = new AppointmentDetails("Insulin review");

        // same values -> returns true
        assertTrue(details.equals(new AppointmentDetails("Insulin review")));

        // differs only in case -> returns true
        assertTrue(details.equals(new AppointmentDetails("insulin review")));

        // same object -> returns true
        assertTrue(details.equals(details));

        // null -> returns false
        assertFalse(details.equals(null));

        // different types -> returns false
        assertFalse(details.equals(5.0f));

        // different values -> returns false
        assertFalse(details.equals(new AppointmentDetails("Blood test")));
    }
}
