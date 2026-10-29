package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AppointmentLookup.Target;
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
 * Shares the validation and immutable update of an appointment's completion status.
 */
abstract class AppointmentStatusCommand extends Command {

    private static final Logger logger = LogsCenter.getLogger(AppointmentStatusCommand.class);

    private final PatientId targetId;
    private final Target targetAppointment;
    private final boolean done;
    private final String commandWord;
    private final String successMessage;
    private final String alreadyInStatusMessage;

    AppointmentStatusCommand(PatientId targetId, long number, boolean done, String commandWord,
            String successMessage, String alreadyInStatusMessage) {
        requireAllNonNull(targetId, commandWord, successMessage, alreadyInStatusMessage);
        this.targetId = targetId;
        this.targetAppointment = new Target(number);
        this.done = done;
        this.commandWord = commandWord;
        this.successMessage = successMessage;
        this.alreadyInStatusMessage = alreadyInStatusMessage;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Patient patient = model.findPatientById(targetId).orElseThrow(() -> {
            logger.fine("Rejected " + commandWord + ": no patient has the given ID.");
            return new CommandException(String.format(Messages.MESSAGE_PATIENT_NOT_FOUND, targetId));
        });
        AppointmentSchedule schedule = patient.getSchedule();
        Appointment appointment = AppointmentLookup.find(targetId, schedule, targetAppointment, commandWord);
        if (appointment.isDone() == done) {
            logger.fine("Rejected " + commandWord + ": the appointment already has the requested status.");
            throw new CommandException(String.format(alreadyInStatusMessage, appointment.getNumber(), targetId));
        }

        Appointment updatedAppointment = appointment.withDone(done);
        assert updatedAppointment.isSameAppointment(appointment);
        assert updatedAppointment.hasSameDateTime(appointment)
                && updatedAppointment.getDetails().equals(appointment.getDetails());

        AppointmentSchedule updatedSchedule = schedule.replace(appointment, updatedAppointment);
        Patient updatedPatient = patient.withSchedule(updatedSchedule);
        model.setPatient(patient, updatedPatient);

        PatientAppointment updatedEntry = new PatientAppointment(updatedPatient, updatedAppointment);
        String message = String.format(successMessage, updatedAppointment.getNumber(), updatedPatient.getName(),
                targetId, Messages.format(updatedAppointment));
        // A marked appointment clashes only if the settings say so (#216), and then a mark warns as well
        String warning = Messages.formatClashWarning(model, updatedEntry);
        logger.fine("Updated appointment #" + updatedAppointment.getNumber() + " of patient " + targetId
                + " using " + commandWord + (warning.isEmpty() ? "." : " with a clash warning."));
        return new CommandResult(message + warning).withNavigation(Navigation.toAppointment(updatedEntry));
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
        if (!(other instanceof AppointmentStatusCommand otherCommand)) {
            return false;
        }
        return done == otherCommand.done && targetId.equals(otherCommand.targetId)
                && targetAppointment.equals(otherCommand.targetAppointment);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetId", targetId)
                .add("targetAppointment", targetAppointment)
                .add("done", done)
                .toString();
    }
}
