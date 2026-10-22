package seedu.address.logic.commands;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentSchedule;
import seedu.address.model.patient.PatientId;

/**
 * Looks up the appointments of a patient for the appointment commands.
 */
public final class AppointmentLookup {

    private static final Logger logger = LogsCenter.getLogger(AppointmentLookup.class);

    private AppointmentLookup() {}

    /**
     * Returns the appointment of the patient with {@code patientId} at the date and time of {@code target}.
     * The details of {@code target} are ignored.
     *
     * @throws CommandException if there is no such appointment. The message says so and lists the patient's
     *         appointments on that day, if any, so that the nurse can retype the one they meant.
     */
    public static Appointment find(PatientId patientId, AppointmentSchedule schedule, Appointment target,
            String commandWord) throws CommandException {
        Optional<Appointment> found = schedule.find(target);
        if (found.isPresent()) {
            return found.get();
        }
        logger.fine("Rejected " + commandWord + ": the patient has no appointment at the given time.");
        String message = String.format(Messages.MESSAGE_APPOINTMENT_NOT_FOUND, patientId, target);
        List<Appointment> sameDay = schedule.getOnDate(target.getDate());
        if (!sameDay.isEmpty()) {
            message += String.format(Messages.MESSAGE_APPOINTMENTS_ON_DATE,
                    sameDay.stream().map(Appointment::toString).collect(Collectors.joining(", ")));
        }
        throw new CommandException(message);
    }
}
