package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import seedu.address.model.patient.Appointment;

/**
 * Formats appointments and their dates for display, relative to a given day.
 */
public final class AppointmentFormatter {

    /** The text shown in place of the time of an appointment that has only a date. */
    public static final String NO_TIME = "No time";

    /** Appointments up to this many days ahead are shown by weekday, such as "Tue". */
    private static final int DAYS_SHOWN_AS_WEEKDAY = 6;

    private static final DateTimeFormatter WEEKDAY = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_MONTH_YEAR = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter WEEKDAY_DAY_MONTH =
            DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH);
    private static final DateTimeFormatter WEEKDAY_DAY_MONTH_YEAR =
            DateTimeFormatter.ofPattern("EEE d MMM uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private AppointmentFormatter() {}

    /**
     * Returns a short description of {@code appointment} for the patient list, such as "Today 09:30",
     * "Tomorrow", "Tue 14:00", "30 Nov" or "5 Jan 2027".
     */
    public static String formatShort(Appointment appointment, LocalDate today) {
        requireNonNull(appointment);
        requireNonNull(today);
        String day = describeDay(appointment.getDate(), today);
        return appointment.getTime().map(time -> day + " " + time.format(TIME)).orElse(day);
    }

    /**
     * Returns the time of {@code appointment}, such as "09:30", or {@link #NO_TIME} if it has only a date.
     */
    public static String formatTime(Appointment appointment) {
        requireNonNull(appointment);
        return appointment.getTime().map(time -> time.format(TIME)).orElse(NO_TIME);
    }

    /**
     * Returns the date of an appointment for the patient details, such as "Today", "Tomorrow", "Fri 23 Oct",
     * or "Mon 4 Jan 2027" for a date in another year.
     */
    public static String formatDetailDate(LocalDate date, LocalDate today) {
        requireNonNull(date);
        requireNonNull(today);
        if (date.equals(today)) {
            return "Today";
        }
        if (date.equals(today.plusDays(1))) {
            return "Tomorrow";
        }
        return formatWeekdayDate(date, today);
    }

    /**
     * Returns the heading of a day in the appointment view, such as "Today · Fri 16 Oct",
     * "Tomorrow · Sat 17 Oct", "Mon 12 Oct · past" or "Mon 19 Oct".
     */
    public static String formatDayHeading(LocalDate date, LocalDate today) {
        requireNonNull(date);
        requireNonNull(today);
        String weekdayDate = formatWeekdayDate(date, today);
        if (date.equals(today)) {
            return "Today · " + weekdayDate;
        }
        if (date.equals(today.plusDays(1))) {
            return "Tomorrow · " + weekdayDate;
        }
        if (date.isBefore(today)) {
            return weekdayDate + " · past";
        }
        return weekdayDate;
    }

    /**
     * Returns true if {@code appointment} is on {@code today}.
     */
    public static boolean isToday(Appointment appointment, LocalDate today) {
        return appointment.getDate().equals(today);
    }

    /**
     * Returns true if {@code appointment} is before {@code today}.
     */
    public static boolean isPast(Appointment appointment, LocalDate today) {
        return appointment.getDate().isBefore(today);
    }

    private static String formatWeekdayDate(LocalDate date, LocalDate today) {
        return date.format(date.getYear() == today.getYear() ? WEEKDAY_DAY_MONTH : WEEKDAY_DAY_MONTH_YEAR);
    }

    private static String describeDay(LocalDate date, LocalDate today) {
        if (date.equals(today)) {
            return "Today";
        }
        if (date.equals(today.plusDays(1))) {
            return "Tomorrow";
        }
        if (date.isAfter(today) && !date.isAfter(today.plusDays(DAYS_SHOWN_AS_WEEKDAY))) {
            return date.format(WEEKDAY);
        }
        if (date.getYear() == today.getYear()) {
            return date.format(DAY_MONTH);
        }
        return date.format(DAY_MONTH_YEAR);
    }
}
