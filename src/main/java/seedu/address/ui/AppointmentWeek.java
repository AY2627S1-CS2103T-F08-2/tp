package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * One week of the appointment view, from Monday to Sunday.
 * Guarantees: immutable.
 */
public final class AppointmentWeek {

    private static final int DAYS_IN_WEEK = 7;

    private static final DateTimeFormatter WEEKDAY_DAY = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);
    private static final DateTimeFormatter WEEKDAY_DAY_MONTH =
            DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH);
    private static final DateTimeFormatter WEEKDAY_DAY_MONTH_YEAR =
            DateTimeFormatter.ofPattern("EEE d MMM uuuu", Locale.ENGLISH);

    private final LocalDate monday;

    private AppointmentWeek(LocalDate monday) {
        this.monday = monday;
    }

    /**
     * Returns the week that contains {@code date}.
     */
    public static AppointmentWeek of(LocalDate date) {
        requireNonNull(date);
        return new AppointmentWeek(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
    }

    /**
     * Returns the Monday that starts this week.
     */
    public LocalDate getStart() {
        return monday;
    }

    /**
     * Returns the Sunday that ends this week.
     */
    public LocalDate getEnd() {
        return monday.plusDays(DAYS_IN_WEEK - 1);
    }

    /**
     * Returns the seven days of this week, from Monday to Sunday.
     */
    public List<LocalDate> getDays() {
        return Stream.iterate(monday, day -> day.plusDays(1)).limit(DAYS_IN_WEEK).toList();
    }

    /**
     * Returns true if {@code date} is in this week.
     */
    public boolean contains(LocalDate date) {
        requireNonNull(date);
        return !date.isBefore(getStart()) && !date.isAfter(getEnd());
    }

    /**
     * Returns the week before this one.
     */
    public AppointmentWeek previous() {
        return new AppointmentWeek(monday.minusWeeks(1));
    }

    /**
     * Returns the week after this one.
     */
    public AppointmentWeek next() {
        return new AppointmentWeek(monday.plusWeeks(1));
    }

    /**
     * Returns the dates of this week, such as "Mon 12 – Sun 18 Oct 2026", "Mon 28 Sep – Sun 4 Oct 2026",
     * or "Mon 28 Dec 2026 – Sun 3 Jan 2027" for a week that spans two years.
     */
    public String describeDates() {
        LocalDate end = getEnd();
        String endText = end.format(WEEKDAY_DAY_MONTH_YEAR);
        if (monday.getYear() != end.getYear()) {
            return monday.format(WEEKDAY_DAY_MONTH_YEAR) + " – " + endText;
        }
        if (monday.getMonth() != end.getMonth()) {
            return monday.format(WEEKDAY_DAY_MONTH) + " – " + endText;
        }
        return monday.format(WEEKDAY_DAY) + " – " + endText;
    }

    /**
     * Returns where this week is relative to the week of {@code today}: "This week", "Next week" or "Last week".
     * Returns {@code Optional#empty()} for any other week.
     */
    public Optional<String> describeRelativeTo(LocalDate today) {
        AppointmentWeek thisWeek = of(today);
        if (equals(thisWeek)) {
            return Optional.of("This week");
        }
        if (equals(thisWeek.next())) {
            return Optional.of("Next week");
        }
        if (equals(thisWeek.previous())) {
            return Optional.of("Last week");
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppointmentWeek otherWeek)) {
            return false;
        }

        return monday.equals(otherWeek.monday);
    }

    @Override
    public int hashCode() {
        return monday.hashCode();
    }

    @Override
    public String toString() {
        return describeDates();
    }
}
