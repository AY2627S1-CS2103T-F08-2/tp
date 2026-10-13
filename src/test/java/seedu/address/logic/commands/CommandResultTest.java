package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.patient.PatientId;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // patient to view only in one -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", new PatientId("S1234567A"))));

        // same patient to view -> returns true
        CommandResult viewResult = new CommandResult("feedback", new PatientId("S1234567A"));
        assertTrue(viewResult.equals(new CommandResult("feedback", new PatientId("S1234567A"))));

        // different patient to view -> returns false
        assertFalse(viewResult.equals(new CommandResult("feedback", new PatientId("S2345678B"))));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // same patient to view -> returns same hashcode
        assertEquals(new CommandResult("feedback", new PatientId("S1234567A")).hashCode(),
                new CommandResult("feedback", new PatientId("S1234567A")).hashCode());

        // different patient to view -> returns different hashcode
        assertNotEquals(new CommandResult("feedback", new PatientId("S1234567A")).hashCode(),
                new CommandResult("feedback", new PatientId("S2345678B")).hashCode());
    }

    @Test
    public void getPatientToView() {
        assertEquals(Optional.empty(), new CommandResult("feedback").getPatientToView());
        assertEquals(Optional.empty(), new CommandResult("feedback", true, false).getPatientToView());
        assertEquals(Optional.of(new PatientId("S1234567A")),
                new CommandResult("feedback", new PatientId("S1234567A")).getPatientToView());
    }

    @Test
    public void constructor_nullPatientToView_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", (PatientId) null));
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=null}";
        assertEquals(expected, commandResult.toString());

        CommandResult viewResult = new CommandResult("feedback", new PatientId("S1234567A"));
        String expectedView = CommandResult.class.getCanonicalName()
                + "{feedbackToUser=feedback, showHelp=false, exit=false, patientToView=S1234567A}";
        assertEquals(expectedView, viewResult.toString());
    }
}
