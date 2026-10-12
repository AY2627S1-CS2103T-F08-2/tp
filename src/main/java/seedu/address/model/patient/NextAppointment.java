package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import seedu.address.commons.util.DateUtil;
import seedu.address.commons.util.TextNormalizer;

/**
 * Represents the date, and optionally the time, of a Patient's next appointment.
 * Guarantees: immutable; is valid as declared in {@link #isValidNextAppointment(String)}
 */
public class NextAppointment {

    public static final String MESSAGE_CONSTRAINTS = "Next appointment should be a real date in the format "
            + DateUtil.DATE_FORMAT + ", optionally followed by a 24-hour time in the format " + DateUtil.TIME_FORMAT
            + ", e.g. 2026-09-20 or 2026-09-20 14:00";

    public final String value;
    private final LocalDate date;
    private final LocalTime time;

    /**
     * Constructs a {@code NextAppointment}.
     *
     * @param nextAppointment A valid date, or date and time.
     */
    public NextAppointment(String nextAppointment) {
        requireNonNull(nextAppointment);
        checkArgument(isValidNextAppointment(nextAppointment), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(nextAppointment);
        String[] parts = value.split(" ");
        date = DateUtil.parseDate(parts[0]);
        time = parts.length == 2 ? DateUtil.parseTime(parts[1]) : null;
    }

    /**
     * Returns true if a given string is a valid date, or a valid date followed by a time.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidNextAppointment(String test) {
        String[] parts = TextNormalizer.normalize(test).split(" ");
        if (parts.length > 2) {
            return false;
        }
        try {
            DateUtil.parseDate(parts[0]);
            if (parts.length == 2) {
                DateUtil.parseTime(parts[1]);
            }
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the time of the appointment, or {@code Optional#empty()} if only a date was given.
     */
    public Optional<LocalTime> getTime() {
        return Optional.ofNullable(time);
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
        if (!(other instanceof NextAppointment otherNextAppointment)) {
            return false;
        }

        return value.equals(otherNextAppointment.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
