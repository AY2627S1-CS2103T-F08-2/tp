package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
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
            + "given ID, at the given date and time.\n"
            + "Parameters: ID " + PREFIX_APPOINTMENT + "DATE_OR_DATETIME\n"
            + "Example: " + COMMAND_WORD + " S1234567A " + PREFIX_APPOINTMENT + "2026-10-05 09:30";

    public static final String MESSAGE_DELETE_APPOINTMENT_SUCCESS = "Deleted appointment of %1$s (%2$s): %3$s";
    public static final String MESSAGE_APPOINTMENT_NOT_FOUND = "Patient %1$s has no appointment on %2$s.";
    public static final String MESSAGE_APPOINTMENTS_ON_DATE = " Their appointments on that day: %1$s.";

    private static final Logger logger = LogsCenter.getLogger(DeleteAppointmentCommand.class);

    private final PatientId targetId;
    private final Appointment targetAppointment;

    /**
     * Creates a DeleteAppointmentCommand to delete the appointment of the patient with {@code targetId} at the
     * date and time of {@code targetAppointment}.
     *
     * @param targetId The ID of the patient whose appointment is deleted.
     * @param targetAppointment The date and time of the appointment to delete. Its details are ignored.
     */
    public DeleteAppointmentCommand(PatientId targetId, Appointment targetAppointment) {
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

        Appointment foundAppointment = findTargetAppointment(patient.getSchedule());

        AppointmentSchedule editedSchedule = patient.getSchedule().remove(foundAppointment);
        Patient editedPatient = new Patient(patient.getId(), patient.getName(), patient.getPhone(),
                patient.getAddress(), patient.getMedicalHistory(), patient.getNotes(), editedSchedule);
        model.setPatient(patient, editedPatient);
        logger.fine("Deleted an appointment of patient " + targetId + ".");

        String message = String.format(MESSAGE_DELETE_APPOINTMENT_SUCCESS, editedPatient.getName(), targetId,
                format(foundAppointment));
        return new CommandResult(message);
    }

    /**
     * Returns the patient's appointment at the date and time of {@code targetAppointment}, with its details.
     *
     * @throws CommandException if there is none. The message lists the patient's appointments on that day, if any,
     *         so that the nurse can retype the one they meant.
     */
    private Appointment findTargetAppointment(AppointmentSchedule schedule) throws CommandException {
        Optional<Appointment> found = schedule.find(targetAppointment);
        if (found.isPresent()) {
            return found.get();
        }
        logger.fine("Rejected deleteappt: the patient has no appointment at the given time.");
        String message = String.format(MESSAGE_APPOINTMENT_NOT_FOUND, targetId, targetAppointment);
        List<Appointment> sameDay = schedule.getOnDate(targetAppointment.getDate());
        if (!sameDay.isEmpty()) {
            message += String.format(MESSAGE_APPOINTMENTS_ON_DATE,
                    sameDay.stream().map(Appointment::toString).collect(Collectors.joining(", ")));
        }
        throw new CommandException(message);
    }

    /**
     * Returns {@code appointment} as shown in the result message: its date and time, and its details, if any.
     */
    private static String format(Appointment appointment) {
        return appointment + appointment.getDetails().map(details -> "; Details: " + details).orElse("");
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
