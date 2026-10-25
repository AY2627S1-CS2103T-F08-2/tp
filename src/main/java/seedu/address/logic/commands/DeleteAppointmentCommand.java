package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CollectionUtil;
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
            + "given ID, using its number or an unambiguous date/time.\n"
            + "Parameters: ID (NUMBER | " + PREFIX_APPOINTMENT + "DATE_OR_DATETIME)\n"
            + "Example: " + COMMAND_WORD + " S1234567A 2";

    public static final String MESSAGE_DELETE_APPOINTMENT_SUCCESS = "Deleted appointment %1$d of %2$s (%3$s): %4$s";

    private static final Logger logger = LogsCenter.getLogger(DeleteAppointmentCommand.class);

    private final PatientId targetId;
    private final Target targetAppointment;

    /**
     * Creates a DeleteAppointmentCommand to delete the appointment of the patient with {@code targetId} at the
     * date and time of {@code targetAppointment}.
     *
     * @param targetId The ID of the patient whose appointment is deleted.
     * @param targetAppointment The date and time of the appointment to delete. Its details are ignored.
     */
    public DeleteAppointmentCommand(PatientId targetId, Appointment targetAppointment) {
        this(targetId, new Target(targetAppointment));
    }

    /**
     * Creates a command selecting an appointment by its patient-local number.
     */
    public DeleteAppointmentCommand(PatientId targetId, int number) {
        this(targetId, new Target(number));
    }

    /**
     * Creates a command with exactly one appointment selector.
     */
    private DeleteAppointmentCommand(PatientId targetId, Target targetAppointment) {
        CollectionUtil.requireAllNonNull(targetId, targetAppointment);
        this.targetId = targetId;
        this.targetAppointment = targetAppointment;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Patient patient = model.findPatientById(targetId)
                .orElseThrow(() -> {
                    logger.fine("Rejected deleteappt: no patient has the given ID.");
                    return new CommandException(Messages.MESSAGE_INVALID_PATIENT_ID);
                });

        Appointment foundAppointment =
                AppointmentLookup.find(targetId, patient.getSchedule(), targetAppointment, COMMAND_WORD);

        AppointmentSchedule editedSchedule = patient.getSchedule().remove(foundAppointment);
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
