package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ID_AMY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.PatientId;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code RemarkCommand}.
 */
public class RemarkCommandTest {

    private static final String REMARK_STUB = "Some remark";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addRemark_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(firstPerson).withRemark(REMARK_STUB).build();

        RemarkCommand remarkCommand = new RemarkCommand(firstPerson.getId(), new Remark(REMARK_STUB));

        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_deleteRemark_success() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithRemark = new PersonBuilder(firstPerson).withRemark(REMARK_STUB).build();
        model.setPerson(firstPerson, personWithRemark);
        Person editedPerson = new PersonBuilder(firstPerson).withRemark("").build();

        RemarkCommand remarkCommand = new RemarkCommand(firstPerson.getId(), new Remark(""));

        String expectedMessage = String.format(RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithRemark, editedPerson);

        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_personNotInFilteredList_success() {
        // the person is found by ID even when hidden by a filter, and the filter is cleared
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person secondPerson = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(secondPerson).withRemark(REMARK_STUB).build();

        RemarkCommand remarkCommand = new RemarkCommand(secondPerson.getId(), new Remark(REMARK_STUB));

        String expectedMessage = String.format(RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS,
                Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(secondPerson, editedPerson);

        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_idNotFound_failure() {
        // VALID_ID_AMY is not in the typical address book
        RemarkCommand remarkCommand = new RemarkCommand(new PatientId(VALID_ID_AMY), new Remark(REMARK_STUB));

        assertCommandFailure(remarkCommand, model, Messages.MESSAGE_INVALID_PERSON_ID);
    }

    @Test
    public void equals() {
        PatientId firstId = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getId();
        PatientId secondId = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()).getId();
        RemarkCommand standardCommand = new RemarkCommand(firstId, new Remark(REMARK_STUB));

        // same values -> returns true
        assertTrue(standardCommand.equals(new RemarkCommand(firstId, new Remark(REMARK_STUB))));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different ID -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(secondId, new Remark(REMARK_STUB))));

        // different remark -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(firstId, new Remark("Another remark"))));
    }

    @Test
    public void toStringMethod() {
        PatientId id = new PatientId(VALID_ID_AMY);
        Remark remark = new Remark(REMARK_STUB);
        RemarkCommand remarkCommand = new RemarkCommand(id, remark);
        String expected = RemarkCommand.class.getCanonicalName() + "{targetId=" + id + ", remark=" + remark + "}";
        assertEquals(expected, remarkCommand.toString());
    }
}
