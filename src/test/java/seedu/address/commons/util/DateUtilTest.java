package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DateUtilTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 19);
    private static final LocalDate SUNDAY = LocalDate.of(2026, 10, 25);

    @Test
    public void getStartOfWeek_eachDayOfWeek_returnsItsMonday() {
        for (int i = 0; i < DateUtil.DAYS_IN_WEEK; i++) {
            assertEquals(MONDAY, DateUtil.getStartOfWeek(MONDAY.plusDays(i)));
        }
        // the next Monday starts the next week
        assertEquals(LocalDate.of(2026, 10, 26), DateUtil.getStartOfWeek(SUNDAY.plusDays(1)));
    }

    @Test
    public void getEndOfWeek_eachDayOfWeek_returnsItsSunday() {
        for (int i = 0; i < DateUtil.DAYS_IN_WEEK; i++) {
            assertEquals(SUNDAY, DateUtil.getEndOfWeek(MONDAY.plusDays(i)));
        }
        // the Sunday before ends the week before
        assertEquals(LocalDate.of(2026, 10, 18), DateUtil.getEndOfWeek(MONDAY.minusDays(1)));
    }

    @Test
    public void getStartOfWeek_weekOverNewYear_spansBothYears() {
        // Thursday 1 January 2026 is in the week from Monday 29 December 2025 to Sunday 4 January 2026
        LocalDate newYearsDay = LocalDate.of(2026, 1, 1);
        assertEquals(LocalDate.of(2025, 12, 29), DateUtil.getStartOfWeek(newYearsDay));
        assertEquals(LocalDate.of(2026, 1, 4), DateUtil.getEndOfWeek(newYearsDay));
    }

    @Test
    public void getStartOfWeek_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DateUtil.getStartOfWeek(null));
        assertThrows(NullPointerException.class, () -> DateUtil.getEndOfWeek(null));
    }

    @Test
    public void parseDate_validDate_returnsLocalDate() {
        assertEquals(LocalDate.of(2026, 10, 5), DateUtil.parseDate("2026-10-05"));
    }

    @Test
    public void parseDate_zeroYear_isAccepted() {
        // year 0000 is a valid ISO-8601 date and stays accepted
        assertEquals(LocalDate.of(0, 1, 1), DateUtil.parseDate("0000-01-01"));
    }

    @Test
    public void parseDate_nonExistentDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseDate("2026-02-30"));
    }

    @Test
    public void parseDate_signedOrWiderYear_throwsDateTimeParseException() {
        // the uuuu pattern alone would parse these, so the shape guard rejects them
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseDate("+12345-01-01"));
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseDate("+2026-01-01"));
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseDate("-2026-01-01"));
    }

    @Test
    public void parseDate_oneDigitMonthOrDay_throwsDateTimeParseException() {
        // the shape guard rejects a month or day that does not have two digits
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseDate("2026-1-05"));
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseDate("2026-01-5"));
    }

    @Test
    public void parseDate_wrongShape_namesThePartThatIsWrong() {
        // the exception text names the first part that breaks the shape, not the year every time
        assertThrows(DateTimeParseException.class,
                "Text '+2026-01-01' is not in the form yyyy-MM-dd: "
                        + "the year must have exactly four digits without a sign", ()
                -> DateUtil.parseDate("+2026-01-01"));
        assertThrows(DateTimeParseException.class,
                "Text '2026-1-05' is not in the form yyyy-MM-dd: the month must have two digits", ()
                -> DateUtil.parseDate("2026-1-05"));
        assertThrows(DateTimeParseException.class,
                "Text '2026-01-5' is not in the form yyyy-MM-dd: the day must have two digits", ()
                -> DateUtil.parseDate("2026-01-5"));
    }

    @Test
    public void parseTime_validTime_returnsLocalTime() {
        assertEquals(LocalTime.of(9, 30), DateUtil.parseTime("09:30"));
        assertEquals(LocalTime.of(23, 59), DateUtil.parseTime("23:59"));
    }

    @Test
    public void parseTime_invalidTime_throwsDateTimeParseException() {
        // out-of-range and one-digit fields are rejected, matching the UG's HH:mm format
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseTime("24:00"));
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseTime("09:60"));
        assertThrows(DateTimeParseException.class, () -> DateUtil.parseTime("9:30"));
    }

    @Test
    public void parseDateAndParseTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> DateUtil.parseDate(null));
        assertThrows(NullPointerException.class, () -> DateUtil.parseTime(null));
    }
}
