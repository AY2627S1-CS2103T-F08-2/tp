package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the address book.
 * The name is stored in upper case with whitespace normalized, so names that differ only in case
 * or spacing are equal.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS = "Names can contain any characters, and should not be blank";

    /*
     * Checked against the normalized name, so a blank string becomes "" and is rejected.
     */
    public static final String VALIDATION_REGEX = ".+";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = TextNormalizer.normalize(name);
    }

    /**
     * Returns true if a given string is a valid name.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidName(String test) {
        return TextNormalizer.normalize(test).matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
