package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ARCHIVED;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PATIENTS;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Shows all patients, and selects the patient with the given ID to show their details.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows all patients, and the details of the patient with the given ID, archived or not. "
            + "Without an ID, shows the patient list: the patients who are not archived, or, with "
            + PREFIX_ALL.toString().trim() + ", all patients, archived ones last, or, with "
            + PREFIX_ARCHIVED.toString().trim() + ", only archived ones.\n"
            + "Parameters: ID, or [" + PREFIX_ALL.toString().trim() + "] or [" + PREFIX_ARCHIVED.toString().trim()
            + "]\n"
            + "Example: " + COMMAND_WORD + " S1234567A, or " + COMMAND_WORD + " " + PREFIX_ALL.toString().trim();

    public static final String MESSAGE_VIEW_PATIENT_SUCCESS = "Showing patient: %1$s (%2$s)";

    private final PatientId targetId;

    /**
     * Creates a ViewCommand to show the patient with the specified {@code targetId}.
     */
    public ViewCommand(PatientId targetId) {
        requireNonNull(targetId);
        this.targetId = targetId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Patient> listAllPatients = model.getLaiNurse().getPatientList();

        Patient patientToView = listAllPatients.stream().filter(patient -> patient.getId().equals(targetId))
                .findFirst().orElseThrow(() -> new CommandException(
                        String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, targetId)));
        // All patients, so that the patient's row is in the list to select, also if they are archived
        model.updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
        model.showInPatientList(patientToView);
        return new CommandResult(String.format(MESSAGE_VIEW_PATIENT_SUCCESS, patientToView.getName(),
                patientToView.getId())).withNavigation(Navigation.toPatient(patientToView.getId()));
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
        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return targetId.equals(otherViewCommand.targetId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .toString();
    }
}
