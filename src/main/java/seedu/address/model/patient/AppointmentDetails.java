package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.TextNormalizer;

/**
 * Represents the details of one of a Patient's appointments in LaiNurse: free text about the visit, such as what
 * to do or what to bring.
 * The details are stored in upper case with whitespace normalized.
 * Guarantees: immutable; is valid as declared in {@link #isValidAppointmentDetails(String)}
 */
public class AppointmentDetails {

    public static final String MESSAGE_CONSTRAINTS = "Appointment details can take any values, and must not be blank.";

    /*
     * Checked against the normalized details, so a blank string becomes "" and is rejected.
     */
    public static final String VALIDATION_REGEX = ".+";

    public final String value;

    /**
     * Constructs an {@code AppointmentDetails}.
     *
     * @param details Valid appointment details.
     */
    public AppointmentDetails(String details) {
        requireNonNull(details);
        checkArgument(isValidAppointmentDetails(details), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(details);
    }

    /**
     * Returns true if a given string is valid appointment details.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidAppointmentDetails(String test) {
        return TextNormalizer.normalize(test).matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppointmentDetails otherDetails)) {
            return false;
        }

        return value.equals(otherDetails.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
