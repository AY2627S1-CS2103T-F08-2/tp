package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;

/**
 * Contains date parsing and formatting utilities, and the week that a date is in.
 */
public final class DateUtil {

    public static final int DAYS_IN_WEEK = 7;

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String MESSAGE_CONSTRAINTS = "Dates should be a real date in the format " + DATE_FORMAT;

    // Strict resolving rejects dates that do not exist, such as 2026-02-30.
    // "uuuu" is the proleptic year, which strict resolving requires in place of "yyyy".
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern(TIME_FORMAT).withResolverStyle(ResolverStyle.STRICT);

    private DateUtil() {}

    /**
     * Parses {@code String date} into a {@code LocalDate}.
     *
     * @throws DateTimeParseException if the given {@code date} is invalid.
     */
    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    /**
     * Parses {@code String time} into a {@code LocalTime}.
     *
     * @throws DateTimeParseException if the given {@code time} is invalid.
     */
    public static LocalTime parseTime(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    /**
     * Returns the Monday of the week, from Monday to Sunday, that contains {@code date}.
     * The logic and the UI both take their weeks from here, so that they agree on which days a week has.
     */
    public static LocalDate getStartOfWeek(LocalDate date) {
        requireNonNull(date);
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Returns the Sunday of the week, from Monday to Sunday, that contains {@code date}.
     */
    public static LocalDate getEndOfWeek(LocalDate date) {
        return getStartOfWeek(date).plusDays(DAYS_IN_WEEK - 1);
    }
}
