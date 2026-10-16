package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.patient.Appointment;

public class AppointmentFormatterTest {

    /** A Monday. */
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 12);

    @Test
    public void formatShort_today_showsToday() {
        assertEquals("Today 09:30", AppointmentFormatter.formatShort(new Appointment("2026-10-12 09:30"), TODAY));
        assertEquals("Today", AppointmentFormatter.formatShort(new Appointment("2026-10-12"), TODAY));
    }

    @Test
    public void formatShort_tomorrow_showsTomorrow() {
        assertEquals("Tomorrow 14:00",
                AppointmentFormatter.formatShort(new Appointment("2026-10-13 14:00"), TODAY));
        assertEquals("Tomorrow", AppointmentFormatter.formatShort(new Appointment("2026-10-13"), TODAY));
    }

    @Test
    public void formatShort_withinSixDays_showsWeekday() {
        assertEquals("Wed", AppointmentFormatter.formatShort(new Appointment("2026-10-14"), TODAY));
        // the last day shown as a weekday
        assertEquals("Sun 08:15", AppointmentFormatter.formatShort(new Appointment("2026-10-18 08:15"), TODAY));
    }

    @Test
    public void formatShort_laterThisYear_showsDayAndMonth() {
        // the first day after the weekdays
        assertEquals("19 Oct", AppointmentFormatter.formatShort(new Appointment("2026-10-19"), TODAY));
        assertEquals("30 Nov 09:00",
                AppointmentFormatter.formatShort(new Appointment("2026-11-30 09:00"), TODAY));
    }

    @Test
    public void formatShort_pastThisYear_showsDayAndMonth() {
        assertEquals("11 Oct", AppointmentFormatter.formatShort(new Appointment("2026-10-11"), TODAY));
        assertEquals("5 Oct 08:00", AppointmentFormatter.formatShort(new Appointment("2026-10-05 08:00"), TODAY));
    }

    @Test
    public void formatShort_otherYear_showsYear() {
        assertEquals("5 Jan 2027", AppointmentFormatter.formatShort(new Appointment("2027-01-05"), TODAY));
        assertEquals("31 Dec 2025 23:59",
                AppointmentFormatter.formatShort(new Appointment("2025-12-31 23:59"), TODAY));
    }

    @Test
    public void formatTime_withAndWithoutTime() {
        assertEquals("09:30", AppointmentFormatter.formatTime(new Appointment("2026-10-12 09:30")));
        assertEquals("00:00", AppointmentFormatter.formatTime(new Appointment("2026-10-12 00:00")));
        assertEquals(AppointmentFormatter.NO_TIME, AppointmentFormatter.formatTime(new Appointment("2026-10-12")));
    }

    @Test
    public void formatDetailDate_variousDays() {
        assertEquals("Today", AppointmentFormatter.formatDetailDate(TODAY, TODAY));
        assertEquals("Tomorrow", AppointmentFormatter.formatDetailDate(TODAY.plusDays(1), TODAY));
        // unlike formatShort, a day within the week still shows its date
        assertEquals("Wed 14 Oct", AppointmentFormatter.formatDetailDate(TODAY.plusDays(2), TODAY));
        assertEquals("Sun 11 Oct", AppointmentFormatter.formatDetailDate(TODAY.minusDays(1), TODAY));
        assertEquals("Mon 2 Nov", AppointmentFormatter.formatDetailDate(LocalDate.of(2026, 11, 2), TODAY));
        assertEquals("Mon 4 Jan 2027", AppointmentFormatter.formatDetailDate(LocalDate.of(2027, 1, 4), TODAY));
        assertEquals("Wed 31 Dec 2025", AppointmentFormatter.formatDetailDate(LocalDate.of(2025, 12, 31), TODAY));
    }

    @Test
    public void formatDayHeading_variousDays() {
        assertEquals("Today · Mon 12 Oct", AppointmentFormatter.formatDayHeading(TODAY, TODAY));
        assertEquals("Tomorrow · Tue 13 Oct", AppointmentFormatter.formatDayHeading(TODAY.plusDays(1), TODAY));
        assertEquals("Wed 14 Oct", AppointmentFormatter.formatDayHeading(TODAY.plusDays(2), TODAY));
        assertEquals("Sun 11 Oct · past", AppointmentFormatter.formatDayHeading(TODAY.minusDays(1), TODAY));
        assertEquals("Fri 1 Jan 2027", AppointmentFormatter.formatDayHeading(LocalDate.of(2027, 1, 1), TODAY));
        assertEquals("Wed 31 Dec 2025 · past",
                AppointmentFormatter.formatDayHeading(LocalDate.of(2025, 12, 31), TODAY));
    }

    @Test
    public void isTodayAndIsPast_boundaries() {
        Appointment yesterday = new Appointment("2026-10-11 23:59");
        Appointment today = new Appointment("2026-10-12");
        Appointment tomorrow = new Appointment("2026-10-13 00:00");

        assertFalse(AppointmentFormatter.isToday(yesterday, TODAY));
        assertTrue(AppointmentFormatter.isToday(today, TODAY));
        assertFalse(AppointmentFormatter.isToday(tomorrow, TODAY));

        assertTrue(AppointmentFormatter.isPast(yesterday, TODAY));
        assertFalse(AppointmentFormatter.isPast(today, TODAY));
        assertFalse(AppointmentFormatter.isPast(tomorrow, TODAY));
    }

    @Test
    public void format_nullArguments_throwsNullPointerException() {
        Appointment appointment = new Appointment("2026-10-12");
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatShort(null, TODAY));
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatShort(appointment, null));
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatTime(null));
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatDetailDate(null, TODAY));
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatDetailDate(TODAY, null));
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatDayHeading(null, TODAY));
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatDayHeading(TODAY, null));
    }
}
