package seedu.address.logic;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentClashes;
import seedu.address.model.patient.Patient;
import seedu.address.model.patient.PatientAppointment;

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
    public static final String MESSAGE_VIEW_APPOINTMENT_WEEK_LISTED_OVERVIEW =
            "%1$d patient(s) with appointments this week, from %2$s to %3$s";
    public static final String MESSAGE_VIEW_APPOINTMENT_PATIENT_ALL_LISTED_OVERVIEW = "%1$d appointment(s) of %2$s";
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
    public static final String MESSAGE_APPOINTMENT_NUMBER_NOT_FOUND = "Patient %1$s has no appointment #%2$d.";

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
        patient.getNotes().ifPresent(notes -> builder.append("; Notes: ").append(notes));
        if (!patient.getSchedule().isEmpty()) {
            builder.append("; Appointments: ").append(patient.getSchedule());
        }
        return builder.toString();
    }

    /**
     * Returns {@code appointment} as shown in command messages: its date and time, and its details, if any.
     */
    public static String format(Appointment appointment) {
        return appointment + appointment.getDetails().map(details -> "; Details: " + details).orElse("");
    }

    /**
     * Describes clashes after a successful appointment change, including other visits of the same patient.
     */
    public static String formatClashWarning(Collection<PatientAppointment> appointments, PatientAppointment target) {
        List<PatientAppointment> clashes = AppointmentClashes.find(appointments, target);
        if (clashes.isEmpty()) {
            return "";
        }
        return "\nWarning: it clashes with " + clashes.stream()
                .map(entry -> entry.getPatient().getName() + " (" + entry.getPatient().getId()
                        + "), appointment #" + entry.getAppointment().getNumber())
                .collect(Collectors.joining("; ")) + ".";
    }

}
