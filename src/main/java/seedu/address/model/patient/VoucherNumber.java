package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the number of vouchers that a voucher command gives to a patient or redeems from them.
 * A number of vouchers is a positive whole number: vouchers are given and redeemed one at a time, even though
 * a {@link VoucherBalance} can hold a fractional count through interest.
 * Guarantees: immutable; is valid as declared in {@link #isValidNumber(String)}.
 */
public class VoucherNumber {

    public static final String MESSAGE_CONSTRAINTS = "The number of vouchers must be a positive whole number.";

    /** Message for a number of vouchers that is a positive whole number, but too large to count with. */
    public static final String MESSAGE_TOO_LARGE = "The number of vouchers %1$s is too large.";

    /** Checked against the whole input, which must be digits only: no sign, fraction or spaces. */
    public static final String VALIDATION_REGEX = "[0-9]+";

    public final long value;

    /**
     * Constructs a {@code VoucherNumber}.
     *
     * @param number A positive whole number, with no sign, fraction or surrounding spaces.
     * @throws IllegalArgumentException if {@code number} is not a positive whole number, or does not fit a long.
     */
    public VoucherNumber(String number) {
        requireNonNull(number);
        checkArgument(isValidNumber(number), MESSAGE_CONSTRAINTS);
        try {
            value = Long.parseLong(number);
        } catch (NumberFormatException tooManyDigits) {
            throw new IllegalArgumentException(String.format(MESSAGE_TOO_LARGE, number));
        }
    }

    /**
     * Returns true if a given string is a valid number of vouchers, which leading zeros such as {@code 05} do not
     * stop. Whether it also fits a long is checked by the constructor, so that it can name the number too large.
     */
    public static boolean isValidNumber(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX) && !test.matches("0+");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VoucherNumber otherNumber)) {
            return false;
        }

        return value == otherNumber.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

}
