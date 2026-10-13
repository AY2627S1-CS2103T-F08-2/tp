package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import seedu.address.model.patient.Appointment;

/**
 * Formats a patient's next appointment for display, relative to a given day.
 */
public final class AppointmentFormatter {

    /** Appointments up to this many days ahead are shown by weekday, such as "Tue". */
    private static final int DAYS_SHOWN_AS_WEEKDAY = 6;

    private static final DateTimeFormatter WEEKDAY = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_MONTH_YEAR = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter FULL_DATE = DateTimeFormatter.ofPattern("EEE d MMM uuuu", Locale.ENGLISH);
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
     * Returns a full description of {@code appointment} for the patient details, such as
     * "Mon 12 Oct 2026, 09:30 (today)" or "Mon 30 Nov 2026, no time set".
     */
    public static String formatFull(Appointment appointment, LocalDate today) {
        requireNonNull(appointment);
        requireNonNull(today);
        LocalDate date = appointment.getDate();
        String text = date.format(FULL_DATE) + ", "
                + appointment.getTime().map(time -> time.format(TIME)).orElse("no time set");
        if (date.equals(today)) {
            return text + " (today)";
        }
        if (date.equals(today.plusDays(1))) {
            return text + " (tomorrow)";
        }
        if (date.isBefore(today)) {
            return text + " (past)";
        }
        return text;
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
