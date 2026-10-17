package seedu.address.logic.commands;

import java.util.Optional;

import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

    /**
     * Returns the view that the main window switches to after this command succeeds, or
     * {@code Optional#empty()} to keep the current view. Commands whose words end in "appt" show
     * {@link View#APPOINTMENTS}, and the commands that list or change patients show {@link View#PATIENTS}.
     */
    public Optional<View> getView() {
        return Optional.empty();
    }

}
