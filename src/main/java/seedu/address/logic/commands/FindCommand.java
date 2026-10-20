package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MEDICAL_HISTORY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.patient.PatientContainsSubstringsPredicate;

/**
 * Finds and lists patients whose fields contain any of the supplied substrings.
 * Matching ignores case and repeated whitespace.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds patients matching any supplied field substring "
            + "(case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: [" + PREFIX_ID + "ID] [" + PREFIX_NAME + "NAME] [" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_PHONE + "PHONE] [" + PREFIX_MEDICAL_HISTORY + "HISTORY] [" + PREFIX_NOTES + "NOTES]\n"
            + "Supply at least one non-blank value. Double quotes around values are optional.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "\"John\" " + PREFIX_ADDRESS + "\"Marina Bay\"";

    private final PatientContainsSubstringsPredicate predicate;

    /**
     * Creates a find command with the supplied patient-field predicate.
     */
    public FindCommand(PatientContainsSubstringsPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPatientList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, model.getFilteredPatientList().size()));
    }

    @Override
    public Optional<CommandResult.View> getView() {
        return Optional.of(CommandResult.View.PATIENTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
