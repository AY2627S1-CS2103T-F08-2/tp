package seedu.address.logic.commands;

import seedu.address.model.patient.PatientId;

/**
 * Unmarks one of a patient's appointments as undone, using its patient-local number.
 */
public class UnmarkAppointmentCommand extends AppointmentStatusCommand {

    public static final String COMMAND_WORD = "unmarkappt";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Unmarks an appointment as undone.\n"
            + "Parameters: ID NUMBER\n"
            + "Example: " + COMMAND_WORD + " S1234567A 2";

    public static final String MESSAGE_UNMARK_APPOINTMENT_SUCCESS =
            "Unmarked appointment %1$d of %2$s (%3$s) as undone: %4$s";
    public static final String MESSAGE_ALREADY_UNDONE =
            "Appointment #%1$d of patient %2$s is already undone.";

    /**
     * Creates a command to unmark the appointment with {@code number} of the patient with {@code targetId}.
     */
    public UnmarkAppointmentCommand(PatientId targetId, long number) {
        super(targetId, number, false, COMMAND_WORD, MESSAGE_UNMARK_APPOINTMENT_SUCCESS, MESSAGE_ALREADY_UNDONE);
    }
}
