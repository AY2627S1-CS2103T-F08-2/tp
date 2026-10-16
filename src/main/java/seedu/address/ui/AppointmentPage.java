package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import seedu.address.model.patient.PatientAppointment;

/**
 * The rows that the appointment view shows for one week: a heading for each day from Monday to Sunday, each
 * followed by that day's appointments in time order. The appointments are numbered from 1 across the week.
 * Only appointment rows can be selected, so this class also finds the appointment rows to move between.
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
     * One appointment, with its number in the week.
     */
    public record Entry(int number, PatientAppointment appointment) implements Row {}

    private final AppointmentWeek week;
    private final List<Row> rows;
    private final int entryCount;

    /**
     * Creates the page of {@code week}, with the appointments in {@code appointments} that fall in that week.
     */
    public AppointmentPage(AppointmentWeek week, List<PatientAppointment> appointments) {
        requireAllNonNull(week, appointments);
        this.week = week;
        List<PatientAppointment> inWeek = appointments.stream()
                .filter(entry -> week.contains(entry.getAppointment().getDate()))
                .sorted()
                .toList();

        List<Row> pageRows = new ArrayList<>();
        int number = 0;
        for (LocalDate day : week.getDays()) {
            List<PatientAppointment> onDay = inWeek.stream()
                    .filter(entry -> entry.getAppointment().getDate().equals(day))
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

    public AppointmentWeek getWeek() {
        return week;
    }

    /**
     * Returns the rows of the page, in display order.
     */
    public List<Row> getRows() {
        return rows;
    }

    /**
     * Returns the number of appointments in the week.
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
     * Returns the index of the first appointment row, or {@code OptionalInt#empty()} if the week has none.
     */
    public OptionalInt firstEntryIndex() {
        return findEntry(IntStream.range(0, rows.size()), unused -> true);
    }

    /**
     * Returns the index of the last appointment row, or {@code OptionalInt#empty()} if the week has none.
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
     * Returns the index of the row of {@code appointment}, or {@code OptionalInt#empty()} if it is not on this
     * page. The patient's other details may have changed since, as {@link PatientAppointment#isSameEntry} allows.
     */
    public OptionalInt indexOf(PatientAppointment appointment) {
        return findEntry(IntStream.range(0, rows.size()),
                i -> ((Entry) rows.get(i)).appointment().isSameEntry(appointment));
    }

    /**
     * Returns the index of the appointment row to select when nothing is selected yet: in the week of
     * {@code today}, the first appointment from today on, and in any other week, its first appointment.
     * Falls back to the first appointment of the week, and returns {@code OptionalInt#empty()} if it has none.
     */
    public OptionalInt defaultEntryIndex(LocalDate today) {
        if (week.contains(today)) {
            OptionalInt fromToday = findEntry(IntStream.range(0, rows.size()),
                    i -> !((Entry) rows.get(i)).appointment().getAppointment().getDate().isBefore(today));
            if (fromToday.isPresent()) {
                return fromToday;
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
