package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.patient.Notes;

public class OptionalFieldEditTest {

    private static final Notes GARDENING = new Notes("Enjoys gardening");
    private static final Notes MORNINGS = new Notes("Prefers mornings");

    @Test
    public void factories_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> OptionalFieldEdit.replace(null));
        assertThrows(NullPointerException.class, () -> OptionalFieldEdit.append(null));
    }

    @Test
    public void applyTo_nullArguments_throwsNullPointerException() {
        OptionalFieldEdit<Notes> edit = OptionalFieldEdit.clear();
        assertThrows(NullPointerException.class, () -> edit.applyTo(null, Notes::new));
        assertThrows(NullPointerException.class, () -> edit.applyTo(Optional.empty(), null));
    }

    @Test
    public void applyTo_replace_givesNewValue() {
        OptionalFieldEdit<Notes> edit = OptionalFieldEdit.replace(MORNINGS);
        assertEquals(Optional.of(MORNINGS), edit.applyTo(Optional.of(GARDENING), Notes::new));
        assertEquals(Optional.of(MORNINGS), edit.applyTo(Optional.empty(), Notes::new));
    }

    @Test
    public void applyTo_clear_givesEmptyField() {
        OptionalFieldEdit<Notes> edit = OptionalFieldEdit.clear();
        assertEquals(Optional.empty(), edit.applyTo(Optional.of(GARDENING), Notes::new));
        assertEquals(Optional.empty(), edit.applyTo(Optional.empty(), Notes::new));
    }

    @Test
    public void applyTo_append_addsAfterSeparator() {
        OptionalFieldEdit<Notes> edit = OptionalFieldEdit.append(MORNINGS);
        assertEquals(Optional.of(new Notes("ENJOYS GARDENING; PREFERS MORNINGS")),
                edit.applyTo(Optional.of(GARDENING), Notes::new));

        // adding to an empty field sets it
        assertEquals(Optional.of(MORNINGS), edit.applyTo(Optional.empty(), Notes::new));
    }

    @Test
    public void equals() {
        OptionalFieldEdit<Notes> replace = OptionalFieldEdit.replace(MORNINGS);

        // same values -> returns true
        assertTrue(replace.equals(OptionalFieldEdit.replace(new Notes("prefers mornings"))));
        assertTrue(OptionalFieldEdit.clear().equals(OptionalFieldEdit.clear()));

        // same object -> returns true
        assertTrue(replace.equals(replace));

        // null or a different type -> returns false
        assertFalse(replace.equals(null));
        assertFalse(replace.equals(MORNINGS));

        // same value, different kind -> returns false
        assertFalse(replace.equals(OptionalFieldEdit.append(MORNINGS)));
        assertFalse(replace.equals(OptionalFieldEdit.clear()));

        // different value -> returns false
        assertFalse(replace.equals(OptionalFieldEdit.replace(GARDENING)));
    }

    @Test
    public void hashCode_equalEdits_sameHashCode() {
        assertEquals(OptionalFieldEdit.append(MORNINGS).hashCode(), OptionalFieldEdit.append(MORNINGS).hashCode());
        assertEquals(OptionalFieldEdit.clear().hashCode(), OptionalFieldEdit.clear().hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals(OptionalFieldEdit.class.getCanonicalName() + "{kind=APPEND, value=PREFERS MORNINGS}",
                OptionalFieldEdit.append(MORNINGS).toString());
        assertEquals(OptionalFieldEdit.class.getCanonicalName() + "{kind=CLEAR, value=null}",
                OptionalFieldEdit.clear().toString());
    }
}
