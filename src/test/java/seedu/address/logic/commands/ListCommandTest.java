package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPatientAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    /** list shows all patients, and selects the first. */
    private static final CommandResult LIST_RESULT = new CommandResult(ListCommand.MESSAGE_SUCCESS)
            .withNavigation(Navigation.toView(View.PATIENTS));

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel = new ModelManager(model.getLaiNurse(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, LIST_RESULT, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPatientAtIndex(model, INDEX_FIRST_PATIENT);
        assertCommandSuccess(new ListCommand(), model, LIST_RESULT, expectedModel);
    }
}
