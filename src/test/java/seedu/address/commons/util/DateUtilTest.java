package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

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
}
