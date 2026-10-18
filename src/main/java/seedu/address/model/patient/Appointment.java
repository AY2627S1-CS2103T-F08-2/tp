package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.DateUtil;
import seedu.address.commons.util.TextNormalizer;

/**
 * Represents one of a Patient's appointments: its date, optionally its time, and optionally its details.
 * Appointments are ordered by date, then by time; an appointment with only a date comes before
 * any appointment with a time on the same date. The details play no part in the order: two appointments at the
 * same date and time are the same appointment, as {@link #isSameAppointment(Appointment)} gives, even if their
 * details differ. {@link #equals(Object)} compares the details too.
 * Guarantees: immutable; is valid as declared in {@link #isValidAppointment(String)}
 */
public class Appointment implements Comparable<Appointment> {

    public static final String MESSAGE_CONSTRAINTS = "Appointments should be a real date in the format "
            + DateUtil.DATE_FORMAT + ", optionally followed by a 24-hour time in the format " + DateUtil.TIME_FORMAT
            + ", e.g. 2026-09-20 or 2026-09-20 14:00";

    /** The date, and the time if there is one, as in "2026-09-20" or "2026-09-20 14:00". */
    public final String value;
    private final LocalDate date;
    private final LocalTime time;
    private final AppointmentDetails details; // null if none

    /**
     * Constructs an {@code Appointment} with no details.
     *
     * @param appointment A valid date, or date and time.
     */
    public Appointment(String appointment) {
        this(appointment, Optional.empty());
    }

    /**
     * Constructs an {@code Appointment} with {@code details}, or with no details if it is empty.
     *
     * @param appointment A valid date, or date and time.
     * @param details The details of the appointment, if it has any.
     */
    public Appointment(String appointment, Optional<AppointmentDetails> details) {
        requireNonNull(appointment);
        requireNonNull(details);
        checkArgument(isValidAppointment(appointment), MESSAGE_CONSTRAINTS);
        value = TextNormalizer.normalize(appointment);
        String[] parts = value.split(" ");
        date = DateUtil.parseDate(parts[0]);
        time = parts.length == 2 ? DateUtil.parseTime(parts[1]) : null;
        this.details = details.orElse(null);
    }

    /**
     * Returns true if a given string is a valid date, or a valid date followed by a time.
     * Leading, trailing and repeated whitespace is ignored.
     */
    public static boolean isValidAppointment(String test) {
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

    /**
     * Returns the date of the appointment.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the time of the appointment, or {@code Optional#empty()} if only a date was given.
     */
    public Optional<LocalTime> getTime() {
        return Optional.ofNullable(time);
    }

    /**
     * Returns the details of the appointment, or {@code Optional#empty()} if it has none.
     */
    public Optional<AppointmentDetails> getDetails() {
        return Optional.ofNullable(details);
    }

    /**
     * Returns true if {@code other} is at the same date and time, whatever the details of either.
     * This defines a weaker notion of equality between two appointments, as {@link Patient#isSamePatient} does
     * for patients.
     */
    public boolean isSameAppointment(Appointment other) {
        return other != null && compareTo(other) == 0;
    }

    /**
     * Compares appointments by date, then by time.
     * An appointment with only a date comes before any appointment with a time on the same day.
     * The details are not compared, so this order is inconsistent with {@link #equals(Object)}.
     */
    @Override
    public int compareTo(Appointment other) {
        return Comparator.comparing(Appointment::getDate)
                .thenComparing(appointment -> appointment.time, Comparator.nullsFirst(Comparator.naturalOrder()))
                .compare(this, other);
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
        if (!(other instanceof Appointment otherAppointment)) {
            return false;
        }

        return value.equals(otherAppointment.value) && Objects.equals(details, otherAppointment.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, details);
    }

}
