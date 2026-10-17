package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientAppointment;

/**
 * Formats appointments and their dates for display, relative to a given day, and decides which appointments
 * have passed.
 */
public final class AppointmentFormatter {

    /**
     * The text shown in the appointment list in place of the time of an appointment that has only a date. It is
     * written with figure dashes, each as wide as a digit, so that it is as wide as a time such as "09:30".
     */
    public static final String NO_TIME = "\u2012\u2012:\u2012\u2012";
    /** The text shown in the patient details in place of the time of an appointment that has only a date. */
    public static final String NO_TIME_DETAIL = "No time specified";

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
     * Returns the time of {@code appointment} for the appointment list, such as "09:30", or {@link #NO_TIME}
     * if it has only a date.
     */
    public static String formatTime(Appointment appointment) {
        requireNonNull(appointment);
        return appointment.getTime().map(time -> time.format(TIME)).orElse(NO_TIME);
    }

    /**
     * Returns the time of {@code appointment} for the patient details, such as "09:30", or
     * {@link #NO_TIME_DETAIL} if it has only a date.
     */
    public static String formatDetailTime(Appointment appointment) {
        requireNonNull(appointment);
        return appointment.getTime().map(time -> time.format(TIME)).orElse(NO_TIME_DETAIL);
    }

    /**
     * Returns the earliest appointment in {@code appointments} that has not passed at {@code now}, or
     * {@code Optional#empty()} if all have passed. On the same day, one with no time comes before one with a
     * time, as {@link Appointment#compareTo} orders them.
     */
    public static Optional<Appointment> findFirstNotPassed(List<Appointment> appointments, LocalDateTime now) {
        requireNonNull(appointments);
        requireNonNull(now);
        return appointments.stream().filter(appointment -> !hasPassed(appointment, now)).sorted().findFirst();
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
     * Returns the dates of a range of appointments, both included, for the header of the appointment view:
     * "Fri 16 Oct 2026" for one day, "Thu 1 Oct – Sat 31 Oct 2026", "Mon 28 Dec 2026 – Fri 1 Jan 2027" across
     * two years, or "From Fri 16 Oct 2026" for a range with no end, whose end is {@code LocalDate.MAX}.
     */
    public static String formatRange(LocalDate start, LocalDate end) {
        requireNonNull(start);
        requireNonNull(end);
        if (end.equals(LocalDate.MAX)) {
            return "From " + start.format(WEEKDAY_DAY_MONTH_YEAR);
        }
        String endText = end.format(WEEKDAY_DAY_MONTH_YEAR);
        if (start.equals(end)) {
            return endText;
        }
        if (start.getYear() == end.getYear()) {
            return start.format(WEEKDAY_DAY_MONTH) + " – " + endText;
        }
        return start.format(WEEKDAY_DAY_MONTH_YEAR) + " – " + endText;
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

    /**
     * Returns true if {@code appointment} has passed at {@code now}: it is on an earlier day, or earlier today at
     * a time before the minute of {@code now}. An appointment today with no time has not passed, as it can still
     * be at any time of the day.
     */
    public static boolean hasPassed(Appointment appointment, LocalDateTime now) {
        requireNonNull(appointment);
        requireNonNull(now);
        LocalDate today = now.toLocalDate();
        if (!isToday(appointment, today)) {
            return isPast(appointment, today);
        }
        LocalTime thisMinute = now.toLocalTime().truncatedTo(ChronoUnit.MINUTES);
        return appointment.getTime().map(time -> time.isBefore(thisMinute)).orElse(false);
    }

    /**
     * Returns true if the window shows {@code appointments} differently at {@code later} than at {@code earlier}:
     * the two are on different days, or one of the appointments has passed at one but not at the other.
     */
    public static boolean hasChangedBetween(List<PatientAppointment> appointments, LocalDateTime earlier,
            LocalDateTime later) {
        requireNonNull(appointments);
        requireNonNull(earlier);
        requireNonNull(later);
        if (!earlier.toLocalDate().equals(later.toLocalDate())) {
            return true;
        }
        return appointments.stream()
                .map(PatientAppointment::getAppointment)
                .anyMatch(appointment -> hasPassed(appointment, earlier) != hasPassed(appointment, later));
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
