package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPOINTMENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult.Navigation;
import seedu.address.logic.commands.CommandResult.View;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;
import seedu.address.model.patient.PatientId;

/**
 * Moves one of a patient's appointments to another date or time, or changes its details, or both.
 */
public class EditAppointmentCommand extends Command {

    public static final String COMMAND_WORD = "editappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the appointment of the patient with the "
            + "given ID at the given date and time. Moving it keeps its details, unless new details are given.\n"
            + "Parameters: ID "
            + PREFIX_APPOINTMENT + "DATE_OR_DATETIME "
            + "[" + PREFIX_TO + "NEW_DATE_OR_DATETIME] "
            + "[" + PREFIX_DETAILS + "DETAILS]\n"
            + "Example: " + COMMAND_WORD + " S1234567A "
            + PREFIX_APPOINTMENT + "2026-10-05 09:30 "
            + PREFIX_TO + "2026-10-06 10:00 "
            + PREFIX_DETAILS + "Bring insulin pen";

    public static final String MESSAGE_EDIT_APPOINTMENT_SUCCESS = "Edited appointment of %1$s (%2$s): %3$s";
    public static final String MESSAGE_MOVED_FROM = " (moved from %1$s)";
    public static final String MESSAGE_NOT_EDITED = "At least one of " + PREFIX_TO.toString().trim() + " and "
            + PREFIX_DETAILS.toString().trim() + " must be provided.";
    public static final String MESSAGE_APPOINTMENT_NOT_FOUND = "Patient %1$s has no appointment on %2$s.";
    public static final String MESSAGE_APPOINTMENTS_ON_DATE = " Their appointments on that day: %1$s.";
    public static final String MESSAGE_APPOINTMENT_TAKEN = "Patient %1$s already has an appointment on %2$s.";

    private static final Logger logger = LogsCenter.getLogger(EditAppointmentCommand.class);

    private final PatientId targetId;
    private final Appointment targetAppointment;
    private final Optional<Appointment> newDateTime;
    private final Optional<AppointmentDetails> newDetails;

    /**
     * Creates an EditAppointmentCommand to edit the appointment of the patient with {@code targetId} at the date and
     * time of {@code targetAppointment}.
     *
     * @param targetId The ID of the patient whose appointment to edit.
     * @param targetAppointment The date and time of the appointment to edit. Its details are ignored.
     * @param newDateTime The date and time to move the appointment to, if it moves. Its details are ignored.
     * @param newDetails The new details of the appointment, if they change.
     */
    public EditAppointmentCommand(PatientId targetId, Appointment targetAppointment, Optional<Appointment> newDateTime,
            Optional<AppointmentDetails> newDetails) {
        CollectionUtil.requireAllNonNull(targetId, targetAppointment, newDateTime, newDetails);
        if (newDateTime.isEmpty() && newDetails.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_NOT_EDITED);
        }
        this.targetId = targetId;
        this.targetAppointment = targetAppointment;
        this.newDateTime = newDateTime;
        this.newDetails = newDetails;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Optional<Patient> foundPatient = model.findPatientById(targetId);
        if (foundPatient.isEmpty()) {
            logger.fine("Rejected editappt: no patient has the given ID.");
            throw new CommandException(Messages.MESSAGE_INVALID_PATIENT_ID);
        }
        Patient patient = foundPatient.get();
        AppointmentSchedule schedule = patient.getSchedule();
        Appointment oldAppointment = findTargetAppointment(schedule);
        Appointment editedAppointment = createEditedAppointment(oldAppointment);

        boolean isMoved = !editedAppointment.isSameAppointment(oldAppointment);
        if (isMoved && schedule.contains(editedAppointment)) {
            logger.fine("Rejected editappt: the patient already has an appointment at the new time.");
            throw new CommandException(String.format(MESSAGE_APPOINTMENT_TAKEN, targetId, editedAppointment));
        }

        AppointmentSchedule editedSchedule = schedule.remove(oldAppointment).add(editedAppointment);
        Patient editedPatient = new Patient(patient.getId(), patient.getName(), patient.getPhone(),
                patient.getAddress(), patient.getMedicalHistory(), patient.getNotes(), editedSchedule);
        model.setPatient(patient, editedPatient);
        logger.fine("Edited an appointment of patient " + targetId + ".");

        String message = String.format(MESSAGE_EDIT_APPOINTMENT_SUCCESS, editedPatient.getName(), targetId,
                format(editedAppointment));
        if (isMoved) {
            message += String.format(MESSAGE_MOVED_FROM, oldAppointment);
        }
        return new CommandResult(message)
                .withNavigation(Navigation.toAppointment(new PatientAppointment(editedPatient, editedAppointment)));
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
        logger.fine("Rejected editappt: the patient has no appointment at the given time.");
        String message = String.format(MESSAGE_APPOINTMENT_NOT_FOUND, targetId, targetAppointment);
        List<Appointment> sameDay = schedule.getOnDate(targetAppointment.getDate());
        if (!sameDay.isEmpty()) {
            message += String.format(MESSAGE_APPOINTMENTS_ON_DATE,
                    sameDay.stream().map(Appointment::toString).collect(Collectors.joining(", ")));
        }
        throw new CommandException(message);
    }

    /**
     * Returns {@code oldAppointment} moved to the new date and time, if any, with the new details, if any.
     * What is not changed is kept from {@code oldAppointment}.
     */
    private Appointment createEditedAppointment(Appointment oldAppointment) {
        assert oldAppointment.isSameAppointment(targetAppointment);
        String dateTime = newDateTime.map(Appointment::toString).orElse(oldAppointment.toString());
        Optional<AppointmentDetails> details = newDetails.or(oldAppointment::getDetails);
        return new Appointment(dateTime, details);
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
        if (!(other instanceof EditAppointmentCommand otherCommand)) {
            return false;
        }

        return targetId.equals(otherCommand.targetId)
                && targetAppointment.equals(otherCommand.targetAppointment)
                && newDateTime.equals(otherCommand.newDateTime)
                && newDetails.equals(otherCommand.newDetails);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("targetAppointment", targetAppointment)
                .add("newDateTime", newDateTime)
                .add("newDetails", newDetails)
                .toString();
    }
}
