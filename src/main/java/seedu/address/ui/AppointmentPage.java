package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.PatientAppointment;

/**
 * The rows that the appointment view shows on one page: a heading for each day, each followed by that day's
 * appointments in time order, with those that have no time first. Today, those with no time come after the
 * appointments that have passed instead, right above the next one. The appointments are numbered from 1 across
 * the page. A page is either one week, with a heading for each day from Monday to Sunday, or the appointments in
 * a range of dates, with a heading for each day that has one. Only appointment rows can be selected, so this
 * class also finds the appointment rows to move between.
 * Guarantees: immutable.
 */
public final class AppointmentPage {

    /**
     * A row of the page.
     */
    public interface Row {}

    /**
     * The heading of one day, with the number of appointments on that day.
     */
    public record DayHeading(LocalDate date, int appointmentCount) implements Row {}

    /**
     * One appointment, with its number on the page.
     */
    public record Entry(int number, PatientAppointment appointment) implements Row {}

    private final List<LocalDate> days;
    private final List<Row> rows;
    private final int entryCount;
    /** The time that the page is for, which decides the appointments that have passed. */
    private final LocalDateTime now;

    private AppointmentPage(List<LocalDate> days, List<PatientAppointment> appointments, LocalDateTime now) {
        this.days = List.copyOf(days);
        this.now = now;
        List<Row> pageRows = new ArrayList<>();
        int number = 0;
        for (LocalDate day : days) {
            List<PatientAppointment> onDay = appointments.stream()
                    .filter(entry -> entry.getAppointment().getDate().equals(day))
                    .sorted(orderOnDay(day, now))
                    .toList();
            pageRows.add(new DayHeading(day, onDay.size()));
            for (PatientAppointment entry : onDay) {
                number++;
                pageRows.add(new Entry(number, entry));
            }
        }
        rows = List.copyOf(pageRows);
        entryCount = number;
    }

    /**
     * Returns the order of the appointments on {@code day}: by time, with those that have no time first. Today,
     * those with no time come after the appointments that have passed at {@code now}, and before the rest.
     */
    private static Comparator<PatientAppointment> orderOnDay(LocalDate day, LocalDateTime now) {
        if (!day.equals(now.toLocalDate())) {
            return Comparator.naturalOrder();
        }
        Comparator<PatientAppointment> byGroup = Comparator.comparingInt(entry -> groupToday(entry, now));
        return byGroup.thenComparing(Comparator.naturalOrder());
    }

    /**
     * Returns where an appointment today goes: 0 if it has passed, 1 if it has no time, and 2 if it is still
     * to come.
     */
    private static int groupToday(PatientAppointment entry, LocalDateTime now) {
        Appointment appointment = entry.getAppointment();
        if (appointment.getTime().isEmpty()) {
            return 1;
        }
        return AppointmentFormatter.hasPassed(appointment, now) ? 0 : 2;
    }

    /**
     * Returns the page of {@code week} as at {@code now}, with a heading for each of its days and the
     * appointments in {@code appointments} that fall in that week.
     */
    public static AppointmentPage ofWeek(AppointmentWeek week, List<PatientAppointment> appointments,
            LocalDateTime now) {
        requireAllNonNull(week, appointments, now);
        return new AppointmentPage(week.getDays(), keep(appointments, week::contains), now);
    }

    /**
     * Returns the page as at {@code now} of the appointments in {@code appointments} from {@code start} to
     * {@code end}, both included, with a heading for each day that has one. A range with no appointments gives
     * an empty page.
     */
    public static AppointmentPage ofRange(LocalDate start, LocalDate end, List<PatientAppointment> appointments,
            LocalDateTime now) {
        requireAllNonNull(start, end, appointments, now);
        List<PatientAppointment> inRange = keep(appointments,
                date -> !date.isBefore(start) && !date.isAfter(end));
        List<LocalDate> days = inRange.stream()
                .map(entry -> entry.getAppointment().getDate())
                .distinct()
                .sorted()
                .toList();
        return new AppointmentPage(days, inRange, now);
    }

