package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class MedicalHistoryTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MedicalHistory(null));
    }

    @Test
    public void constructor_invalidMedicalHistory_throwsIllegalArgumentException() {
        String invalidMedicalHistory = "";
        assertThrows(IllegalArgumentException.class, () -> new MedicalHistory(invalidMedicalHistory));
    }

    @Test
    public void isValidMedicalHistory() {
        // null medical history
        assertThrows(NullPointerException.class, () -> MedicalHistory.isValidMedicalHistory(null));

        // invalid medical history
        assertFalse(MedicalHistory.isValidMedicalHistory("")); // empty string
        assertFalse(MedicalHistory.isValidMedicalHistory("   ")); // spaces only

        // valid medical history
        assertTrue(MedicalHistory.isValidMedicalHistory("Dementia"));
        assertTrue(MedicalHistory.isValidMedicalHistory("-")); // one character
        assertTrue(MedicalHistory.isValidMedicalHistory(
                "Type 2 diabetes (since 2019); allergic to penicillin & shellfish; BP 140/90")); // free text
    }

    @Test
    public void constructor_normalizesValue() {
        assertEquals("ALLERGIC TO PENICILLIN", new MedicalHistory("  allergic to\n penicillin ").value);
    }

    @Test
    public void equals() {
        MedicalHistory medicalHistory = new MedicalHistory("Dementia");

        // same values -> returns true
        assertTrue(medicalHistory.equals(new MedicalHistory("Dementia")));

        // differs only in case -> returns true
        assertTrue(medicalHistory.equals(new MedicalHistory("dementia")));

        // same object -> returns true
        assertTrue(medicalHistory.equals(medicalHistory));

        // null -> returns false
        assertFalse(medicalHistory.equals(null));

        // different types -> returns false
        assertFalse(medicalHistory.equals(5.0f));

        // different values -> returns false
        assertFalse(medicalHistory.equals(new MedicalHistory("Asthma")));
    }
}
