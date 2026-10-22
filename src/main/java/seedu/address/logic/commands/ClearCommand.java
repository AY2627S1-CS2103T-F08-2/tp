package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.LaiNurse;
import seedu.address.model.Model;

/**
 * Clears LaiNurse.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "LaiNurse has been cleared!";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Clears all entries from LaiNurse.\n"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setLaiNurse(new LaiNurse());
        return new CommandResult(MESSAGE_SUCCESS).withSelectionCleared();
    }
}
