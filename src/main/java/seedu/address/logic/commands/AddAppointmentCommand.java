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

    public static final String MESSAGE_ADD_APPOINTMENT_SUCCESS = "Added appointment %1$d of %2$s (%3$s): %4$s";

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
        int number;
        try {
            number = schedule.getNextNumber();
        } catch (IllegalArgumentException e) {
            logger.fine("Rejected addappt: no new appointment number is available.");
            throw new CommandException(e.getMessage(), e);
        }

        Appointment numbered = new Appointment(number, toAdd.value, toAdd.getDetails());
        AppointmentSchedule editedSchedule = schedule.add(numbered);
        Patient editedPatient = new Patient(patient.getId(), patient.getName(), patient.getPhone(),
                patient.getAddress(), patient.getMedicalHistory(), patient.getNotes(), editedSchedule);
        model.setPatient(patient, editedPatient);

        assert editedSchedule.findByNumber(numbered.getNumber()).orElseThrow().equals(numbered);
        PatientAppointment created = new PatientAppointment(editedPatient, numbered);
        String message = String.format(MESSAGE_ADD_APPOINTMENT_SUCCESS, numbered.getNumber(),
                editedPatient.getName(), targetId, Messages.format(numbered));
        String warning = Messages.formatClashWarning(model.getAppointmentList(), created);
        message += warning;
        logger.fine("Added appointment #" + number + " of patient " + targetId
                + (warning.isEmpty() ? "." : " with a clash warning."));
        return new CommandResult(message).withNavigation(Navigation.toAppointment(created));
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
