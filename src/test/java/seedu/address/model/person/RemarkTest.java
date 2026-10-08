package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_trimsWhitespace() {
        assertEquals("Likes to swim.", new Remark("  Likes to swim.  ").value);
    }

    @Test
    public void isEmpty() {
        assertTrue(new Remark("").isEmpty());
        assertTrue(new Remark("   ").isEmpty());
        assertFalse(new Remark("Likes to swim.").isEmpty());
    }

    @Test
    public void equals() {
        Remark remark = new Remark("Likes to swim.");

        // same values -> returns true
        assertTrue(remark.equals(new Remark("Likes to swim.")));

        // same object -> returns true
        assertTrue(remark.equals(remark));

        // null -> returns false
        assertFalse(remark.equals(null));

        // different types -> returns false
        assertFalse(remark.equals(5.0f));

        // different values -> returns false
        assertFalse(remark.equals(new Remark("Likes to run.")));
    }
}
