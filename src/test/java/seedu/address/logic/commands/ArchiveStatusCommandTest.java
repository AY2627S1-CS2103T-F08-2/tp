package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.BENSON;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.PatientScope;
import seedu.address.model.UserPrefs;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code ArchiveCommand} and
 * {@code UnarchiveCommand}.
 */
public class ArchiveStatusCommandTest {

    private Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

    @Test
    public void constructor_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ArchiveCommand(null));
        assertThrows(NullPointerException.class, () -> new UnarchiveCommand(null));
    }

    @Test
    public void execute_archiveInDefaultScope_hidesPatientAndClearsSelection() {
        Patient archived = BENSON.withArchived(true);
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatient(BENSON, archived);

        String expectedMessage = String.format(ArchiveCommand.MESSAGE_ARCHIVE_SUCCESS, BENSON.getName(),
                BENSON.getId());
        assertCommandSuccess(new ArchiveCommand(BENSON.getId()), model,
                new CommandResult(expectedMessage).withSelectionCleared(), expectedModel);
        // the scope stays, so the patient list no longer shows them
        assertEquals(PatientScope.DEFAULT, model.getPatientListScope().getValue());
        assertFalse(model.getFilteredPatientList().contains(archived));
    }

    @Test
    public void execute_archiveInAllScope_staysOnPatient() {
        model.setPatientListScope(PatientScope.ALL);
        Patient archived = BENSON.withArchived(true);
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatientListScope(PatientScope.ALL);
        expectedModel.setPatient(BENSON, archived);

        String expectedMessage = String.format(ArchiveCommand.MESSAGE_ARCHIVE_SUCCESS, BENSON.getName(),
                BENSON.getId());
        assertCommandSuccess(new ArchiveCommand(BENSON.getId()), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        // archived patients come last
        assertEquals(archived, model.getFilteredPatientList().get(model.getFilteredPatientList().size() - 1));
    }

    @Test
    public void execute_unarchive_showsPatientAgain() {
        model.setPatient(BENSON, BENSON.withArchived(true));
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());

        String expectedMessage = String.format(UnarchiveCommand.MESSAGE_UNARCHIVE_SUCCESS, BENSON.getName(),
                BENSON.getId());
        assertCommandSuccess(new UnarchiveCommand(BENSON.getId()), model,
                new CommandResult(expectedMessage).withNavigation(Navigation.toPatient(BENSON.getId())),
                expectedModel);
        assertTrue(model.getFilteredPatientList().contains(BENSON));
    }

    @Test
    public void execute_unarchiveInArchivedScope_hidesPatientAndClearsSelection() {
        model.setPatient(BENSON, BENSON.withArchived(true));
        model.setPatientListScope(PatientScope.ARCHIVED);
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setPatientListScope(PatientScope.ARCHIVED);

        String expectedMessage = String.format(UnarchiveCommand.MESSAGE_UNARCHIVE_SUCCESS, BENSON.getName(),
                BENSON.getId());
        assertCommandSuccess(new UnarchiveCommand(BENSON.getId()), model,
                new CommandResult(expectedMessage).withSelectionCleared(), expectedModel);
        assertTrue(model.getFilteredPatientList().isEmpty());
    }

    @Test
    public void execute_alreadyInStatus_throwsCommandException() {
        assertCommandFailure(new UnarchiveCommand(ALICE.getId()), model,
                String.format(UnarchiveCommand.MESSAGE_NOT_ARCHIVED, ALICE.getName(), ALICE.getId()));

        model.setPatient(ALICE, ALICE.withArchived(true));
        assertCommandFailure(new ArchiveCommand(ALICE.getId()), model,
                String.format(ArchiveCommand.MESSAGE_ALREADY_ARCHIVED, ALICE.getName(), ALICE.getId()));
    }

    @Test
    public void execute_unknownId_throwsCommandException() {
        PatientId unknownId = new PatientId("T9999999Z");
        assertCommandFailure(new ArchiveCommand(unknownId), model,
                String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, unknownId));
        assertCommandFailure(new UnarchiveCommand(unknownId), model,
                String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, unknownId));
    }

    @Test
    public void equals() {
        ArchiveCommand archiveAlice = new ArchiveCommand(ALICE.getId());

        assertTrue(archiveAlice.equals(archiveAlice));
        assertTrue(archiveAlice.equals(new ArchiveCommand(ALICE.getId())));
        assertFalse(archiveAlice.equals(null));
        assertFalse(archiveAlice.equals(1));
        assertFalse(archiveAlice.equals(new ArchiveCommand(BENSON.getId())));
        // archiving and unarchiving the same patient are different commands
        assertNotEquals(archiveAlice, new UnarchiveCommand(ALICE.getId()));
    }

    @Test
    public void getView_patients() {
        assertEquals(CommandResult.View.PATIENTS, new ArchiveCommand(ALICE.getId()).getView().orElseThrow());
        assertEquals(CommandResult.View.PATIENTS, new UnarchiveCommand(ALICE.getId()).getView().orElseThrow());
    }

    @Test
    public void toStringMethod() {
        assertEquals(ArchiveCommand.class.getCanonicalName() + "{targetId=" + ALICE.getId() + ", isArchiving=true}",
                new ArchiveCommand(ALICE.getId()).toString());
        assertEquals(UnarchiveCommand.class.getCanonicalName() + "{targetId=" + ALICE.getId()
                + ", isArchiving=false}", new UnarchiveCommand(ALICE.getId()).toString());
    }
}
