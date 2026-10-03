package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.TextNormalizer;

/**
 * Represents a Person's medical history in the address book, as free-form notes.
 * The notes are stored in upper case with whitespace normalized.
 * Guarantees: immutable; is valid as declared in {@link #isValidMedicalHistory(String)}
 */
public class MedicalHistory {

    public static final String MESSAGE_CONSTRAINTS = "Medical history can take any values, and should not be blank";

    /*
     * Checked against the normalized notes, so a blank string becomes "" and is rejected.
     */
    public static final String VALIDATION_REGEX = ".+";

    public final String value;

    /**
     * Constructs a {@code MedicalHistory}.
     *
     * @param medicalHistory Valid medical history notes.
     */
    public MedicalHistory(String medicalHistory) {
        requireNonNull(medicalHistory);
        checkArgument(isValidMedicalHistory(medicalHistory), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(medicalHistory);
    }

    /**
     * Returns true if a given string is valid medical history.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidMedicalHistory(String test) {
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
        if (!(other instanceof MedicalHistory otherMedicalHistory)) {
            return false;
        }

        return value.equals(otherMedicalHistory.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
