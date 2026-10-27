package seedu.address.model.patient;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A patient's appointments, sorted by date, optional time and number.
 * Guarantees: immutable; no nulls; every appointment has a unique positive number.
 */
public class AppointmentSchedule {

    public static final String MESSAGE_DUPLICATE_NUMBER = "A patient's appointment numbers must be unique.";
    public static final String MESSAGE_NUMBER_EXHAUSTED = "No appointment number is available for this patient.";

    private static final AppointmentSchedule EMPTY = new AppointmentSchedule(List.of());

    private final List<Appointment> appointments;

    /**
     * Preserves explicit numbers and assigns missing ones in chronological order, using the lowest unused
     * positive numbers. Equal timestamps retain their input order when assigning numbers.
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
        List<Appointment> numbered = new ArrayList<>();
        int candidate = 1;
        for (Appointment appointment : appointments.stream().sorted().toList()) {
            if (appointment.isNumbered()) {
                numbered.add(appointment);
                continue;
            }
            while (used.contains(candidate)) {
                checkArgument(candidate < Integer.MAX_VALUE, MESSAGE_NUMBER_EXHAUSTED);
                candidate++;
            }
            numbered.add(appointment.withNumber(candidate));
            used.add(candidate);
        }
        this.appointments = numbered.stream().sorted().toList();
        assert this.appointments.stream().allMatch(Appointment::isNumbered);
    }

    /**
     * Returns an {@code AppointmentSchedule} with no appointments.
     */
    public static AppointmentSchedule empty() {
        return EMPTY;
    }

    /**
     * Returns a copy of these appointments with {@code appointment} added.
     * An unnumbered input receives the highest remaining number plus one.
     */
    public AppointmentSchedule add(Appointment appointment) {
        requireNonNull(appointment);
        List<Appointment> updated = new ArrayList<>(appointments);
        updated.add(appointment.isNumbered() ? appointment : appointment.withNumber(getNextNumber()));
        return new AppointmentSchedule(updated);
    }

    /**
     * Removes the appointment with the given identity without renumbering the others.
     */
    public AppointmentSchedule remove(Appointment appointment) {
        requireNonNull(appointment);
        List<Appointment> updated = appointments.stream().filter(stored -> !stored.isSameAppointment(appointment))
                .toList();
        return new AppointmentSchedule(updated);
    }

    /**
     * Returns whether an appointment with this number exists, regardless of its date/time or details.
     */
    public boolean contains(Appointment appointment) {
        requireNonNull(appointment);
        return appointments.stream().anyMatch(appointment::isSameAppointment);
    }

    /**
     * Looks up a numbered appointment by identity, returning the stored date/time and details.
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
     * Returns the highest remaining number plus one, or 1 for an empty schedule.
     *
     * @throws IllegalArgumentException if the highest number cannot be incremented.
     */
    public int getNextNumber() {
        int highest = appointments.stream().mapToInt(Appointment::getNumber).max().orElse(0);
        checkArgument(highest < Integer.MAX_VALUE, MESSAGE_NUMBER_EXHAUSTED);
        return highest + 1;
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
