package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAILS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TO;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AppointmentLookup.Target;
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
            + "given ID, using its number. Keeps its number and completion status.\n"
            + "Parameters: ID NUMBER "
            + "[" + PREFIX_TO + "NEW_DATE_OR_DATETIME] "
            + "[" + PREFIX_DETAILS + "DETAILS]\n"
            + "Example: " + COMMAND_WORD + " S1234567A 2 "
            + PREFIX_TO + "2026-10-06 10:00 "
            + PREFIX_DETAILS + "Bring insulin pen";

    public static final String MESSAGE_EDIT_APPOINTMENT_SUCCESS = "Edited appointment %1$d of %2$s (%3$s): %4$s";
    public static final String MESSAGE_MOVED_FROM = " (moved from %1$s)";
    public static final String MESSAGE_NOT_EDITED = "At least one of " + PREFIX_TO.toString().trim() + " and "
            + PREFIX_DETAILS.toString().trim() + " must be provided.";

    private static final Logger logger = LogsCenter.getLogger(EditAppointmentCommand.class);

    private final PatientId targetId;
    private final Target targetAppointment;
    private final Optional<Appointment> newDateTime;
    private final Optional<AppointmentDetails> newDetails;

    /**
     * Creates a command selecting an appointment by its patient-local number.
     *
     * @param targetId The ID of the patient whose appointment to edit.
     * @param number The patient-local appointment number to edit.
     * @param newDateTime The date and time to move the appointment to, if it moves. Its details are ignored.
     * @param newDetails The new details of the appointment, if they change.
     */
    public EditAppointmentCommand(PatientId targetId, int number, Optional<Appointment> newDateTime,
            Optional<AppointmentDetails> newDetails) {
        CollectionUtil.requireAllNonNull(targetId, newDateTime, newDetails);
        if (newDateTime.isEmpty() && newDetails.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_NOT_EDITED);
        }
        this.targetId = targetId;
        this.targetAppointment = new Target(number);
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
        Appointment oldAppointment =
                AppointmentLookup.find(targetId, schedule, targetAppointment, COMMAND_WORD);
        Appointment editedAppointment = createEditedAppointment(oldAppointment);

        boolean isMoved = !editedAppointment.hasSameDateTime(oldAppointment);
        assert editedAppointment.getNumber() == oldAppointment.getNumber();

        AppointmentSchedule editedSchedule = schedule.remove(oldAppointment).add(editedAppointment);
        assert editedSchedule.findByNumber(targetAppointment.number()).orElseThrow().equals(editedAppointment);
        Patient editedPatient = new Patient(patient.getId(), patient.getName(), patient.getPhone(),
                patient.getAddress(), patient.getMedicalHistory(), patient.getNotes(), editedSchedule);
        model.setPatient(patient, editedPatient);

        String message = String.format(MESSAGE_EDIT_APPOINTMENT_SUCCESS, editedAppointment.getNumber(),
                editedPatient.getName(), targetId, Messages.format(editedAppointment));
        if (isMoved) {
            message += String.format(MESSAGE_MOVED_FROM, oldAppointment);
        }
        PatientAppointment editedEntry = new PatientAppointment(editedPatient, editedAppointment);
        String warning = Messages.formatClashWarning(model.getAppointmentList(), editedEntry);
        message += warning;
        logger.fine("Edited appointment #" + editedAppointment.getNumber() + " of patient " + targetId
                + (warning.isEmpty() ? "." : " with a clash warning."));
        return new CommandResult(message).withNavigation(Navigation.toAppointment(editedEntry));
    }

    /**
     * Returns {@code oldAppointment} moved to the new date and time, if any, with the new details, if any.
     * What is not changed is kept from {@code oldAppointment}.
     */
    private Appointment createEditedAppointment(Appointment oldAppointment) {
        assert oldAppointment.isNumbered();
        String dateTime = newDateTime.map(Appointment::toString).orElse(oldAppointment.toString());
        Optional<AppointmentDetails> details = newDetails.or(oldAppointment::getDetails);
        return new Appointment(oldAppointment.getNumber(), dateTime, details).withDone(oldAppointment.isDone());
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
