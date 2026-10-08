package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.TextNormalizer;

/**
 * Represents a Patient's unique patient ID in the address book.
 * The ID is stored in upper case, so IDs that differ only in case are equal.
 * Guarantees: immutable; is valid as declared in {@link #isValidId(String)}
 */
public class PatientId {

    public static final String MESSAGE_CONSTRAINTS =
            "Patient IDs should only contain letters and digits, and should be 1 to 10 characters long";

    /*
     * Checked against the normalized (trimmed, upper case) ID.
     */
    public static final String VALIDATION_REGEX = "[A-Z0-9]{1,10}";

    public final String value;

    /**
     * Constructs a {@code PatientId}.
     *
     * @param id A valid patient ID.
     */
    public PatientId(String id) {
        requireNonNull(id);
        checkArgument(isValidId(id), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(id);
    }

    /**
     * Returns true if a given string is a valid patient ID.
     * Leading and trailing whitespace and letter case are ignored.
     */
    public static boolean isValidId(String test) {
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
        if (!(other instanceof PatientId otherId)) {
            return false;
        }

        return value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
