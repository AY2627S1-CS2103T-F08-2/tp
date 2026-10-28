package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.logging.Logger;

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
     * A positive patient-local appointment number, as typed. It can be larger than any appointment number, which
     * no patient has.
     */
    public record Target(long number) {
        /**
         * Validates that the appointment number is positive.
         */
        public Target {
            AppUtil.checkArgument(number > 0, Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        }

        @Override
        public String toString() {
            return "#" + number;
        }
    }

    /**
     * Resolves a patient-local appointment number without changing the schedule.
     *
     * @throws CommandException if the patient has no appointment with the given number.
     */
    public static Appointment find(PatientId patientId, AppointmentSchedule schedule, Target target,
            String commandWord) throws CommandException {
        requireAllNonNull(patientId, schedule, target, commandWord);
        Optional<Appointment> match = target.number() <= Integer.MAX_VALUE
                ? schedule.findByNumber((int) target.number())
                : Optional.empty();
        Appointment found = match.orElseThrow(() -> {
            logger.fine("Rejected " + commandWord + ": no appointment has the given number.");
            return new CommandException(String.format(Messages.MESSAGE_APPOINTMENT_NUMBER_NOT_FOUND,
                    patientId, target.number()));
        });
        assert found.getNumber() == target.number();
        return found;
    }
}
