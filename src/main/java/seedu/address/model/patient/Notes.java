package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.TextNormalizer;

/**
 * Represents a Patient's notes in LaiNurse: free text about personal information not covered by other patient fields,
 * such as hobbies, pet peeves or daily routines.
 * The notes are stored in upper case with whitespace normalized.
 * Guarantees: immutable; is valid as declared in {@link #isValidNotes(String)}
 */
public class Notes {

    public static final String MESSAGE_CONSTRAINTS = "Notes can take any values, and should not be blank";

    /*
     * Checked against the normalized notes, so a blank string becomes "" and is rejected.
     */
    public static final String VALIDATION_REGEX = ".+";

    public final String value;

    /**
     * Constructs a {@code Notes}.
     *
     * @param notes Valid notes.
     */
    public Notes(String notes) {
        requireNonNull(notes);
        checkArgument(isValidNotes(notes), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(notes);
    }

    /**
     * Returns true if a given string is valid notes.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidNotes(String test) {
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
        if (!(other instanceof Notes otherNotes)) {
            return false;
        }

        return value.equals(otherNotes.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
