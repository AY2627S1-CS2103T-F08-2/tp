package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AppointmentLookup.Target;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientId;

/**
 * Deletes one of the appointments of the patient with the given ID.
 */
public class DeleteAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "deleteappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes an appointment of the patient with the "
            + "given ID, using its number.\n"
            + "Parameters: ID NUMBER\n"
            + "Example: " + COMMAND_WORD + " S1234567A 2";

    public static final String MESSAGE_DELETE_APPOINTMENT_SUCCESS = "Deleted appointment %1$d of %2$s (%3$s): %4$s";

    private static final Logger logger = LogsCenter.getLogger(DeleteAppointmentCommand.class);

    private final PatientId targetId;
    private final Target targetAppointment;

    /**
     * Creates a command selecting an appointment by its patient-local number.
     *
     * @param targetId The ID of the patient whose appointment is deleted.
     * @param number The patient-local appointment number to delete.
     */
    public DeleteAppointmentCommand(PatientId targetId, int number) {
        requireNonNull(targetId);
        this.targetId = targetId;
        this.targetAppointment = new Target(number);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Patient patient = model.findPatientById(targetId)
                .orElseThrow(() -> {
                    logger.fine("Rejected deleteappt: no patient has the given ID.");
                    return new CommandException(String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, targetId));
                });

        Appointment foundAppointment =
                AppointmentLookup.find(targetId, patient.getSchedule(), targetAppointment, COMMAND_WORD);

        AppointmentSchedule editedSchedule = patient.getSchedule().remove(foundAppointment);
        assert editedSchedule.findByNumber(targetAppointment.number()).isEmpty();
        Patient editedPatient = new Patient(patient.getId(), patient.getName(), patient.getPhone(),
                patient.getAddress(), patient.getMedicalHistory(), patient.getNotes(), editedSchedule);
        model.setPatient(patient, editedPatient);
        logger.fine("Deleted appointment #" + foundAppointment.getNumber() + " of patient " + targetId + ".");

        String message = String.format(MESSAGE_DELETE_APPOINTMENT_SUCCESS, foundAppointment.getNumber(),
                editedPatient.getName(), targetId, Messages.format(foundAppointment));
        return new CommandResult(message).withSelectionCleared();
    }

    @Override
    public Optional<View> getView() {
        return Optional.of(View.APPOINTMENTS);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteAppointmentCommand otherCommand)) {
            return false;
        }

        return targetId.equals(otherCommand.targetId)
                && targetAppointment.equals(otherCommand.targetAppointment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("targetAppointment", targetAppointment)
                .toString();
    }
}
