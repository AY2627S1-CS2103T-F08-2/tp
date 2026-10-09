package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class DateParserTest {

    @Test
    public void isValidDate() {
        // valid dates
        assertTrue(DateParser.isValidDate("2026-01-01"));
        assertTrue(DateParser.isValidDate("2026-12-31"));
        assertTrue(DateParser.isValidDate("2028-02-29")); // leap year
        assertTrue(DateParser.isValidDate(" 2026-01-01 ")); // with whitespace

        // invalid dates
        assertFalse(DateParser.isValidDate("2026/01/01")); // wrong separator
        assertFalse(DateParser.isValidDate("2026-1-01")); // month not zero-padded
        assertFalse(DateParser.isValidDate("2026-13-01")); // invalid month
        assertFalse(DateParser.isValidDate("2026-02-30")); // invalid date
        assertFalse(DateParser.isValidDate("2026-02-29")); // not a leap year
        assertFalse(DateParser.isValidDate("")); // empty
        assertFalse(DateParser.isValidDate("tomorrow")); // not a date
    }

    @Test
    public void parseDate_invalidDate_throwsParseException() {
        assertThrows(ParseException.class, () -> DateParser.parseDate("invalid"));
        assertThrows(ParseException.class, () -> DateParser.parseDate("2026-02-30"));
        assertThrows(ParseException.class, () -> DateParser.parseDate("2026-13-01"));
    }

    @Test
    public void parseDate_validDate_success() throws ParseException {
        assertEquals(LocalDate.of(2026, 1, 15), DateParser.parseDate("2026-01-15"));
        assertEquals(LocalDate.of(2026, 1, 15), DateParser.parseDate(" 2026-01-15 "));
    }
}
