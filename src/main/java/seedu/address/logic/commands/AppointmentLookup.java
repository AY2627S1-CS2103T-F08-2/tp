package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.AppUtil;
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
     * Exactly one appointment selector: a patient-local number or a legacy date/time.
     */
    public record Target(Integer number, Appointment dateTime) {
        /**
         * Validates that exactly one selector is present and a supplied number is positive.
         */
        public Target {
            AppUtil.checkArgument((number == null) != (dateTime == null), "Exactly one selector is required.");
            AppUtil.checkArgument(number == null || number > 0, Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        }

        public Target(int number) {
            this(number, null);
        }

        public Target(Appointment dateTime) {
            this(null, requireNonNull(dateTime));
        }

        @Override
        public String toString() {
            return number == null ? dateTime.toString() : "#" + number;
        }
    }

    /**
     * Returns the appointment of the patient with {@code patientId} at the date and time of {@code target}.
     * The details of {@code target} are ignored.
     *
     * @throws CommandException if there is no such appointment. The message says so and lists the patient's
     *         appointments on that day, if any, so that the nurse can retype the one they meant.
     */
    public static Appointment find(PatientId patientId, AppointmentSchedule schedule, Appointment target,
            String commandWord) throws CommandException {
        return find(patientId, schedule, new Target(target), commandWord);
    }

    /**
     * Resolves a number or an unambiguous exact timestamp, without changing the schedule.
     *
     * @throws CommandException if the selector matches no appointment, or more than one.
     */
    public static Appointment find(PatientId patientId, AppointmentSchedule schedule, Target target,
            String commandWord) throws CommandException {
        if (target.number() != null) {
            return schedule.findByNumber(target.number()).orElseThrow(() -> {
                logger.fine("Rejected " + commandWord + ": no appointment has the given number.");
                return new CommandException(String.format(Messages.MESSAGE_APPOINTMENT_NUMBER_NOT_FOUND,
                        patientId, target.number()));
            });
        }
        Appointment dateTime = target.dateTime();
        List<Appointment> matches = schedule.findAtDateTime(dateTime);
        if (matches.size() == 1) {
            return matches.getFirst();
        }
        if (matches.size() > 1) {
            logger.fine("Rejected " + commandWord + ": the appointment timestamp is ambiguous.");
            throw new CommandException(String.format(Messages.MESSAGE_APPOINTMENT_AMBIGUOUS, patientId, dateTime,
                    matches.stream().map(Messages::formatNumbered).collect(Collectors.joining(", "))));
        }
        logger.fine("Rejected " + commandWord + ": the patient has no appointment at the given time.");
        String message = String.format(Messages.MESSAGE_APPOINTMENT_NOT_FOUND, patientId, target);
        List<Appointment> sameDay = schedule.getOnDate(dateTime.getDate());
        if (!sameDay.isEmpty()) {
            message += String.format(Messages.MESSAGE_APPOINTMENTS_ON_DATE,
                    sameDay.stream().map(Messages::formatNumbered).collect(Collectors.joining(", ")));
        }
        throw new CommandException(message);
    }
}
