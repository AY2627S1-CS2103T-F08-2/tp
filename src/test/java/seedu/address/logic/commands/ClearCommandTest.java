package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPatients.getTypicalLaiNurse;

import org.junit.jupiter.api.Test;

import seedu.address.model.LaiNurse;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyLaiNurse_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        // A command that removes patients selects nothing
        assertCommandSuccess(new ClearCommand(), model,
                new CommandResult(ClearCommand.MESSAGE_SUCCESS).withSelectionCleared(), expectedModel);
    }

    @Test
    public void execute_nonEmptyLaiNurse_success() {
        Model model = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalLaiNurse(), new UserPrefs());
        expectedModel.setLaiNurse(new LaiNurse());

        // A command that removes patients selects nothing
        assertCommandSuccess(new ClearCommand(), model,
                new CommandResult(ClearCommand.MESSAGE_SUCCESS).withSelectionCleared(), expectedModel);
    }

}
