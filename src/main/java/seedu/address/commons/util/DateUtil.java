package seedu.address.commons.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Contains date parsing and formatting utilities.
 */
public final class DateUtil {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";

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
}