    private static List<PatientAppointment> keep(List<PatientAppointment> appointments, Predicate<LocalDate> test) {
        return appointments.stream().filter(entry -> test.test(entry.getAppointment().getDate())).toList();
    }

    /**
     * Returns the rows of the page, in display order.
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Returns the number of appointments on the page.
     */
    public int getEntryCount() {
        return entryCount;
    }

    /**
     * Returns the appointment in the row at {@code index}, or {@code Optional#empty()} if that row is a heading
     * or there is no such row.
     */
    public Optional<PatientAppointment> getEntryAt(int index) {
        if (index < 0 || index >= rows.size() || !(rows.get(index) instanceof Entry entry)) {
            return Optional.empty();
        }
        return Optional.of(entry.appointment());
    }

    /**
     * Returns the index of the first appointment row, or {@code OptionalInt#empty()} if the page has none.
     */
    public OptionalInt firstEntryIndex() {
        return findEntry(IntStream.range(0, rows.size()), unused -> true);
    }

    /**
     * Returns the index of the last appointment row, or {@code OptionalInt#empty()} if the page has none.
     */
    public OptionalInt lastEntryIndex() {
        return findEntry(IntStream.range(0, rows.size()).map(i -> rows.size() - 1 - i), unused -> true);
    }

    /**
     * Returns the index of the first appointment row after the row at {@code index}, or
     * {@code OptionalInt#empty()} if there is none.
     */
    public OptionalInt entryIndexAfter(int index) {
        return findEntry(IntStream.range(Math.max(index + 1, 0), rows.size()), unused -> true);
    }

    /**
     * Returns the index of the last appointment row before the row at {@code index}, or
     * {@code OptionalInt#empty()} if there is none.
     */
    public OptionalInt entryIndexBefore(int index) {
        int end = Math.min(index, rows.size());
        return findEntry(IntStream.range(0, Math.max(end, 0)).map(i -> end - 1 - i), unused -> true);
    }

    /**
     * Returns the index of the heading of the day of the row at {@code index}, or of the row itself if it is a
     * heading. Returns 0 for an index outside the page.
     */
    public int headingIndexOf(int index) {
        int heading = Math.min(index, rows.size() - 1);
        while (heading > 0 && !(rows.get(heading) instanceof DayHeading)) {
            heading--;
        }
        return Math.max(heading, 0);
    }

    /**
     * Returns the index of the row of {@code appointment}, or {@code OptionalInt#empty()} if it is not on this
     * page. The patient's other details may have changed since, as {@link PatientAppointment#isSameEntry} allows.
     */
    public OptionalInt indexOf(PatientAppointment appointment) {
        return findEntry(IntStream.range(0, rows.size()),
                i -> ((Entry) rows.get(i)).appointment().isSameEntry(appointment));
    }

    /**
     * Returns the index of the appointment row to select when nothing is selected yet: on a page whose days
     * span today, the first appointment that has not passed, and on any other page, its first appointment.
     * Falls back to the first appointment of the page, and returns {@code OptionalInt#empty()} if it has none.
     */
    public OptionalInt defaultEntryIndex() {
        LocalDate today = now.toLocalDate();
        boolean isTodaySpanned = !days.isEmpty()
                && !today.isBefore(days.get(0)) && !today.isAfter(days.get(days.size() - 1));
        if (isTodaySpanned) {
            OptionalInt notPassed = findEntry(IntStream.range(0, rows.size()), i -> !AppointmentFormatter
                    .hasPassed(((Entry) rows.get(i)).appointment().getAppointment(), now));
            if (notPassed.isPresent()) {
                return notPassed;
            }
        }
        return firstEntryIndex();
    }

    /**
     * Returns the first index from {@code indices} whose row is an appointment row that passes {@code test}.
     */
    private OptionalInt findEntry(IntStream indices, IntPredicate test) {
        return indices.filter(i -> rows.get(i) instanceof Entry).filter(test).findFirst();
    }
}
