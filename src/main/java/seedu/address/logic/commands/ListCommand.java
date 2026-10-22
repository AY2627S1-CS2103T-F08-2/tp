package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PATIENTS;

import java.util.Optional;

import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.model.Model;

/**
 * Lists all patients in LaiNurse to the user, and selects the first.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_SUCCESS = "Listed all patients.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows a list of all patients in LaiNurse, sorted by name (the patient ID breaks ties), "
            + "and the details of the first.\n"
            + "Example: " + COMMAND_WORD;


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
        return new CommandResult(MESSAGE_SUCCESS).withNavigation(Navigation.toView(View.PATIENTS));
    }

    @Override
    public Optional<CommandResult.View> getView() {
        return Optional.of(CommandResult.View.PATIENTS);
    }
}
