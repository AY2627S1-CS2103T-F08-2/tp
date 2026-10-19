package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.patient.Patient;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type 'help' to view all available commands.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PATIENT_ID = "The patient ID provided is invalid.";
    public static final String MESSAGE_PATIENTS_LISTED_OVERVIEW = "%1$d patient(s) listed!";
    public static final String MESSAGE_VIEW_APPOINTMENT_LISTED_OVERVIEW = "%1$d patient(s) with appointments on %2$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_RANGE_LISTED_OVERVIEW =
            "%1$d patient(s) with appointments from %2$s to %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_FROM_LISTED_OVERVIEW =
            "%1$d patient(s) with appointments on or after %2$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_LISTED_OVERVIEW =
            "%1$d appointment(s) of %2$s on %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_RANGE_LISTED_OVERVIEW =
            "%1$d appointment(s) of %2$s from %3$s to %4$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_FROM_LISTED_OVERVIEW =
            "%1$d appointment(s) of %2$s on or after %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_TO_BEFORE_TODAY =
            "The --to date %1$s is before today (%2$s). To see past appointments, also give --from.";
    public static final String MESSAGE_VIEW_APPOINTMENT_FROM_AFTER_TO =
            "The --from date must not be after the --to date.";
    public static final String MESSAGE_DUPLICATE_FIELDS =
            "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code patient} for display to the user.
     */
    public static String format(Patient patient) {
        final StringBuilder builder = new StringBuilder();
        builder.append(patient.getName())
                .append("; ID: ")
                .append(patient.getId())
                .append("; Phone: ")
                .append(patient.getPhone())
                .append("; Address: ")
                .append(patient.getAddress());
        patient.getMedicalHistory().ifPresent(history -> builder.append("; Medical History: ").append(history));
        if (!patient.getSchedule().isEmpty()) {
            builder.append("; Appointments: ").append(patient.getSchedule());
        }
        return builder.toString();
    }

}
