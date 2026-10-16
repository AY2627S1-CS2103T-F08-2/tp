package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPatientAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.PatientId;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ViewCommand}.
 */
public class ViewCommandTest {

    private Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void execute_existingId_showsPatient() {
        PatientId bensonId = new PatientId("S2345678B");
        CommandResult expectedResult = new CommandResult("Showing patient: BENSON MEIER (S2345678B)", bensonId);
        Model expectedModel = new ModelManager(model.getLaiNurse(), new UserPrefs());

        assertCommandSuccess(new ViewCommand(bensonId), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_lowercaseId_showsPatient() {
        PatientId bensonId = new PatientId("s2345678b");
        CommandResult expectedResult = new CommandResult("Showing patient: BENSON MEIER (S2345678B)",
                new PatientId("S2345678B"));
        Model expectedModel = new ModelManager(model.getLaiNurse(), new UserPrefs());

        assertCommandSuccess(new ViewCommand(bensonId), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_patientHiddenByFilter_showsPatientAndKeepsFilter() {
        showPatientAtIndex(model, INDEX_FIRST_PATIENT);
        PatientId bensonId = new PatientId("S2345678B");
        CommandResult expectedResult = new CommandResult("Showing patient: BENSON MEIER (S2345678B)", bensonId);
        Model expectedModel = new ModelManager(model.getLaiNurse(), new UserPrefs());
        showPatientAtIndex(expectedModel, INDEX_FIRST_PATIENT);

        assertCommandSuccess(new ViewCommand(bensonId), model, expectedResult, expectedModel);
    }

    @Test
    public void execute_nonExistentId_throwsCommandException() {
        ViewCommand viewCommand = new ViewCommand(new PatientId("T9999999Z"));

        assertCommandFailure(viewCommand, model, Messages.MESSAGE_INVALID_PATIENT_ID);
    }

    @Test
    public void execute_emptyLaiNurse_throwsCommandException() {
        Model emptyModel = new ModelManager(new LaiNurse(), new UserPrefs());
        ViewCommand viewCommand = new ViewCommand(new PatientId("S2345678B"));

        assertCommandFailure(viewCommand, emptyModel, Messages.MESSAGE_INVALID_PATIENT_ID);
    }

    @Test
    public void equals() {
        ViewCommand viewFirstCommand = new ViewCommand(new PatientId("T0123456A"));
        ViewCommand viewSecondCommand = new ViewCommand(new PatientId("T0123456B"));

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        assertTrue(viewFirstCommand.equals(new ViewCommand(new PatientId("T0123456A"))));

        // same ID in a different case -> returns true
        assertTrue(viewFirstCommand.equals(new ViewCommand(new PatientId("t0123456a"))));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different patient -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        ViewCommand viewCommand = new ViewCommand(new PatientId("T0123456B"));
        String expected = ViewCommand.class.getCanonicalName() + "{targetId=T0123456B}";
        assertEquals(expected, viewCommand.toString());
    }
}
