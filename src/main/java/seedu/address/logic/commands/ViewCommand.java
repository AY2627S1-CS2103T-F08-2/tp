package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Shows the details of the patient with the given ID.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows the details of the patient with the given ID.\n"
            + "Parameters: ID\n"
            + "Example: " + COMMAND_WORD + " S1234567A";

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
                .findFirst().orElseThrow(() -> new CommandException(Messages.MESSAGE_INVALID_PATIENT_ID));
        return new CommandResult(String.format(MESSAGE_VIEW_PATIENT_SUCCESS, patientToView.getName(),
                patientToView.getId()), patientToView.getId());
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
