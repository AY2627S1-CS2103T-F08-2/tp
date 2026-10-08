package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Optional;

import seedu.address.commons.util.TextNormalizer;

/**
 * Represents the date, and optionally the time, of a Patient's next appointment.
 * Guarantees: immutable; is valid as declared in {@link #isValidNextAppointment(String)}
 */
public class NextAppointment {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";

    public static final String MESSAGE_CONSTRAINTS = "Next appointment should be a real date in the format "
            + DATE_FORMAT + ", optionally followed by a 24-hour time in the format " + TIME_FORMAT
            + ", e.g. 2026-09-20 or 2026-09-20 14:00";

    // Strict resolving rejects dates that do not exist, such as 2026-02-30.
    // "uuuu" is the proleptic year, which strict resolving requires in place of "yyyy".
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern(TIME_FORMAT).withResolverStyle(ResolverStyle.STRICT);

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
        date = LocalDate.parse(parts[0], DATE_FORMATTER);
        time = parts.length == 2 ? LocalTime.parse(parts[1], TIME_FORMATTER) : null;
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
            LocalDate.parse(parts[0], DATE_FORMATTER);
            if (parts.length == 2) {
                LocalTime.parse(parts[1], TIME_FORMATTER);
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
