package seedu.address.logic.commands;

import seedu.address.model.patient.PatientId;

/**
 * Marks one of a patient's appointments as done, using its patient-local number.
 */
public class MarkAppointmentCommand extends AppointmentStatusCommand {

    public static final String COMMAND_WORD = "markappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks an appointment as done. A done appointment "
            + "does not clash, and is not the patient's next appointment.\n"
            + "Parameters: ID NUMBER\n"
            + "Example: " + COMMAND_WORD + " S1234567A 2";

    public static final String MESSAGE_MARK_APPOINTMENT_SUCCESS =
            "Marked appointment %1$d of %2$s (%3$s) as done: %4$s";
    public static final String MESSAGE_ALREADY_DONE =
            "Appointment #%1$d of patient %2$s is already done.";

    /**
     * Creates a command to mark the appointment with {@code number} of the patient with {@code targetId}.
     */
    public MarkAppointmentCommand(PatientId targetId, long number) {
        super(targetId, number, true, COMMAND_WORD, MESSAGE_MARK_APPOINTMENT_SUCCESS, MESSAGE_ALREADY_DONE);
    }
}
