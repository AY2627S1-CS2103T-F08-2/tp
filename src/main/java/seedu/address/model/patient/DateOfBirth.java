package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.address.commons.util.DateUtil;

/**
 * Represents a patient's date of birth in LaiNurse. The date is a real date in the format yyyy-MM-dd, and cannot
 * be in the future: a patient is born before they are cared for.
 * Guarantees: immutable; is valid as declared in {@link #isValidDateOfBirth(String)}.
 */
public class DateOfBirth {

    public static final String MESSAGE_CONSTRAINTS = "Dates of birth must be a real date in the format "
            + DateUtil.DATE_FORMAT + ", and cannot be in the future.";

    public final LocalDate value;

    /**
     * Constructs a {@code DateOfBirth}.
     *
     * @param dateOfBirth A real date in the format yyyy-MM-dd, not after today.
     */
    public DateOfBirth(String dateOfBirth) {
        requireNonNull(dateOfBirth);
        checkArgument(isValidDateOfBirth(dateOfBirth), MESSAGE_CONSTRAINTS);
        value = DateUtil.parseDate(dateOfBirth.trim());
    }

    /**
     * Returns true if a given string is a valid date of birth: a real date in the format yyyy-MM-dd, not after
     * today. Surrounding whitespace is ignored.
     */
    public static boolean isValidDateOfBirth(String test) {
        requireNonNull(test);
        try {
            return !DateUtil.parseDate(test.trim()).isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Returns true if the date of birth falls on the same month and day as {@code date}, whatever the year it
     * was in: the month and day that a patient's birthday is made of. A date of birth of 29 Feb falls on a
     * 29 Feb only.
     */
    public boolean isOn(LocalDate date) {
        return value.getMonth() == date.getMonth() && value.getDayOfMonth() == date.getDayOfMonth();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DateOfBirth otherDateOfBirth)) {
            return false;
        }

        return value.equals(otherDateOfBirth.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
