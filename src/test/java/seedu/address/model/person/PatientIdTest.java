package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PatientIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PatientId(null));
    }

    @Test
    public void constructor_invalidId_throwsIllegalArgumentException() {
        String invalidId = "";
        assertThrows(IllegalArgumentException.class, () -> new PatientId(invalidId));
    }

    @Test
    public void isValidId() {
        // null id
        assertThrows(NullPointerException.class, () -> PatientId.isValidId(null));

        // invalid ids
        assertFalse(PatientId.isValidId("")); // empty string
        assertFalse(PatientId.isValidId(" ")); // spaces only
        assertFalse(PatientId.isValidId("S1234567ABC")); // 11 characters, over the limit
        assertFalse(PatientId.isValidId("S1234-567")); // hyphen
        assertFalse(PatientId.isValidId("S123 4567A")); // space within the ID
        assertFalse(PatientId.isValidId("S1234567A!")); // symbol
        assertFalse(PatientId.isValidId("É1234567A")); // non-ASCII letter

        // valid ids
        assertTrue(PatientId.isValidId("1")); // 1 character
        assertTrue(PatientId.isValidId("S1234567A")); // NRIC format
        assertTrue(PatientId.isValidId("S1234567AB")); // 10 characters, at the limit
        assertTrue(PatientId.isValidId("E12345678")); // foreign ID, not NRIC format
        assertTrue(PatientId.isValidId("s1234567a")); // lower case
        assertTrue(PatientId.isValidId("  S1234567A ")); // surrounding whitespace is ignored
    }

    @Test
    public void constructor_normalizesValue() {
        assertEquals("S1234567A", new PatientId(" s1234567a ").value);
    }

    @Test
    public void equals() {
        PatientId id = new PatientId("S1234567A");

        // same values -> returns true
        assertTrue(id.equals(new PatientId("S1234567A")));

        // differs only in case -> returns true
        assertTrue(id.equals(new PatientId("s1234567a")));

        // same object -> returns true
        assertTrue(id.equals(id));

        // null -> returns false
        assertFalse(id.equals(null));

        // different types -> returns false
        assertFalse(id.equals(5.0f));

        // different values -> returns false
        assertFalse(id.equals(new PatientId("S7654321B")));
    }

    @Test
    public void hashCode_sameIdDifferentCase_equal() {
        assertEquals(new PatientId("S1234567A").hashCode(), new PatientId("s1234567a").hashCode());
    }
}
