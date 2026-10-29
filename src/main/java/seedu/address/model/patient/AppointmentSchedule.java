package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A patient's appointments, numbered from 1 in date order: by date, then by time, with an appointment that has no
 * time first on its date. Appointments at the same date and time keep the order in which they were added or moved
 * there. The numbers change whenever an appointment is added, moved or removed.
 * Guarantees: immutable; no nulls; the appointments are numbered 1, 2, 3 and so on in date order.
 */
public class AppointmentSchedule {

    public static final String MESSAGE_DUPLICATE_NUMBER = "A patient's appointment numbers must be unique.";

    /** Date order, with numbered appointments in number order before unnumbered ones at the same date and time. */
    private static final Comparator<Appointment> DATE_ORDER = Comparator.comparing(Appointment::getDate)
            .thenComparing(appointment -> appointment.getTime().orElse(null),
                    Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(appointment -> appointment.isNumbered() ? appointment.getNumber() : null,
                    Comparator.nullsLast(Comparator.naturalOrder()));

    private static final AppointmentSchedule EMPTY = new AppointmentSchedule(List.of());

    private final List<Appointment> appointments;

    /**
     * Numbers {@code appointments} from 1 in date order. At the same date and time, numbered appointments keep their
     * number order, and unnumbered ones come after them, in their order in {@code appointments}.
     *
     * @throws IllegalArgumentException if explicit numbers are duplicated.
     */
    public AppointmentSchedule(Collection<Appointment> appointments) {
        requireNonNull(appointments);
        Set<Integer> used = new HashSet<>();
        for (Appointment appointment : appointments) {
            requireNonNull(appointment);
            if (appointment.isNumbered()) {
                checkArgument(used.add(appointment.getNumber()), MESSAGE_DUPLICATE_NUMBER);
            }
        }
        // The sort is stable, so unnumbered appointments at the same date and time keep their input order
        List<Appointment> ordered = appointments.stream().sorted(DATE_ORDER).toList();
        List<Appointment> numbered = new ArrayList<>();
        for (int i = 0; i < ordered.size(); i++) {
            numbered.add(ordered.get(i).withoutNumber().withNumber(i + 1));
        }
        this.appointments = List.copyOf(numbered);
    }

    /**
     * Returns an {@code AppointmentSchedule} with no appointments.
     */
    public static AppointmentSchedule empty() {
        return EMPTY;
    }

    /**
     * Returns a copy of these appointments with {@code appointment} added, after the appointments already at its date
     * and time. Its own number, if any, is ignored. {@link #getLastAt} finds it in the copy.
     */
    public AppointmentSchedule add(Appointment appointment) {
        requireNonNull(appointment);
        List<Appointment> updated = new ArrayList<>(appointments);
        updated.add(appointment.withoutNumber());
        return new AppointmentSchedule(updated);
    }

    /**
     * Returns a copy of these appointments with {@code target} replaced by {@code replacement}. At the same date and
     * time as {@code target}, {@code replacement} keeps its place and number. At another, it comes after the
     * appointments already there, and {@link #getLastAt} finds it in the copy.
     *
     * @throws IllegalArgumentException if {@code target} is not one of these appointments.
     */
    public AppointmentSchedule replace(Appointment target, Appointment replacement) {
        requireAllNonNull(target, replacement);
        Appointment stored = find(target).orElseThrow(() ->
                new IllegalArgumentException("The appointment is not in this schedule."));
        Appointment placed = replacement.hasSameDateTime(stored)
                ? replacement.withoutNumber().withNumber(stored.getNumber())
                : replacement.withoutNumber();
        List<Appointment> updated = appointments.stream()
                .map(appointment -> appointment.isSameAppointment(stored) ? placed : appointment)
                .toList();
        return new AppointmentSchedule(updated);
    }

    /**
     * Returns the last of these appointments at the date and time of {@code appointment}, which is the one added or
     * moved there last.
     *
     * @throws java.util.NoSuchElementException if none of these appointments is at that date and time.
     */
    public Appointment getLastAt(Appointment appointment) {
        return findAtDateTime(appointment).getLast();
    }

    /**
     * Returns a copy of these appointments without {@code appointment}, in which the appointments after it move up
     * one number. Removing an appointment that is not one of these changes nothing.
     */
    public AppointmentSchedule remove(Appointment appointment) {
        requireNonNull(appointment);
        List<Appointment> updated = appointments.stream().filter(stored -> !stored.isSameAppointment(appointment))
                .toList();
        return new AppointmentSchedule(updated);
    }

    /**
     * Returns whether one of these appointments has the number of {@code appointment}, whatever its date, time or
     * details.
     */
    public boolean contains(Appointment appointment) {
        requireNonNull(appointment);
        return appointments.stream().anyMatch(appointment::isSameAppointment);
    }

    /**
     * Returns the appointment with the number of {@code appointment}, with its stored date, time and details, if
     * there is one.
     */
    public Optional<Appointment> find(Appointment appointment) {
        requireNonNull(appointment);
        return appointments.stream().filter(appointment::isSameAppointment).findFirst();
    }

    /**
     * Returns the appointment with {@code number}, if present.
     */
    public Optional<Appointment> findByNumber(int number) {
        checkArgument(number > 0, Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        return appointments.stream().filter(appointment -> appointment.getNumber() == number).findFirst();
    }

    /**
     * Returns every appointment at the exact date and optional time, in number order.
     */
    public List<Appointment> findAtDateTime(Appointment target) {
        requireNonNull(target);
        return appointments.stream().filter(target::hasSameDateTime).toList();
    }

    /**
     * Returns the appointments on {@code date}, whatever their time, in chronological order.
     */
    public List<Appointment> getOnDate(LocalDate date) {
        requireNonNull(date);
        return appointments.stream().filter(appointment -> appointment.getDate().equals(date)).toList();
    }

    /**
     * Returns true if there are no appointments.
     */
    public boolean isEmpty() {
        return appointments.isEmpty();
    }

    /**
     * Returns the earliest appointment on or after {@code date}, or {@code Optional#empty()} if there is none.
     * Only dates are compared, so every appointment on {@code date} counts, whatever its time.
     */
    public Optional<Appointment> getNextOnOrAfter(LocalDate date) {
        requireNonNull(date);
        return appointments.stream().filter(appointment -> !appointment.getDate().isBefore(date)).findFirst();
    }

    /**
     * Returns the appointments as an unmodifiable list in chronological order.
     */
    public List<Appointment> asList() {
        return Collections.unmodifiableList(appointments);
    }

    @Override
    public String toString() {
        return appointments.stream().map(Appointment::toString).collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AppointmentSchedule otherSchedule)) {
            return false;
        }

        return appointments.equals(otherSchedule.appointments);
    }

    @Override
    public int hashCode() {
        return appointments.hashCode();
    }

}
