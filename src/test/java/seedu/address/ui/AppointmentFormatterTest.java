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
    public void formatFull_variousDays_showsFullDateAndTag() {
        assertEquals("Mon 12 Oct 2026, 09:30 (today)",
                AppointmentFormatter.formatFull(new Appointment("2026-10-12 09:30"), TODAY));
        assertEquals("Tue 13 Oct 2026, no time set (tomorrow)",
                AppointmentFormatter.formatFull(new Appointment("2026-10-13"), TODAY));
        assertEquals("Mon 5 Oct 2026, 08:00 (past)",
                AppointmentFormatter.formatFull(new Appointment("2026-10-05 08:00"), TODAY));
        assertEquals("Mon 30 Nov 2026, no time set",
                AppointmentFormatter.formatFull(new Appointment("2026-11-30"), TODAY));
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
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatFull(null, TODAY));
        assertThrows(NullPointerException.class, () -> AppointmentFormatter.formatFull(appointment, null));
    }
}
