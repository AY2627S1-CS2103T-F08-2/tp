package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Contains utility methods for parsing dates.
 */
public class DateParser {

    // Strict resolving rejects dates that do not exist, such as 2026-02-30.
    // "uuuu" is the proleptic year, which strict resolving requires in place of "yyyy".
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses {@code String date} into a {@code LocalDate}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code date} is invalid.
     */
    public static LocalDate parseDate(String date) throws ParseException {
        requireNonNull(date);
        String trimmedDate = date.trim();
        try {
            return LocalDate.parse(trimmedDate, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid date. Expected format: yyyy-MM-dd");
        }
    }

    /**
     * Returns true if a given string is a valid date in yyyy-MM-dd format.
     * Leading and trailing whitespaces will be ignored.
     */
    public static boolean isValidDate(String test) {
        try {
            LocalDate.parse(test.trim(), DATE_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
