package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.TextNormalizer;

/**
 * Represents a Person's address in the address book.
 * The address is stored in upper case with whitespace normalized.
 * Guarantees: immutable; is valid as declared in {@link #isValidAddress(String)}
 */
public class Address {

    public static final String MESSAGE_CONSTRAINTS = "Addresses can take any values, and should not be blank";

    /*
     * Checked against the normalized address, so a blank string becomes "" and is rejected.
     */
    public static final String VALIDATION_REGEX = ".+";

    public final String value;

    /**
     * Constructs an {@code Address}.
     *
     * @param address A valid address.
     */
    public Address(String address) {
        requireNonNull(address);
        checkArgument(isValidAddress(address), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(address);
    }

    /**
     * Returns true if a given string is a valid address.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidAddress(String test) {
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
        if (!(other instanceof Address otherAddress)) {
            return false;
        }

        return value.equals(otherAddress.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
