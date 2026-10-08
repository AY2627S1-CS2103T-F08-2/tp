package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.TextNormalizer;

/**
 * Represents a Patient's phone number in the address book.
 * The number is stored with leading, trailing and repeated whitespace removed.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should only contain digits, spaces, '+' and '-', and should have at least 3 digits";

    /*
     * Checked against the normalized number. The lookahead requires at least 3 digits anywhere in it.
     */
    public static final String VALIDATION_REGEX = "(?=(?:\\D*\\d){3})[\\d+\\- ]+";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(phone);
    }

    /**
     * Returns true if a given string is a valid phone number.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidPhone(String test) {
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
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
