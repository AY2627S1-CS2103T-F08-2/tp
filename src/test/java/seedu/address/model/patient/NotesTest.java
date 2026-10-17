package seedu.address.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NotesTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Notes(null));
    }

    @Test
    public void constructor_invalidNotes_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Notes(""));
    }

    @Test
    public void isValidNotes() {
        // null notes
        assertThrows(NullPointerException.class, () -> Notes.isValidNotes(null));

        // invalid notes
        assertFalse(Notes.isValidNotes("")); // empty string
        assertFalse(Notes.isValidNotes("   ")); // spaces only

        // valid notes
        assertTrue(Notes.isValidNotes("Has a dog"));
        assertTrue(Notes.isValidNotes("-")); // one character
        assertTrue(Notes.isValidNotes(
                "Daughter Mei Ling (91234567) is the main contact; gate code #4321")); // free text
    }

    @Test
    public void constructor_normalizesValue() {
        assertEquals("HAS A DOG", new Notes("  has a\n dog ").value);
    }

    @Test
    public void equals() {
        Notes notes = new Notes("Has a dog");

        // same values -> returns true
        assertTrue(notes.equals(new Notes("Has a dog")));

        // differs only in case -> returns true
        assertTrue(notes.equals(new Notes("has a dog")));

        // same object -> returns true
        assertTrue(notes.equals(notes));

        // null -> returns false
        assertFalse(notes.equals(null));

        // different types -> returns false
        assertFalse(notes.equals(5.0f));

        // different values -> returns false
        assertFalse(notes.equals(new Notes("Has a cat")));
    }
}
