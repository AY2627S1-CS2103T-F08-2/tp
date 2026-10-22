package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;

/**
 * Adds an appointment to the schedule of the patient with the given ID.
 */
public class AddAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "addappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an appointment to the patient with the "
            + "given ID, at the given date and time.\n"
            + "Parameters: ID "
            + PREFIX_APPOINTMENT + "DATE_OR_DATETIME "
            + "[" + PREFIX_DETAILS + "DETAILS]\n"
            + "Example: " + COMMAND_WORD + " S1234567A "
            + PREFIX_APPOINTMENT + "2026-12-01 10:00 "
            + PREFIX_DETAILS + "Bring insulin pen";

    public static final String MESSAGE_ADD_APPOINTMENT_SUCCESS = "Added appointment of %1$s (%2$s): %3$s";
    public static final String MESSAGE_APPOINTMENT_TAKEN = "Patient %1$s already has an appointment on %2$s.";

    private static final Logger logger = LogsCenter.getLogger(AddAppointmentCommand.class);

    private final PatientId targetId;
    private final Appointment toAdd;

    /**
     * Creates an AddAppointmentCommand to add {@code appointment} to the schedule of the patient with
     * {@code targetId}.
     *
     * @param targetId The ID of the patient whose schedule gains the appointment.
     * @param appointment The appointment to add, with its details, if any.
     */
    public AddAppointmentCommand(PatientId targetId, Appointment appointment) {
        CollectionUtil.requireAllNonNull(targetId, appointment);
        this.targetId = targetId;
        this.toAdd = appointment;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Patient patient = model.findPatientById(targetId)
                .orElseThrow(() -> {
                    logger.fine("Rejected addappt: no patient has the given ID.");
                    return new CommandException(Messages.MESSAGE_INVALID_PATIENT_ID);
                });

        AppointmentSchedule schedule = patient.getSchedule();
        if (schedule.contains(toAdd)) {
            logger.fine("Rejected addappt: the patient already has an appointment at the given time.");
            throw new CommandException(String.format(MESSAGE_APPOINTMENT_TAKEN, targetId, toAdd));
        }

        AppointmentSchedule editedSchedule = schedule.add(toAdd);
        Patient editedPatient = new Patient(patient.getId(), patient.getName(), patient.getPhone(),
                patient.getAddress(), patient.getMedicalHistory(), patient.getNotes(), editedSchedule);
        model.setPatient(patient, editedPatient);
        logger.fine("Added an appointment of patient " + targetId + ".");

        PatientAppointment created = new PatientAppointment(editedPatient, toAdd);
        String message = String.format(MESSAGE_ADD_APPOINTMENT_SUCCESS, editedPatient.getName(), targetId,
                format(toAdd));
        return new CommandResult(message).withNavigation(Navigation.toAppointment(created));
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
        if (!(other instanceof AddAppointmentCommand otherCommand)) {
            return false;
        }

        return targetId.equals(otherCommand.targetId)
                && toAdd.equals(otherCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("toAdd", toAdd)
                .toString();
    }
}
